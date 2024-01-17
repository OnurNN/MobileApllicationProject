package com.example.harmony2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//ali inanç
public class ProfileFragment extends Fragment {

    private Button updateBtn;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private String CurrentUserID;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserID = firebaseAuth.getUid();
        userRef = database.getReference("users").child(CurrentUserID);

        final TextView nameTextView = view.findViewById(R.id.profileNameTextView);
        final TextView surnameTextView = view.findViewById(R.id.profileSurnameTextView);
        final TextView emailTextView = view.findViewById(R.id.profileEmailTextView);
        final TextView phoneTextView = view.findViewById(R.id.profilePhoneTextView);


        updateBtn = view.findViewById(R.id.updateProfile);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment epf = new EditProfileFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.profileContainer, epf);
                transaction.addToBackStack(null);  // Optional: Adds the transaction to the back stack
                transaction.commit();
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);

                    // Set values in TextViews
                    nameTextView.setText("Name: " + name);
                    surnameTextView.setText("Surname: " + surname);
                    emailTextView.setText("Email: " + email);
                    phoneTextView.setText("Phone: " + phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(getActivity(), "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}