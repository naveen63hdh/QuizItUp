package com.example.quizitup.view_result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.quizitup.R;
import com.example.quizitup.question.McqFragment;
import com.example.quizitup.question.QuestionModel;
import com.example.quizitup.question.TrueFalseFragment;
import com.example.quizitup.view_result.fragments.ResultMcqFragment;
import com.example.quizitup.view_result.fragments.ResultTrueFalseFragment;
import com.example.quizitup.view_result.model.Result;
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
import java.util.Locale;

public class ViewResultActivity extends AppCompatActivity {

    ProgressDialog dialog;
    String quizCode,uid,date;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference quizRef,userRef;

    ArrayList<Result> resultList;
    int qno;
    long count;
    String question,op1,op2,op3,op4,op5,op6,qType,ans,exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);

//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));
        
        quizCode = getIntent().getStringExtra("code");

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference().child("Quiz").child(quizCode);
        userRef = database.getReference().child("Users").child(uid).child("Quiz");

        dialog = ProgressDialog.show(this,"Please wait","Loading your result");
        resultList = new ArrayList<>();
        populateDataset();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void populateDataset() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                date = snapshot.child("Date").getValue().toString();
                date = encodeDate(date);
                DataSnapshot quizSnap = snapshot.child("Question");
                count = quizSnap.getChildrenCount();
                for (DataSnapshot questionSnap : quizSnap.getChildren()) {
                    qno = Double.valueOf(questionSnap.getKey()).intValue();
                    userRef.child(date).child(quizCode).child(String.valueOf(qno)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            qno = Double.valueOf(questionSnap.getKey()).intValue();
                            question = questionSnap.child("question").getValue().toString();
                            qType = questionSnap.child("qtype").getValue().toString();
                            if (qType.equals("M")) {
                                op1 = questionSnap.child("op1").getValue().toString();
                                op2 = questionSnap.child("op2").getValue().toString();
                                op3 = questionSnap.child("op3").getValue().toString();
                                op4 = questionSnap.child("op4").getValue().toString();
                                op5 = questionSnap.child("op5").getValue().toString();
                                op6 = questionSnap.child("op6").getValue().toString();
                            } else
                                op1 = op2 = op3 = op4 = op5 = op6 = "NA";
                            ans = questionSnap.child("ans").getValue().toString();
                            exp = questionSnap.child("exp").getValue().toString();
                            String choice = userSnap.child("choice").getValue().toString();
                            Result result = new Result(qno,qType,question,op1,op2,op3,op4,op5,op6,ans,exp,choice);
                            resultList.add(result);

                            if (count==resultList.size()) {
                                dialog.dismiss();
                                setUpUi();
                            }


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

    private void setUpUi() {
        Result result = resultList.get(0);
//        getIntent().putParcelableArrayListExtra("sag",);
        switch (result.getQType().toUpperCase(Locale.ROOT)) {
            case "M":
                getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, new ResultMcqFragment(resultList,0)).commit();
                break;
            case "TF":
                getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, new ResultTrueFalseFragment(resultList,0)).commit();
                break;
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