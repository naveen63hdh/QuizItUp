package com.example.quizitup.question;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizitup.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class TrueFalseFragment extends Fragment {

    ArrayList<QuestionModel> questions;
    int position;
    boolean submit = false;
    Button prevButton, nextButton;
    TextView qno, questionText;
    RadioGroup trueFalseGroup;
    RadioButton trueRadio, falseRadio;

    String code, endTime, ans;
    int questionNo;


    ProgressDialog progressDialog;

    FirebaseDatabase database;
    DatabaseReference quizRef, questionRef, userAnsRef;
    FirebaseAuth auth;

    public TrueFalseFragment(ArrayList<QuestionModel> questions, int position, String code, String endTime) {
        this.questions = questions;
        this.position = position;
        this.code = code;
        this.endTime = endTime;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View and elements initialization
        View view = inflater.inflate(R.layout.fragment_true_false, container, false);

        prevButton = view.findViewById(R.id.previous_btn);
        nextButton = view.findViewById(R.id.next_btn);
        qno = view.findViewById(R.id.question_no);
        questionText = view.findViewById(R.id.question);
        trueFalseGroup = view.findViewById(R.id.radio_group);
        trueRadio = view.findViewById(R.id.option_true);
        falseRadio = view.findViewById(R.id.option_false);

        progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Loading your message");


        auth = FirebaseAuth.getInstance();
        String uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference().child("Quiz").child(code);
        questionRef = database.getReference().child("Quiz").child(code).child("Question");
        String date = encodeDate(Calendar.getInstance().getTime());
        questionNo = questions.get(position).getQno().intValue();
        userAnsRef = database.getReference().child("Users").child(uid).child(date).child(code).child(String.valueOf(questionNo));

        ans = questions.get(position).getAns();

        updateStatus();

//      Next button is disabled unless option is clicked
//        nextButton.setEnabled(false);

        prevButton.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Saving your answer");
            // TODO PUT choice to database
            String choice = "NULL";

            switch (trueFalseGroup.getCheckedRadioButtonId()) {
                case R.id.option_true:
                    choice = "T";
                    break;
                case R.id.option_false:
                    choice = "F";
                    break;
            }

            HashMap<String, Object> ansMap = new HashMap<>();
            if (ans.equals(choice)) {
                ansMap.put("score", questions.get(position).getMarks());
            } else {
                ansMap.put("score", 0);
            }
            ansMap.put("choice", choice);

//            setToPreference();
            userAnsRef.updateChildren(ansMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    position--;
                    progressDialog.dismiss();
                    switch (questions.get(position).getQType().toUpperCase(Locale.ROOT)) {
                        case "M":
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new McqFragment(questions, position, code, endTime)).commit();
                            break;
                        case "TF":
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TrueFalseFragment(questions, position, code, endTime)).commit();
                            break;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Some Error Occurred, Check your network connection", Toast.LENGTH_SHORT).show();
                }
            });

        });
        nextButton.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Saving your answer");
            // TODO PUT choice to database
            String choice = "NULL";

            switch (trueFalseGroup.getCheckedRadioButtonId()) {
                case R.id.option_true:
                    choice = "T";
                    break;
                case R.id.option_false:
                    choice = "F";
                    break;
            }

            HashMap<String, Object> ansMap = new HashMap<>();
            if (ans.equals(choice)) {
                ansMap.put("score", questions.get(position).getMarks());
            } else {
                ansMap.put("score", 0);
            }
            ansMap.put("choice", choice);


            userAnsRef.updateChildren(ansMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    position++;
                    progressDialog.dismiss();
                    if (submit) {
                        getActivity().finish();
                        return;
                    }
                    switch (questions.get(position).getQType().toUpperCase(Locale.ROOT)) {
                        case "M":
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new McqFragment(questions, position, code, endTime)).commit();
                            break;
                        case "TF":
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TrueFalseFragment(questions, position, code, endTime)).commit();
                            break;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Some Error Occurred, Check your network connection", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return view;
    }

    private void updateStatus() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                endTime = snapshot.child("End Time").getValue().toString();
//                String endJoinTime = snapshot.child("End Joining Time").getValue().toString();
                int status_code = Integer.parseInt(snapshot.child("Status").getValue().toString());

//  --------------------------------------- Code TO Update Status to End -------------------------------------------------
                if (status_code != 4) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                    try {
                        String time = timeFormat.format(c.getTime());
                        endTime = timeFormat.format(timeFormat.parse(endTime));
                        Date now = timeFormat.parse(time);
                        Date end = timeFormat.parse(endTime);
                        if (now.compareTo(end) >= 0)
                            quizRef.child("Status").setValue(4).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Quiz Ended and your answers have been saved", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Some Error occurred please check internet connection and try again", Toast.LENGTH_SHORT).show();
                                }
                            });

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//  --------------------------------------- Code TO Update Status to End -------------------------------------------------
                    setUi();
                } else
                    getActivity().finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUi() {

        if (position == questions.size() - 1) {
            nextButton.setText("Submit");
            submit = true;
        } else {
            nextButton.setText("Next");
            submit = false;
        }

        // Set Values to UI elements
        QuestionModel q = questions.get(position);
        int no = q.getQno().intValue();
        qno.setText("Q" + no);
        questionText.setText(q.getQuestion());

//        If first question is getting displayed disable previous button
        if (position == 0)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

        // TODO GET choice from database

    }

//    private void setToPreference() {
//        int ans = trueFalseGroup.getCheckedRadioButtonId();
//        ansEditor.putInt(String.valueOf(position), ans);
//        ansEditor.commit();
//    }
//
//    private void getFromPreference() {
//        int ansId = sharedPreferences.getInt(String.valueOf(position), -1);
//        if (ansId != -1) {
//            if (ansId == R.id.option_true) {
//                trueRadio.setChecked(true);
//            } else if (ansId == R.id.option_false) {
//                falseRadio.setChecked(true);
//            }
//        }
//    }

    private String encodeDate(Date date) {
//        For Update of status
        String d = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(date);
        return d;
    }

}