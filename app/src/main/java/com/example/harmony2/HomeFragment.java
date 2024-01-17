package com.example.harmony2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//onur
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView motivationalSentence;
    private List<String> sentences;
    private Handler handler;
    private Runnable updateSentenceRunnable;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        motivationalSentence = view.findViewById(R.id.motivationalSentence);

        // Initialize your list of motivational sentences
        sentences = new ArrayList<>();
        sentences.add("Believe in yourself and all that you are.");
        sentences.add("Dream big, work hard, stay focused.");
        sentences.add("Rise above, stay positive.");
        sentences.add("Make it happen.");
        sentences.add("Inspire and be inspired.");
        sentences.add("Create your own sunshine.");
        sentences.add("Be fearless, stay strong.");
        sentences.add("I'm possible");
        sentences.add("Stay true to yourself.");

        // Add more sentences as needed

        // Initialize handler and runnable for updating sentences
        handler = new Handler();
        updateSentenceRunnable = new Runnable() {
            @Override
            public void run() {
                fadeOutAndChangeSentence();
                handler.postDelayed(this, 5000); // Repeat after 10 seconds
            }
        };

        // Start updating sentences
        handler.postDelayed(updateSentenceRunnable, 5000);

        // Show the initial sentence immediately
        updateMotivationalSentence();

        return view;
    }

    private void fadeOutAndChangeSentence() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(motivationalSentence, "alpha", 1f, 0f);
        fadeOut.setDuration(1000); // 1 second duration for fading out

        fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                motivationalSentence.setAlpha(value);
            }
        });

        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                super.onAnimationEnd(animation);
                updateMotivationalSentence();
                fadeIn(); // Fade in the new sentence
            }
        });

        fadeOut.start();
    }

    private void fadeIn() {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(motivationalSentence, "alpha", 0f, 1f);
        fadeIn.setDuration(1000); // 1 second duration for fading in
        fadeIn.start();
    }

    private void updateMotivationalSentence() {
        // Choose a random sentence from the list
        Random random = new Random();
        int index = random.nextInt(sentences.size());
        String newSentence = sentences.get(index);

        // Update the TextView
        motivationalSentence.setText(newSentence);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the handler when the fragment is destroyed to prevent memory leaks
        handler.removeCallbacks(updateSentenceRunnable);
    }
}