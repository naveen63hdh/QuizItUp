package com.example.quizitup.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizitup.QuizDetailsActivity;
import com.example.quizitup.R;
import com.example.quizitup.mail.GMailSender;
import com.example.quizitup.question.QuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;

public class JoinClassroom extends AppCompatActivity {

    EditText classCodeTxt, unameTxt;
    Button fetchBtn, joinBtn;
    TextView noClassFound;

    boolean isClassFound = false, isOpen;
    long count = 0;
    int size = 0;
    int qcount = 0;
    FirebaseDatabase database;
    DatabaseReference classRef, userRef, quizRef, uRef;
    FirebaseAuth auth;
    String uid, classCode, className, creator, uname, createdBy;

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
        uRef = database.getReference("Users").child(uid);
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
                                        createdBy = cSnapshot.child("createdBy").getValue().toString();
                                        unameTxt.setHint(cSnapshot.child("hint").getValue().toString());

                                        isOpen = Boolean.parseBoolean(cSnapshot.child("isOpen").getValue().toString());

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
                    if (isOpen) {
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
                        Map<String, Object> requestMap = new HashMap<>();
                        requestMap.put("uname",unameTxt.getText().toString());
                        requestMap.put("accepted",false);
                        requestMap.put("ignore",false);
                        classRef.child(classCode).child("Request_Participant").child(uid).updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference("Users").child(createdBy).child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String recipient = snapshot.getValue().toString();
                                        notifyUsers(recipient);
                                        Toast.makeText(JoinClassroom.this, "Sent Request to join the Classroom", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });
                    }

                    } else {
                        Toast.makeText(JoinClassroom.this, "Please Fetch valid quiz and type user name", Toast.LENGTH_SHORT).show();
                    }

            }

        });
    }

    private void joinAllQuiz() {

        progressDialog = ProgressDialog.show(JoinClassroom.this, "Please Wait", "Joining all quizzes inside classroom");
        classRef.child(classCode).child("Quiz").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    ArrayList<String> quizList = new ArrayList<>();
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

                        quizList.add(key);
//                        participantMap.put(key, tempMap2);

                        quizRef.child(key).child("Participants").updateChildren(tempMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    count++;
                                    if(count >= snapshot.getChildrenCount()) {
                                        progressDialog.dismiss();
                                        uploadAllQuestions(quizList);
//                                        Toast.makeText(JoinClassroom.this, "Joined " + className + " Successfully", Toast.LENGTH_SHORT).show();
//                                        finish();
                                    }

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(JoinClassroom.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                }

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


    void notifyUsers(String recipients) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("quizitup.official@gmail.com",
                            "Quizitup@123");
                    sender.sendMail("Request to join", "An user named "+unameTxt.getText().toString()+" wants to join your course "+className+". Please check it in quizitup",
                            "quizitup.official@gmail.com", recipients);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }

    private void uploadAllQuestions(ArrayList<String> quizList) {
        progressDialog = ProgressDialog.show(JoinClassroom.this, "Please Wait", "Creating records for all quiz");
        size = quizList.size();
        if (size==0)
            progressDialog.dismiss();
        for(String key : quizList) {
            quizRef.child(key).child("Date").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    quizRef.child(key).child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
                        String date = snapshot.getValue().toString();

                        @Override
                        public void onDataChange(@NonNull DataSnapshot quizSnap) {
                            HashMap<String,Object> questions = new HashMap<>();
                            for (DataSnapshot questionSnap : quizSnap.getChildren()) {
                                String qno,ans;
                                Double marks;

                                qno = questionSnap.getKey();
                                ans = questionSnap.child("ans").getValue().toString();
                                marks = Double.valueOf(questionSnap.child("marks").getValue().toString());
                                QuestionModel model = new QuestionModel(marks,ans,"NULL",0);
                                questions.put(qno,model);
                            }
                            date = encodeDate(date);
                            uRef.child("Quiz").child(date).child(key).updateChildren(questions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    qcount++;
                                    if (qcount==size && task.isSuccessful()) {
                                        Toast.makeText(JoinClassroom.this, "Joined Classroom successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public String encodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("yyyy_MM_dd",Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}