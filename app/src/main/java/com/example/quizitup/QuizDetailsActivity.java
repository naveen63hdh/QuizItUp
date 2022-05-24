package com.example.quizitup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class QuizDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    Button submitBtn;

    EditText dateTxt, startTimeTxt, endTimeTxt, quizNameTxt, descriptionTxt;
    ImageButton dateBtn, startTimeBtn, endTimeBtn;

    SwitchCompat switchCompat;

    String quizCode, classId, emailList="";
    Double total;
    private int mYear, mMonth, mDay, mHour, mMinute;

    DatabaseReference quizRef, userRef, classRef, participantRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        //UI Init
        quizNameTxt = findViewById(R.id.quizNameTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        switchCompat = findViewById(R.id.ans_switch);

        dateTxt = findViewById(R.id.date_txt);
        startTimeTxt = findViewById(R.id.start_time_txt);
        endTimeTxt = findViewById(R.id.end_time_txt);
//        endJoinTimeTxt = findViewById(R.id.end_joining_txt);
        dateBtn = findViewById(R.id.date_btn);
        startTimeBtn = findViewById(R.id.start_time_btn);
        endTimeBtn = findViewById(R.id.end_time_btn);
//        endJoinTimeBtn = findViewById(R.id.end_joining_btn);

        quizCode = getIntent().getStringExtra("code");
        classId = getIntent().getStringExtra("classId");
        total = getIntent().getDoubleExtra("total", 0.0);
//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Quiz Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        submitBtn = findViewById(R.id.submit_btn);

        dateBtn.setOnClickListener(this);
        startTimeBtn.setOnClickListener(this);
        endTimeBtn.setOnClickListener(this);
//        endJoinTimeBtn.setOnClickListener(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        quizRef = FirebaseDatabase.getInstance().getReference("Quiz");
        classRef = FirebaseDatabase.getInstance().getReference("Classrooms").child(classId).child("Quiz").child(quizCode);
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        participantRef = FirebaseDatabase.getInstance().getReference("Classrooms").child(classId).child("Participants");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<String> courseParticipants = new ArrayList<>();
                ProgressDialog dialog = ProgressDialog.show(QuizDetailsActivity.this, "", "Creating quiz");
                HashMap<String, Object> participantMap = new HashMap<>();
                participantRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> participantTree = new HashMap<>();


                        participantTree.put("score",0);
                        participantTree.put("isCompleted",0);

                        for(DataSnapshot snap : snapshot.getChildren()) {
                            String key = snap.getKey();
                            String uname = snap.child("uname").getValue().toString();
                            participantTree.put("Name",uname);
//                            Toast.makeText(QuizDetailsActivity.this, uname, Toast.LENGTH_SHORT).show();
                            courseParticipants.add(key);
                            participantMap.put(key,participantTree);
                        }


                        String date = dateTxt.getText().toString();

                        try {
                            Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(d);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        final String dt = date;
//                        Toast.makeText(QuizDetailsActivity.this, date, Toast.LENGTH_SHORT).show();

                        HashMap<String, Object> quizMap = new HashMap<>();
                        quizMap.put("quiz name", quizNameTxt.getText().toString());
                        quizMap.put("Date", dateTxt.getText().toString());
                        quizMap.put("Start Time", startTimeTxt.getText().toString());
                        quizMap.put("End Time", endTimeTxt.getText().toString());
                        quizMap.put("Description", descriptionTxt.getText().toString());
                        quizMap.put("Created by", uid);
                        quizMap.put("Status", 1);
                        quizMap.put("total", total);
                        quizMap.put("Participants",participantMap);

                        if (switchCompat.isChecked())
                            quizMap.put("Score Type", 1);
//                    quizRef.child(quizCode).child("Score Type").setValue(1);
                        else
                            quizMap.put("Score Type", 0);


                        userRef.child(uid).child("Quiz").child(date).child(quizCode).child("quiz name").setValue(quizNameTxt.getText().toString());
                        userRef.child(uid).child("Quiz").child(date).child(quizCode).child("quiz code").setValue(quizCode);

                        quizRef.child(quizCode).updateChildren(quizMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    classRef.child("quizname").setValue(quizNameTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(QuizDetailsActivity.this, "Error while creating quiz", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                quizRef.child(quizCode).child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    int count = 0;
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        HashMap<String,Object> questions = downloadQuestions(snapshot);

                                                        int size = courseParticipants.size();
                                                        if (size==0)
                                                            dialog.dismiss();
                                                        for(String key : courseParticipants) {
                                                            userRef.child(key).child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot mailSnap) {
                                                                    emailList += mailSnap.getValue()+", ";
                                                                    userRef.child(key).child("Quiz").child(dt).child(quizCode).updateChildren(questions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            count++;
                                                                            if(count==size && task.isSuccessful()) {
                                                                                emailList = emailList.trim().replaceAll(",$","");
                                                                                Log.i("Emails_test",emailList);
                                                                                notifyUsers(emailList);
                                                                                Toast.makeText(QuizDetailsActivity.this, "Quiz created successfully", Toast.LENGTH_SHORT).show();
                                                                                dialog.dismiss();
                                                                            }
                                                                        }
                                                                    });
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
//                                                Toast.makeText(QuizDetailsActivity.this, "Quiz created successfully", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                } else {
                                    Toast.makeText(QuizDetailsActivity.this, "Error while creating quiz", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }


                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //                quizRef.child(quizCode).child("quiz name").setValue(quizNameTxt.getText().toString());
//                quizRef.child(quizCode).child("Date").setValue(dateTxt.getText().toString());
//                quizRef.child(quizCode).child("Start Time").setValue(startTimeTxt.getText().toString());
//                quizRef.child(quizCode).child("End Time").setValue(endTimeTxt.getText().toString());
////                quizRef.child(quizCode).child("End Joining Time").setValue(endJoinTimeTxt.getText().toString());
//                quizRef.child(quizCode).child("Description").setValue(descriptionTxt.getText().toString());
//                quizRef.child(quizCode).child("Created by").setValue(uid);
//                quizRef.child(quizCode).child("Status").setValue(1);
//                quizRef.child(quizCode).child("total").setValue(total);

//                    quizRef.child(quizCode).child("Score Type").setValue(0);
//                Dialog dialog = new DialogQCreated(QuizDetailsActivity.this, quizCode);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.show();
//                Window window = dialog.getWindow();
//                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            }
        });

    }

    private HashMap<String, Object> downloadQuestions(DataSnapshot snapshot) {
        HashMap<String,Object> questions = new HashMap<>();
        for (DataSnapshot questionSnap : snapshot.getChildren()) {
            String qno,ans;
            Double marks;

            qno = questionSnap.getKey();
            ans = questionSnap.child("ans").getValue().toString();
            marks = Double.valueOf(questionSnap.child("marks").getValue().toString());
            QuestionModel model = new QuestionModel(marks,ans,"NULL",0);
            questions.put(qno,model);
        }
        return questions;
    }


    @Override
    public void onClick(View v) {
        if (v == dateBtn) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            dateTxt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        } else if (v == startTimeBtn) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            int hour = hourOfDay;
                            String timeset = "";
                            if (hour > 12) {
                                hour -= 12;
                                timeset = "PM";
                            } else if (hour == 0) {
                                hour += 12;
                                timeset = "AM";
                            } else if (hour == 12)
                                timeset = "PM";
                            else
                                timeset = "AM";


                            startTimeTxt.setText(hour + ":" + minute + " " + timeset);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } else if (v == endTimeBtn) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            int hour = hourOfDay;
                            String timeset = "";
                            if (hour > 12) {
                                hour -= 12;
                                timeset = "PM";
                            } else if (hour == 0) {
                                hour += 12;
                                timeset = "AM";
                            } else if (hour == 12)
                                timeset = "PM";
                            else
                                timeset = "AM";


                            endTimeTxt.setText(hour + ":" + minute + " " + timeset);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
