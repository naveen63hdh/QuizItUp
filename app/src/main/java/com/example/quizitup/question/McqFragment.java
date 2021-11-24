package com.example.quizitup.question;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class McqFragment extends Fragment implements View.OnClickListener {

    ArrayList<QuestionModel> questions;
    int position;
    boolean submit = false;
    Button prevButton, nextButton;
    TextView qno, questionText;
    RadioGroup ansGroup;
    RadioButton radioOp1, radioOp2, radioOp3, radioOp4, radioOp5, radioOp6;

    String code, endTime, ans;
    int questionNo;

    ProgressDialog progressDialog;

    FirebaseDatabase database;
    DatabaseReference quizRef, questionRef, userAnsRef;
    FirebaseAuth auth;

    public McqFragment(ArrayList<QuestionModel> questions, int position, String code, String endTime) {
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

        View view = inflater.inflate(R.layout.fragment_mcq, container, false);
        prevButton = view.findViewById(R.id.previous_btn);
        nextButton = view.findViewById(R.id.next_btn);

        if (position == questions.size() - 1) {
            nextButton.setText("Submit");
            submit = true;
        } else {
            nextButton.setText("Next");
            submit = false;
        }

        qno = view.findViewById(R.id.question_no);
        questionText = view.findViewById(R.id.question);
        ansGroup = view.findViewById(R.id.radio_group);
        radioOp1 = view.findViewById(R.id.op1);
        radioOp2 = view.findViewById(R.id.op2);
        radioOp3 = view.findViewById(R.id.op3);
        radioOp4 = view.findViewById(R.id.op4);
        radioOp5 = view.findViewById(R.id.op5);
        radioOp6 = view.findViewById(R.id.op6);

        radioOp1.setOnClickListener(this);
        radioOp2.setOnClickListener(this);
        radioOp3.setOnClickListener(this);
        radioOp4.setOnClickListener(this);
        radioOp5.setOnClickListener(this);
        radioOp6.setOnClickListener(this);

        progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Loading your message");

        auth = FirebaseAuth.getInstance();
        String uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference().child("Quiz").child(code);
        questionRef = database.getReference().child("Quiz").child(code).child("Question");
        String date = encodeDate(Calendar.getInstance().getTime());
        questionNo = questions.get(position).getQno().intValue();
        userAnsRef = database.getReference().child("Users").child(uid).child(date).child(code).child(String.valueOf(questionNo));

        Double val = Double.parseDouble(questions.get(position).getAns());
        int ansPos = val.intValue();
        ans = null;
        switch (ansPos) {
            case 1:
                ans = questions.get(position).getOp1();
                break;
            case 2:
                ans = questions.get(position).getOp2();
                break;
            case 3:
                ans = questions.get(position).getOp3();
                break;
            case 4:
                ans = questions.get(position).getOp4();
                break;
            case 5:
                ans = questions.get(position).getOp5();
                break;
            case 6:
                ans = questions.get(position).getOp6();
                break;
        }


        updateStatus();


        prevButton.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Saving your answer");
            // TODO PUT choice to database
            String choice = "NULL";

            switch (ansGroup.getCheckedRadioButtonId()) {
                case R.id.op1:
                    choice = radioOp1.getText().toString();
                    break;
                case R.id.op2:
                    choice = radioOp2.getText().toString();
                    break;
                case R.id.op3:
                    choice = radioOp3.getText().toString();
                    break;
                case R.id.op4:
                    choice = radioOp4.getText().toString();
                    break;
                case R.id.op5:
                    choice = radioOp5.getText().toString();
                    break;
                case R.id.op6:
                    choice = radioOp6.getText().toString();
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
            // TODO PUT choice to database
            String choice = "NULL";

            switch (ansGroup.getCheckedRadioButtonId()) {
                case R.id.op1:
                    choice = radioOp1.getText().toString();
                    break;
                case R.id.op2:
                    choice = radioOp2.getText().toString();
                    break;
                case R.id.op3:
                    choice = radioOp3.getText().toString();
                    break;
                case R.id.op4:
                    choice = radioOp4.getText().toString();
                    break;
                case R.id.op5:
                    choice = radioOp5.getText().toString();
                    break;
                case R.id.op6:
                    choice = radioOp6.getText().toString();
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
                        Toast.makeText(getContext(), "Quiz Ended and your answers have been saved", Toast.LENGTH_SHORT).show();
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
//            setToPreference();

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
                    setUI();
                } else
                    getActivity().finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUI() {
        // Set Values to UI elements
        QuestionModel q = questions.get(position);
        // If first question is getting displayed disable previous button
        if (position == 0)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

//        getFromPreference();
//      Next button is disabled unless option is clicked
//        nextButton.setEnabled(false);

        Log.i("LIST_RECEIVED", String.valueOf(questions.size()));
        int no = q.getQno().intValue();
        qno.setText("Q" + no);
        questionText.setText(q.getQuestion());
        String op = q.getOp1();
        if (op.equals("NA")) {
            radioOp1.setVisibility(View.GONE);
        } else {
            radioOp1.setText(op);
        }

        op = q.getOp2();
        if (op.equals("NA")) {
            radioOp2.setVisibility(View.GONE);
        } else {
            radioOp2.setText(op);
        }

        op = q.getOp3();
        if (op.equals("NA")) {
            radioOp3.setVisibility(View.GONE);
        } else {
            radioOp3.setText(op);
        }

        op = q.getOp4();
        if (op.equals("NA")) {
            radioOp4.setVisibility(View.GONE);
        } else {
            radioOp4.setText(op);
        }

        op = q.getOp5();
        if (op.equals("NA")) {
            radioOp5.setVisibility(View.GONE);
        } else {
            radioOp5.setText(op);
        }

        op = q.getOp6();
        if (op.equals("NA")) {
            radioOp6.setVisibility(View.GONE);
        } else {
            radioOp6.setText(op);
        }

        userAnsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String choice = snapshot.child("choice").getValue().toString();
                if (!choice.equals("NULL")) {
                    if (choice.equals(questions.get(position).getOp1()))
                        radioOp1.setChecked(true);
                    else if (choice.equals(questions.get(position).getOp2()))
                        radioOp2.setChecked(true);
                    else if (choice.equals(questions.get(position).getOp3()))
                        radioOp3.setChecked(true);
                    else if (choice.equals(questions.get(position).getOp4()))
                        radioOp4.setChecked(true);
                    else if (choice.equals(questions.get(position).getOp5()))
                        radioOp5.setChecked(true);
                    else if (choice.equals(questions.get(position).getOp6()))
                        radioOp6.setChecked(true);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        progressDialog.dismiss();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.op1:
                Log.i("OPTION_PRESSED", "OP1");
//                enableNext();
                break;
            case R.id.op2:
                Log.i("OPTION_PRESSED", "OP2");
//                enableNext();
                break;
            case R.id.op3:
                Log.i("OPTION_PRESSED", "OP3");
//                enableNext();
                break;
            case R.id.op4:
                Log.i("OPTION_PRESSED", "OP4");
//                enableNext();
                break;
            case R.id.op5:
                Log.i("OPTION_PRESSED", "OP5");
//                enableNext();
                break;
            case R.id.op6:
                Log.i("OPTION_PRESSED", "OP6");
//                enableNext();
                break;
        }
    }

//    private void setToPreference() {
//        int ans = ansGroup.getCheckedRadioButtonId();
//        ansEditor.putInt(String.valueOf(position), ans);
//        ansEditor.commit();
//    }
//
//    private void getFromPreference() {
//        int ansId = sharedPreferences.getInt(String.valueOf(position), -1);
//        if (ansId != -1) {
//            switch (ansId) {
//                case R.id.op1:
//                    radioOp1.setChecked(true);
//                    break;
//                case R.id.op2:
//                    radioOp2.setChecked(true);
//                    break;
//                case R.id.op3:
//                    radioOp3.setChecked(true);
//                    break;
//                case R.id.op4:
//                    radioOp4.setChecked(true);
//                    break;
//                case R.id.op5:
//                    radioOp5.setChecked(true);
//                    break;
//                case R.id.op6:
//                    radioOp6.setChecked(true);
//                    break;
//            }
//        }
//    }

    private void enableNext() {
        if (!nextButton.isEnabled())
            nextButton.setEnabled(true);
    }

    private String encodeDate(Date date) {
//        For Update of status
        String d = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(date);
        return d;
    }


}