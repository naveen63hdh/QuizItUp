package com.example.quizitup.view_analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import com.example.quizitup.R;
import com.example.quizitup.question.QuestionModel;
import com.example.quizitup.view_analysis.fragment.AnalysisMcqFragment;
import com.example.quizitup.view_analysis.fragment.AnalysisTrueFalseFragment;
import com.example.quizitup.view_analysis.model.Analysis;
import com.example.quizitup.view_analysis.model.Participant;
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
import java.util.HashMap;
import java.util.Locale;

public class ViewAnalysisActivity extends AppCompatActivity {

    ProgressDialog dialog;
    String quizCode, uid, date;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference quizRef;

    ArrayList<Result> resultList;
    ArrayList<QuestionModel> questionList;
    ArrayList<Analysis> analysisList;
    long participantCount, quesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_analysis);

        //        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        quizCode = getIntent().getStringExtra("code");

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference().child("Quiz").child(quizCode);
        dialog = ProgressDialog.show(this, "Please wait", "Loading your result");
        resultList = new ArrayList<>();
        questionList = new ArrayList<>();
        analysisList = new ArrayList<>();

        // Get User Details
        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateDataset(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void populateDataset(DataSnapshot userSnap) {
        //Download Quiz Details
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//               Quiz date and encode date
                date = snapshot.child("Date").getValue().toString();
                date = encodeDate(date);

//                Get question count and participant count
                quesCount = snapshot.child("Question").getChildrenCount();
                participantCount = snapshot.child("Participants").getChildrenCount();

                HashMap<Integer, Integer> questionIndex = new HashMap<>();
                HashMap<Integer, int[]> ansCountMap = new HashMap<>();

                int index = 0;

//                Populate Question model
                for (DataSnapshot questionSnap : snapshot.child("Question").getChildren()) {
                    double marks, qno;
                    String QType, question, op1, op2, op3, op4, op5, op6, ans, exp, shuffle;
                    qno = Double.parseDouble(questionSnap.child("qno").getValue().toString());
                    marks = Double.parseDouble(questionSnap.child("marks").getValue().toString());
                    QType = questionSnap.child("qtype").getValue().toString();
                    question = questionSnap.child("question").getValue().toString();
                    op1 = questionSnap.child("op1").getValue().toString();
                    op2 = questionSnap.child("op2").getValue().toString();
                    op3 = questionSnap.child("op3").getValue().toString();
                    op4 = questionSnap.child("op4").getValue().toString();
                    op5 = questionSnap.child("op5").getValue().toString();
                    op6 = questionSnap.child("op6").getValue().toString();
                    ans = questionSnap.child("ans").getValue().toString();
                    exp = questionSnap.child("exp").getValue().toString();
                    shuffle = questionSnap.child("shuffle").getValue().toString();

//                    Create index list for specific question
                    Double temp = qno;
                    questionIndex.put(temp.intValue(), index);
                    index++;

                    QuestionModel questionModel = new QuestionModel(qno, QType, question, op1, op2, op3, op4, op5, op6, ans, exp, shuffle, marks);
                    questionList.add(questionModel);

//                    Initialize default option array for all question
                    int[] optionsInt = new int[7];
                    ansCountMap.put(temp.intValue(), optionsInt);
                }

//                Iterate all participants of quiz
                for (DataSnapshot participantsSnap : snapshot.child("Participants").getChildren()) {
                    String key = participantsSnap.getKey();
//                    Iterate ans of specific participant
                    for (DataSnapshot ansSnap : userSnap.child(key).child("Quiz").child(date).child(quizCode).getChildren()) {
                        int qno = Integer.parseInt(ansSnap.getKey());
                        int[] myOptionInt = ansCountMap.get(qno);
//                        Get choice of specific question
                        if (ansSnap.child("choice").getValue()!=null) {
                            String ch = ansSnap.child("choice").getValue().toString();
                            int in = questionIndex.get(qno);
//                        Check and increment which ans user has given
                            if (questionList.get(in).getQType().equals("M")) {
                                if (ch.equals(questionList.get(in).getOp1())) {
                                    myOptionInt[1]++;
                                } else if (ch.equals(questionList.get(in).getOp2())) {
                                    myOptionInt[2]++;
                                } else if (ch.equals(questionList.get(in).getOp3())) {
                                    myOptionInt[3]++;
                                } else if (ch.equals(questionList.get(in).getOp4())) {
                                    myOptionInt[4]++;
                                } else if (ch.equals(questionList.get(in).getOp5())) {
                                    myOptionInt[5]++;
                                } else if (ch.equals(questionList.get(in).getOp6())) {
                                    myOptionInt[6]++;
                                }
                            } else if (questionList.get(in).getQType().equals("TF")) {
                                if (ch.equals("T")) {
                                    myOptionInt[1]++;
                                } else if (ch.equals("F")) {
                                    myOptionInt[2]++;
                                }
                            }
                        } else {
//                            If participant doesn't select any option
                            myOptionInt[0]++;
                        }
                        ansCountMap.put(qno,myOptionInt);
                    }
                }


                for (int i = 1;i<=6;i++) {
                    Log.i("ANS_MAP","size = "+ansCountMap.size());
                    int[] ans = ansCountMap.get(i);
                    for (int j = 0;j<7;j++)
                        Log.i("ANS_MAP"+"Q"+i," = "+ans[j]);
                }

//                Populate Analysis Model
                for (DataSnapshot questionSnap : snapshot.child("Question").getChildren()) {
                    Double qno;
                    qno = Double.parseDouble(questionSnap.child("qno").getValue().toString());
                    int in = questionIndex.get(qno.intValue());
                    QuestionModel qmodel = questionList.get(in);
                    int[] ans = ansCountMap.get(qno.intValue());
                    double pnone = (ans[0] / participantCount) * 100;
                    double p1 = (ans[1] / participantCount) * 100;
                    double p2 = (ans[2] / participantCount) * 100;
                    double p3 = (ans[3] / participantCount) * 100;
                    double p4 = (ans[4] / participantCount) * 100;
                    double p5 = (ans[5] / participantCount) * 100;
                    double p6 = (ans[6] / participantCount) * 100;
                    Analysis analysis = new Analysis(p1,p2,p3,p4,p5,p6,pnone,qmodel);
                    analysisList.add(analysis);
                }

                setUpUi();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String encodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void setUpUi() {
        QuestionModel questionModel = analysisList.get(0).getQuestionModel();
//        getIntent().putParcelableArrayListExtra("sag",);
        switch (questionModel.getQType().toUpperCase(Locale.ROOT)) {
            case "M":
                getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, new AnalysisMcqFragment(analysisList,0)).commit();
                break;
            case "TF":
                getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, new AnalysisTrueFalseFragment(analysisList,0)).commit();
                break;
        }
    }


}