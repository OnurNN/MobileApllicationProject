package com.example.harmony2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoutineFragment extends Fragment {

    private DatabaseReference databaseReference;
    private String userId;

    private EditText taskEditText;
    private RoutineTaskAdapter taskAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        }

        taskEditText = view.findViewById(R.id.editTextTask);
        Button addButton = view.findViewById(R.id.buttonAdd);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new RoutineTaskAdapter(databaseReference);
        recyclerView.setAdapter(taskAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(taskAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskEditText.getText().toString().trim();
                if (!task.isEmpty()) {
                    addTaskToFirebase(task);
                    taskEditText.getText().clear();
                } else {
                    Toast.makeText(getContext(), "Please enter a task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadTasksFromFirebase();

        return view;
    }

    private void addTaskToFirebase(String task) {
        String taskKey = databaseReference.child("tasks").push().getKey();
        if (taskKey != null) {
            databaseReference.child("tasks").child(taskKey).setValue(new RoutineTask(task, false));
        }
    }

    private void loadTasksFromFirebase() {
        databaseReference.child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<RoutineTask> tasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RoutineTask task = snapshot.getValue(RoutineTask.class);
                    if (task != null) {
                        task.setKey(snapshot.getKey()); // Set the key
                        tasks.add(task);
                    }
                }
                taskAdapter.setTasks(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