//        else if (v == endJoinTimeBtn) {
//            // Get Current Time
//            final Calendar c = Calendar.getInstance();
//            mHour = c.get(Calendar.HOUR_OF_DAY);
//            mMinute = c.get(Calendar.MINUTE);
//
//            // Launch Time Picker Dialog
//            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                    new TimePickerDialog.OnTimeSetListener() {
//
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay,
//                                              int minute) {
//                            int hour = hourOfDay;
//                            String timeset = "";
//                            if (hour > 12) {
//                                hour -= 12;
//                                timeset = "PM";
//                            } else if (hour == 0) {
//                                hour += 12;
//                                timeset = "AM";
//                            } else if (hour == 12)
//                                timeset = "PM";
//                            else
//                                timeset = "AM";
//
//
//                            endJoinTimeTxt.setText(hour + ":" + minute + " " + timeset);
//                        }
//                    }, mHour, mMinute, false);
//            timePickerDialog.show();
//        }
    }

    void notifyUsers(String recipients) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("quizitup.official@gmail.com",
                            "Quizitup@123");
                    sender.sendMail("New Quiz Has been created!!!", "Dear Participants a new quiz has been created inside course "+classId,
                            "quizitup.official@gmail.com", recipients);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }
}


//TODO
// Add this to mailer
//for (String id : courseParticipants) {
//        userRef.child(id).child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot snapshot) {
//        emailList += snapshot.getValue()+", ";
//        count++;
//        if(count==size && task.isSuccessful()) {
//        Toast.makeText(QuizDetailsActivity.this, "Quiz created successfully", Toast.LENGTH_SHORT).show();
//        dialog.dismiss();
//        }
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//        });
//        }