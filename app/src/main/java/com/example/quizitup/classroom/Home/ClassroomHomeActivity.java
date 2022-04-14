package com.example.quizitup.classroom.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.SplashActivity;
import com.example.quizitup.classroom.Home.adapter.ClassroomHomeAdapter;
import com.example.quizitup.classroom.Models.Classroom;
import com.example.quizitup.home.HomeActivity;
import com.example.quizitup.home.MoreDialog;
import com.example.quizitup.home.QuizHomeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ClassroomHomeActivity extends AppCompatActivity {

    FloatingActionButton moreFab;
    RecyclerView recyclerView;
    ArrayList<Classroom> classroomHomeModels;
    FirebaseDatabase database;
    DatabaseReference userReference, classReference;
    FirebaseAuth auth;
    String uid;
    long count = 0,size;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_home);

        //        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        moreFab = findViewById(R.id.more_btn);
        recyclerView = findViewById(R.id.contentRecycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        classroomHomeModels = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();
        userReference = database.getReference("Users");
//        StorageReference storage = FirebaseStorage.getInstance().getReference("");
        classReference = database.getReference("Classrooms");



        moreFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreDialog dialog = new MoreDialog(ClassroomHomeActivity.this);
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

        progressDialog = ProgressDialog.show(this,"Loading","Please wait loading data...");
        classroomHomeModels = new ArrayList<>();
        count = 0;
        populateRecycler();
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
            Intent i = new Intent(ClassroomHomeActivity.this, SplashActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateRecycler() {

        userReference.child(uid).child("Classroom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    size = snapshot.getChildrenCount();
                    for (DataSnapshot classSnap: snapshot.getChildren()) {
                        String id = classSnap.getKey();
                        String name = classSnap.child("name").getValue().toString();
                        String creator = classSnap.child("uname").getValue().toString();
                        classroomHomeModels.add(new Classroom(id,name,creator));

                    }
                    ClassroomHomeAdapter adapter = new ClassroomHomeAdapter(classroomHomeModels,ClassroomHomeActivity.this);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();


                }
                else
                    progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
}