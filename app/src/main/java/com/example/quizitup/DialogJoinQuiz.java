package com.example.quizitup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quizitup.question.QuestionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.HashMap;
import java.util.Locale;

public class DialogJoinQuiz  extends Dialog {

    TextInputEditText quizCode;
    Button joinBtn,cancelBtn;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String uid, date,uname;
    DatabaseReference reference,participantRef;

    HashMap<String, QuestionModel> questions;

    public DialogJoinQuiz(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_join_quiz);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        participantRef = database.getReference().child("Quiz");
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();

        quizCode = findViewById(R.id.code_txt);
        joinBtn = findViewById(R.id.join_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        reference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname = snapshot.child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = ProgressDialog.show(getContext(),"Please Wait","Fetching Quiz");
                String code = quizCode.getText().toString().trim();
                /*
                Check if code is there in quiz tree else toast no quiz found
                 */
                reference.child("Quiz").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String quiz = snapshot.child("quiz name").getValue().toString();
                            date = snapshot.child("Date").getValue().toString();
                            String start_time = snapshot.child("Start Time").getValue().toString();
                            String end_time = snapshot.child("End Time").getValue().toString();
                            String end_join_time = snapshot.child("End Joining Time").getValue().toString();
                            int status_code = Integer.parseInt(snapshot.child("Status").getValue().toString());
                            Double total  = Double.valueOf(snapshot.child("total").getValue().toString());

                            status_code = updateStatus(status_code,date,start_time,end_time,end_join_time);
                            reference.child("Quiz").child(code).child("Status").setValue(status_code);

                            date = encodeDate(date);

                            if (status_code<3) {
                                downloadQuestions(snapshot);


                                //Code to join quiz
                                reference.child("Users").child(uid).child("Quiz").child(date).child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {
                                        if (snap.exists()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Already Joined", Toast.LENGTH_SHORT).show();
                                        } else {
                                            reference.child("Users").child(uid).child("Quiz").child(date).child(code).child("quiz code").setValue(code);
                                            reference.child("Users").child(uid).child("Quiz").child(date).child(code).child("quiz name").setValue(quiz);
                                            reference.child("Users").child(uid).child("Quiz").child(date).child(code).child("total").setValue(total);
                                            reference.child("Users").child(uid).child("Quiz").child(date).child(code).child("Score").setValue(0);
                                            participantRef.child(code).child("Participants").child(uid).child("Name").setValue(uname);

                                            reference.child("Users").child(uid).child("Quiz").child(date).child(code).setValue(questions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getContext(), "Joined Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                        closeDialog();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Cannot Join Deadline ended", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Not valid", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void downloadQuestions(DataSnapshot snapshot) {
        questions = new HashMap<>();
        for (DataSnapshot questionSnap : snapshot.child("Question").getChildren()) {
            String qno,ans;
            Double marks;

            qno = questionSnap.getKey();
            ans = questionSnap.child("ans").getValue().toString();
            marks = Double.valueOf(questionSnap.child("marks").getValue().toString());
            QuestionModel model = new QuestionModel(marks,ans,"NULL",0);
            questions.put(qno,model);
        }
    }

    private void closeDialog() {
        this.dismiss();
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


    private String encodeDate(Date date) {
//        For Update of status
        String d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
        return d;
    }
    private int updateStatus(int status_code, String quiz_date, String startTime, String endTime, String endJoinTime) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        today = decodeToDate(encodeDate(today));
        Date quiz = decodeToDate(quiz_date);
        Date now = null,start = null,end = null,endJoin = null;

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        try {
            String time = timeFormat.format(c.getTime());
            startTime = timeFormat.format(timeFormat.parse(startTime));
            endTime = timeFormat.format(timeFormat.parse(endTime));
//            endJoinTime = timeFormat.format(timeFormat.parse(endJoinTime));
            now = timeFormat.parse(time);
            start = timeFormat.parse(startTime);
            end = timeFormat.parse(endTime);
            endJoin = timeFormat.parse(endJoinTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("CHECK",today.toString());
        if (today.compareTo(quiz)>0) {
            if (status_code == 1) {
                status_code = 4;
            }
        } else if(today.compareTo(quiz)==0) {
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
