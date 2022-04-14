package com.example.quizitup.classroom.MyClass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.quizitup.R;
import com.example.quizitup.classroom.Fragments.CourseParticipantsFragment;
import com.example.quizitup.classroom.Fragments.CourseQuizFragment;
import com.example.quizitup.classroom.Fragments.CourseResultsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MyClassroomActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    String classId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classroom);
        //        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        classId = getIntent().getStringExtra("classId");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.all_quiz);
        CourseQuizFragment courseQuizFragment = new CourseQuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString("classId",classId);
        courseQuizFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, courseQuizFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if (item.getItemId()==R.id.delete) {
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setMessage("Are you sure you want to Delete the course ?")
                   .setCancelable(false)
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           finishActivity(1);
                       }
                   }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();
               }
           });
           AlertDialog alertDialog = builder.create();
           alertDialog.setTitle("Delete Course");
           alertDialog.show();
       }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_quiz:
                CourseQuizFragment courseQuizFragment = new CourseQuizFragment();
                Bundle bundle = new Bundle();
                bundle.putString("classId",classId);
                courseQuizFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, courseQuizFragment).commit();
                return true;
            case R.id.participant:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new CourseParticipantsFragment()).commit();
                return true;
            case R.id.result:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new CourseResultsFragment()).commit();
                return true;
        }
        return false;
    }


}