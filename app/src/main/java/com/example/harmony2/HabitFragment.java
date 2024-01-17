
package com.example.harmony2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HabitFragment extends Fragment {

    private ProgressBar waterProgressBar;
    private Button addWaterButton;
    private Button resetButton;

    private int waterIntake = 0;
    private FirebaseUser currentUser;
    private DatabaseReference userWaterIntakeRef;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit, container, false);

        waterProgressBar = view.findViewById(R.id.waterProgressBar);
        addWaterButton = view.findViewById(R.id.addWaterButton);
        resetButton = view.findViewById(R.id.resetButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userWaterIntakeRef = FirebaseDatabase.getInstance().getReference().child("waterIntake").child(userId);
            userWaterIntakeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        waterIntake = snapshot.getValue(Integer.class);
                        updateProgressBar();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        addWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetWaterIntake();
            }
        });

        return view;
    }

    private void addWater() {
        if (waterIntake < 15) {
            waterIntake++;
            updateFirebaseWaterIntake();
            updateProgressBar();
        }
    }

    private void resetWaterIntake() {
        waterIntake = 0;
        updateFirebaseWaterIntake();
        updateProgressBar();
    }

    private void updateFirebaseWaterIntake() {
        if (currentUser != null && userWaterIntakeRef != null) {
            userWaterIntakeRef.setValue(waterIntake);
        }
    }

    private void updateProgressBar() {
        waterProgressBar.setProgress(waterIntake);
    }
}
