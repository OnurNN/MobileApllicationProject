package com.example.harmony2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harmony2.Adapter.ToDoAdapter;
import com.example.harmony2.Model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
//onur
public class ToDo extends AppCompatActivity implements OnDialogCloseListener{

    //t = this
    //onur
    private RecyclerView tRV;
    private FloatingActionButton tFAB;
    private FirebaseFirestore firestore;
    private ToDoAdapter toDoAdapter;
    private List<ToDoModel> tList;
    private Query toDoQuery;
    private ListenerRegistration toDoListenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        tRV = findViewById(R.id.toDoRecyclerView);
        tFAB = findViewById(R.id.toDoFloatingActionButton);
        firestore = FirebaseFirestore.getInstance();

        tRV.setHasFixedSize(true);
        tRV.setLayoutManager(new LinearLayoutManager(ToDo.this));

        tFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTask.addNewTask().show(getSupportFragmentManager(), NewTask.TAG);
            }
        });

        tList = new ArrayList<>();
        toDoAdapter = new ToDoAdapter(ToDo.this , tList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(tRV);

        showData();
        tRV.setAdapter(toDoAdapter);
    }

    private void showData() {
        toDoQuery = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
        toDoListenerRegistration = toDoQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange: value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                        tList.add(toDoModel);
                        toDoAdapter.notifyDataSetChanged();
                    }
                }
                toDoListenerRegistration.remove();
            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        tList.clear();
        showData();
        toDoAdapter.notifyDataSetChanged();
    }
}