package com.example.quizitup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuizDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    Button submitBtn;

    EditText dateTxt, startTimeTxt, endTimeTxt, endJoinTimeTxt,quizNameTxt,descriptionTxt;
    ImageButton dateBtn, startTimeBtn, endTimeBtn, endJoinTimeBtn;

    SwitchCompat switchCompat;

    String quizCode;
    Double total;

    private int mYear, mMonth, mDay, mHour, mMinute;

    DatabaseReference quizRef,userRef;

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
        endJoinTimeTxt = findViewById(R.id.end_joining_txt);
        dateBtn = findViewById(R.id.date_btn);
        startTimeBtn = findViewById(R.id.start_time_btn);
        endTimeBtn = findViewById(R.id.end_time_btn);
        endJoinTimeBtn = findViewById(R.id.end_joining_btn);

        quizCode = getIntent().getStringExtra("code");
        total = getIntent().getDoubleExtra("total",0.0);
//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Quiz Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        submitBtn = findViewById(R.id.submit_btn);

        dateBtn.setOnClickListener(this);
        startTimeBtn.setOnClickListener(this);
        endTimeBtn.setOnClickListener(this);
        endJoinTimeBtn.setOnClickListener(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        quizRef = FirebaseDatabase.getInstance().getReference("Quiz");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateTxt.getText().toString();

                try {
                    Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
                    date = new SimpleDateFormat("yyyy_MM_dd",Locale.ENGLISH).format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(QuizDetailsActivity.this, date, Toast.LENGTH_SHORT).show();
                userRef.child(uid).child("Quiz").child(date).child(quizCode).child("quiz name").setValue(quizNameTxt.getText().toString());
                userRef.child(uid).child("Quiz").child(date).child(quizCode).child("quiz code").setValue(quizCode);
                quizRef.child(quizCode).child("quiz name").setValue(quizNameTxt.getText().toString());
                quizRef.child(quizCode).child("Date").setValue(dateTxt.getText().toString());
                quizRef.child(quizCode).child("Start Time").setValue(startTimeTxt.getText().toString());
                quizRef.child(quizCode).child("End Time").setValue(endTimeTxt.getText().toString());
                quizRef.child(quizCode).child("End Joining Time").setValue(endJoinTimeTxt.getText().toString());
                quizRef.child(quizCode).child("Description").setValue(descriptionTxt.getText().toString());
                quizRef.child(quizCode).child("Created by").setValue(uid);
                quizRef.child(quizCode).child("Status").setValue(1);
                quizRef.child(quizCode).child("total").setValue(total);
                if (switchCompat.isChecked())
                    quizRef.child(quizCode).child("Score Type").setValue(1);
                else
                    quizRef.child(quizCode).child("Score Type").setValue(0);
                Dialog dialog = new DialogQCreated(QuizDetailsActivity.this, quizCode);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            }
        });

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
        } else if (v == endJoinTimeBtn) {
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


                            endJoinTimeTxt.setText(hour + ":" + minute + " " + timeset);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}