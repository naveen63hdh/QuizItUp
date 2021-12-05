package com.example.quizitup.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.SplashActivity;
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
import java.util.Calendar;
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

    @Override
    protected void onResume() {
        super.onResume();

        quizHomeModels = new ArrayList<>();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_refresh,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.refresh) {
            onResume();
            return true;
        } else if(item.getItemId()==R.id.signout) {
            auth.signOut();
            Intent i = new Intent(HomeActivity.this, SplashActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
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
                                    String name, startTime, endTime, status, created_by;
                                    boolean isStudent;
                                    int status_code;
                                    name = quiz_snapshot.child("quiz name").getValue().toString();
                                    startTime = quiz_snapshot.child("Start Time").getValue().toString();
                                    endTime = quiz_snapshot.child("End Time").getValue().toString();
//                                    endJoinTime = quiz_snapshot.child("End Joining Time").getValue().toString();
                                    status_code = Integer.parseInt(quiz_snapshot.child("Status").getValue().toString());
                                    created_by = quiz_snapshot.child("Created by").getValue().toString();
                                    if (created_by.equals(uid))
                                        isStudent = false;
                                    else
                                        isStudent = true;
                                    String quiz_date = decodeDate(date);
                                    status_code = updateStatus(status_code, quiz_date, startTime, endTime);
                                    quizReference.child(code).child("Status").setValue(status_code);
                                    status = status_map.get(status_code);
                                    quizHomeModels.add(new QuizHomeModel(isStudent, name, code, startTime, endTime, quiz_date, status, status_code));
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

    private int updateStatus(int status_code, String quiz_date, String startTime, String endTime) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        today = decodeToDate(encodeDate(today));
        Date quiz = decodeToDate(quiz_date);
        Date now = null, start = null, end = null, endJoin = null;

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        try {
            String time = timeFormat.format(c.getTime());
            startTime = timeFormat.format(timeFormat.parse(startTime));
            endTime = timeFormat.format(timeFormat.parse(endTime));
//            endJoinTime = timeFormat.format(timeFormat.parse(endJoinTime));
            now = timeFormat.parse(time);
            start = timeFormat.parse(startTime);
            end = timeFormat.parse(endTime);
//            endJoin = timeFormat.parse(endJoinTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (today.compareTo(quiz) > 0) {
            if (status_code == 1) {
                status_code = 4;
            }
        } else if (today.compareTo(quiz) == 0) {
//            if (now.compareTo(start)>=0 && status_code<2)
//                status_code = 2;
//            else
            if (status_code==1) {
                if (now.compareTo(start) >= 0)
                    status_code = 2;
                if (now.compareTo(end) >= 0)
                    status_code = 4;
            }
        }


        Log.i("TIME_FORMATTER_Start", start.toString());
        Log.i("TIME_FORMATTER_Now", now.toString());
        return status_code;
    }

    private String encodeDate(Date date) {
        String d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
        return d;
    }

    private String decodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private Date decodeToDate(String date) {
        Date d = null;
        try {
            d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}