package com.example.quizitup.quiz;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.view_result.ViewResultActivity;
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
import java.util.Date;
import java.util.Locale;

public class QuizCompletedHomeFragment extends Fragment {

    TextView quizNameTxt, quizCodeTxt, scoreTxt;
    ImageButton cpyBtn;
    Button viewResultBtn;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference quizRef,ansRef;

    String uid,code,name,date;
    int score_type;
    double  score,myScore = 0.0,total;
    public QuizCompletedHomeFragment(String code) {
        this.code = code;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_completed_home, container, false);
        quizNameTxt = view.findViewById(R.id.quizName);
        quizCodeTxt = view.findViewById(R.id.quiz_code);
        scoreTxt = view.findViewById(R.id.scoreTxt);
        cpyBtn = view.findViewById(R.id.cpyBtn);
        viewResultBtn = view.findViewById(R.id.viewResultBtn);

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference("Quiz").child(code);
        ansRef = database.getReference("Users").child(uid).child("Quiz");

        viewResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        score_type = Integer.parseInt(snapshot.child("Score Type").getValue().toString());
                        if (score_type == 0)
                            Toast.makeText(getContext(), "Answers are not released yet ", Toast.LENGTH_SHORT).show();
                        else {
                            Intent intent = new Intent(getContext(), ViewResultActivity.class);
                            intent.putExtra("code",code);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        cpyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("quiz code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(),"Please Wait","Loading your score");
        setUpUi();
    }

    private void setUpUi() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("quiz name").getValue().toString();
                total = Double.parseDouble(snapshot.child("total").getValue().toString());
                date = snapshot.child("Date").getValue().toString();
                date = encodeDate(date);
                score = Double.parseDouble(snapshot.child("Participants").child(uid).child("score").getValue().toString());
                quizNameTxt.setText(name);
                quizCodeTxt.setText(code);
                if (score<=0)
                    calculateScore();
                else {
                    scoreTxt.setText(score+"/"+total);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calculateScore() {
        ansRef.child(date).child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //double myScore = 0.0;
                for (DataSnapshot ansSnap : snapshot.getChildren()) {
                    double temp = Double.parseDouble(ansSnap.child("score").getValue().toString());
                    myScore+=temp;
                }
                quizRef.child("Participants").child(uid).child("score").setValue(myScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            scoreTxt.setText(myScore+"/"+total);
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Some Error Occurred, Please Try again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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