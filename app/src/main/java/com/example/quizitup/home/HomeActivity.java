package com.example.quizitup.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.quizitup.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton moreFab;
    RecyclerView recyclerView;
    ArrayList<QuizHomeModel> quizHomeModels;
    FirebaseDatabase database;
    DatabaseReference userReference, quizReference, statusReference;
    FirebaseAuth auth;
    String uid;
    HashMap<Integer, String> status_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        moreFab = findViewById(R.id.more_btn);
        recyclerView = findViewById(R.id.contentRecycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        quizHomeModels = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("Users");
        quizReference = database.getReference("Quiz");
        statusReference = database.getReference("Status");
        uid = auth.getUid();

//        Get Status codes and its values
        status_map = new HashMap<>();
        statusReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot statusSnapshot : snapshot.getChildren()) {
                        int code = Integer.parseInt(statusSnapshot.getKey());
                        String status = statusSnapshot.getValue().toString();
                        status_map.put(code, status);
                    }
                    populateRecycler();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Dummy values for quiz adapter


//        quizHomeModels.add(new QuizHomeModel("O","Quiz-1","ABC123","5:00 PM","6:00 PM","10/10/2021","Yet to Start"));
//        quizHomeModels.add(new QuizHomeModel("P","Quiz-2","A1B2C3","10:00 AM","10:30 PM","10/10/2021","Yet to Start"));
//        quizHomeModels.add(new QuizHomeModel("O","Quiz-3","CBA321","5:00 PM","6:00 PM","9/10/2021","Ended"));
//        quizHomeModels.add(new QuizHomeModel("P","Quiz-4","C3B2A1","10:00 PM","10:30 PM","9/10/2021","Ended"));

//        HomeAdapter homeAdapter = new HomeAdapter(quizHomeModels,this);
//        recyclerView.setAdapter(homeAdapter);

        moreFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreDialog dialog = new MoreDialog(HomeActivity.this);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.BOTTOM | Gravity.END;
                wmlp.x = 100;   //x position
                wmlp.y = 150;   //y position
                dialog.show();
            }
        });
    }

    private void populateRecycler() {

        userReference.child(uid).child("Quiz").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Iterable<DataSnapshot> quizzes_snapshot = snapshot.getChildren();
                    for (DataSnapshot quizSnap : quizzes_snapshot) {
                        String date = quizSnap.getKey();
                        Iterable<DataSnapshot> quizSubTree = quizSnap.getChildren();
                        for (DataSnapshot quizByDate : quizSubTree) {
                            String code = quizByDate.getKey();
                            quizReference.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot quiz_snapshot) {
                                    String name, startTime, endTime, status, created_by, type;
                                    int  status_code;
                                    name = quiz_snapshot.child("quiz name").getValue().toString();
                                    startTime = quiz_snapshot.child("Start Time").getValue().toString();
                                    endTime = quiz_snapshot.child("End Time").getValue().toString();
                                    status_code = Integer.parseInt(quiz_snapshot.child("Status").getValue().toString());
                                    created_by = quiz_snapshot.child("Created by").getValue().toString();
                                    if (created_by.equals(uid))
                                        type = "O";
                                    else
                                        type = "P";
                                    status = status_map.get(status_code);
                                    String quiz_date = decodeDate(date);
                                    quizHomeModels.add(new QuizHomeModel(type, name, code, startTime, endTime, quiz_date, status));
                                    HomeAdapter homeAdapter = new HomeAdapter(quizHomeModels, HomeActivity.this);
                                    recyclerView.setAdapter(homeAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String decodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}