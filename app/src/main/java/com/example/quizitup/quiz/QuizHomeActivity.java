package com.example.quizitup.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quizitup.LoginAdapter;
import com.example.quizitup.R;
import com.example.quizitup.view_analysis.ViewAnalysisActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuizHomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    QuizHomeAdapter quizHomeAdapter;

    boolean isStudent;
    String quizCode,date,uid;
    int statusCode;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));
        actionBar.setTitle("Quiz-1");

        quizCode = getIntent().getExtras().getString("code");
        isStudent = getIntent().getExtras().getBoolean("isStudent");
        date = getIntent().getExtras().getString("date");
        statusCode = getIntent().getExtras().getInt("status");

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference().child("Quiz").child(quizCode);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.container_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Participants"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm = getSupportFragmentManager();
                quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        statusCode = Integer.parseInt(snapshot.child("Status").getValue().toString());
                        String endTime = snapshot.child("End Time").getValue().toString();
                        int isCompleted=0;
                        if (isStudent)
                             isCompleted = Integer.parseInt(snapshot.child("Participants").child(uid).child("isCompleted").getValue().toString());
                        if (statusCode != 4) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                            try {
                                String time = timeFormat.format(c.getTime());
                                endTime = timeFormat.format(timeFormat.parse(endTime));
                                Date now = timeFormat.parse(time);
                                Date end = timeFormat.parse(endTime);
                                if (now.compareTo(end) >= 0) {
                                    quizRef.child("Status").setValue(4);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        quizHomeAdapter = new QuizHomeAdapter(fm,getLifecycle(),statusCode,isStudent,quizCode,isCompleted);
                        viewPager2.setAdapter(quizHomeAdapter);
                        viewPager2.setCurrentItem(tab.getPosition());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = getSupportFragmentManager();
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusCode = Integer.parseInt(snapshot.child("Status").getValue().toString());
                String endTime = snapshot.child("End Time").getValue().toString();
                int isCompleted = 0;
                if (isStudent)
                    isCompleted = Integer.parseInt(snapshot.child("Participants").child(uid).child("isCompleted").getValue().toString());
                if (statusCode != 4) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                    try {
                        String time = timeFormat.format(c.getTime());
                        endTime = timeFormat.format(timeFormat.parse(endTime));
                        Date now = timeFormat.parse(time);
                        Date end = timeFormat.parse(endTime);
                        if (now.compareTo(end) >= 0) {
                            quizRef.child("Status").setValue(4);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                quizHomeAdapter = new QuizHomeAdapter(fm,getLifecycle(),statusCode,isStudent,quizCode,isCompleted);
                viewPager2.setAdapter(quizHomeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isStudent && statusCode==4) {
            getMenuInflater().inflate(R.menu.menu_teacher,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.download) {

        } else if (id == R.id.analysis) {
            Intent intent = new Intent(this, ViewAnalysisActivity.class);
            intent.putExtra("code",quizCode);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}