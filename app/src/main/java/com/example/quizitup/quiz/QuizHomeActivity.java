package com.example.quizitup.quiz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.quizitup.LoginAdapter;
import com.example.quizitup.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class QuizHomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    QuizHomeAdapter quizHomeAdapter;

    boolean isStudent;
    String quizCode;
    int statusCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));
        actionBar.setTitle("Quiz-1");

        quizCode = getIntent().getExtras().getString("code");
        isStudent = getIntent().getExtras().getBoolean("isStudent");
        statusCode = getIntent().getExtras().getInt("status");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.container_pager);

        FragmentManager fm = getSupportFragmentManager();
        quizHomeAdapter = new QuizHomeAdapter(fm,getLifecycle(),statusCode,isStudent,quizCode);
        viewPager2.setAdapter(quizHomeAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Participants"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
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
}