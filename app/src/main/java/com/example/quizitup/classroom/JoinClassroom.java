package com.example.quizitup.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizitup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class JoinClassroom extends AppCompatActivity {

    EditText classCodeTxt, unameTxt;
    Button fetchBtn, joinBtn;
    TextView noClassFound;

    boolean isClassFound = false;
    long count = 0;
    FirebaseDatabase database;
    DatabaseReference classRef, userRef, quizRef;
    FirebaseAuth auth;
    String uid, classCode, className, creator, uname;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_classroom);
//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        classCodeTxt = findViewById(R.id.classroomCode);
        unameTxt = findViewById(R.id.usernameTxt);
        fetchBtn = findViewById(R.id.fetchQuiz);
        joinBtn = findViewById(R.id.joinQuiz);
        noClassFound = findViewById(R.id.no_class_found);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        classRef = database.getReference("Classrooms");
        quizRef = database.getReference("Quiz");
        userRef = database.getReference("Users").child(uid).child("Classroom");
        unameTxt.setEnabled(false);


        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(JoinClassroom.this, "Please wait", "Fetching quiz data");
                classCode = classCodeTxt.getText().toString();
                userRef.child(classCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            noClassFound.setText("User Already Joined Course");
                            noClassFound.setVisibility(View.VISIBLE);
                            isClassFound = false;
                            unameTxt.setEnabled(false);
                            progressDialog.dismiss();
                        } else {
                            classRef.child(classCode).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot cSnapshot) {
                                    if (cSnapshot.exists()) {
                                        className = cSnapshot.child("name").getValue().toString();
                                        creator = cSnapshot.child("creator").getValue().toString();
                                        unameTxt.setHint(cSnapshot.child("hint").getValue().toString());


                                        isClassFound = true;
                                        noClassFound.setVisibility(View.GONE);
                                        unameTxt.setEnabled(true);

                                    } else {
                                        isClassFound = false;
                                        unameTxt.setEnabled(false);
                                        noClassFound.setText("No Course Found");
                                        noClassFound.setVisibility(View.VISIBLE);
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClassFound && !unameTxt.getText().toString().equals("")) {
                    progressDialog = ProgressDialog.show(JoinClassroom.this, "Please Wait", "Joining the classroom");


                    HashMap<String, Object> userMapList = new HashMap<>();
                    userMapList.put("name", className);
                    userMapList.put("uname", creator);

                    HashMap<String, Object> participantMap = new HashMap<>();
                    participantMap.put("Name", unameTxt.getText().toString());
                    participantMap.put("isCompleted", 0);
                    participantMap.put("score", 0);

                    HashMap<String, Object> participantList = new HashMap<>();
                    uname = unameTxt.getText().toString();
                    participantList.put("uname", uname);

                    classRef.child(classCode).child("Participants").child(uid).updateChildren(participantList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userRef.child(classCode).updateChildren(userMapList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            joinAllQuiz();
//                                            Toast.makeText(JoinClassroom.this, "Joined " + className + " Successfully", Toast.LENGTH_SHORT).show();

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(JoinClassroom.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(JoinClassroom.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(JoinClassroom.this, "Please Fetch valid quiz and type user name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void joinAllQuiz() {

        progressDialog = ProgressDialog.show(JoinClassroom.this, "Please Wait", "Joining the classroom");
        classRef.child(classCode).child("Quiz").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
//                    HashMap<String, Object> participantMap = new HashMap<>();
                    String uid = auth.getUid();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        HashMap<String, Object> tempMap = new HashMap<>();
                        HashMap<String, Object> tempMap2 = new HashMap<>();
                        tempMap.put("Name", unameTxt.getText().toString());
                        tempMap.put("isCompleted", 0);
                        tempMap.put("score", 0);
                        String key = snap.getKey();

                        tempMap2.put(uid,tempMap);



//                        participantMap.put(key, tempMap2);

                        quizRef.child(key).child("Participants").updateChildren(tempMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if(count >= snapshot.getChildrenCount()) {
                                        Toast.makeText(JoinClassroom.this, "Joined " + className + " Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    count++;
                                } else {
                                    Toast.makeText(JoinClassroom.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }

                }
                else
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        finish();
    }
}