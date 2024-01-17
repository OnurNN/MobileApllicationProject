package com.example.harmony2.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harmony2.Model.ToDoModel;
import com.example.harmony2.NewTask;
import com.example.harmony2.R;
import com.example.harmony2.RoutineFragment;
import com.example.harmony2.ToDo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
//inanç
public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> toDoList;
    private ToDo activity;
    private FirebaseFirestore firestore;

    public ToDoAdapter(ToDo toDoActivity , List<ToDoModel> todoList){
        this.toDoList = todoList;
        activity = toDoActivity;
    }

    @NonNull
    @Override
    public ToDoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.task, parent, false);
        firestore = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }

    public Context getContext(){
        return activity;
    }
    public void deleteTask(int position) {
        ToDoModel toDoModel = toDoList.get(position);
        firestore.collection("task").document(toDoModel.TaskId).delete();
        toDoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editTask(int position) {
        ToDoModel toDoModel = toDoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task" , toDoModel.getTask());
        bundle.putString("due" , toDoModel.getDue());
        bundle.putString("id" , toDoModel.TaskId);

        NewTask newTask = new NewTask();
        newTask.setArguments(bundle);
        newTask.show(activity.getSupportFragmentManager() , newTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.MyViewHolder holder, int position) {
        ToDoModel toDoModel = toDoList.get(position);

        holder.tCheckBox.setText(toDoModel.getTask());

        holder.tDueDate.setText("Due On " + toDoModel.getDue());

        holder.tCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        holder.tCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firestore.collection("task").document(toDoModel.TaskId).update("status" , 1);
                }else{
                    firestore.collection("task").document(toDoModel.TaskId).update("status" , 0);
                }
            }
        });
    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tDueDate;
        CheckBox tCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tDueDate = itemView.findViewById(R.id.dueDate);
            tCheckBox = itemView.findViewById(R.id.checkBox);

        }
    }
}
