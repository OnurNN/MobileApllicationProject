package com.example.harmony2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
//onur
public class NewTask extends BottomSheetDialogFragment {

    public static final String TAG = "New Task";

    private TextView tSetDueDate;
    private EditText tEdit;
    private Button tSaveBtn;
    private FirebaseFirestore firestore;
    private Context tContext;
    private String tDueDate;
    private String tDueDateUpdate;
    private String id = "";

    public static NewTask addNewTask() {
        return new NewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tSetDueDate = view.findViewById(R.id.new_taskSetDueTV);
        tEdit = view.findViewById(R.id.new_taskET);
        tSaveBtn = view.findViewById(R.id.new_taskSaveBtn);

        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            tDueDateUpdate = bundle.getString("due");

            tEdit.setText(task);
            tSetDueDate.setText(tDueDateUpdate);

            if (task.length() > 0){
                tSaveBtn.setEnabled(false);
                tSaveBtn.setBackgroundColor(Color.GRAY);
            }
        }

        tEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    tSaveBtn.setEnabled(false);
                    tSaveBtn.setBackgroundColor(Color.GRAY);
                } else {
                    tSaveBtn.setEnabled(true);
                    tSaveBtn.setBackgroundColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tSetDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(tContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        tSetDueDate.setText(dayOfMonth + "/" + month + "/" + year);
                        tDueDate = dayOfMonth + "/" + month +"/"+year;

                    }
                } , YEAR , MONTH , DAY);
                datePickerDialog.show();
            }
        });

        boolean finalIsUpdate = isUpdate;
        tSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = tEdit.getText().toString();

                if (finalIsUpdate){
                    firestore.collection("task").document(id).update("task" , task , "due" , tDueDate);
                    Toast.makeText(tContext, "Task Updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (task.isEmpty()) {
                        Toast.makeText(tContext, "Empty texts are not allowed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", task);
                        taskMap.put("due", tDueDate);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp());

                        firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(tContext, "Task has been saved.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(tContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(tContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                    dismiss();
                }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.tContext = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof  OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}
