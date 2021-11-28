package com.example.quizitup.view_analysis.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.quizitup.R;
import com.example.quizitup.question.QuestionModel;
import com.example.quizitup.view_analysis.model.Analysis;
import com.example.quizitup.view_result.fragments.ResultMcqFragment;
import com.example.quizitup.view_result.fragments.ResultTrueFalseFragment;
import com.example.quizitup.view_result.model.Result;

import java.util.ArrayList;
import java.util.Locale;

public class AnalysisMcqFragment extends Fragment {

    ArrayList<Analysis> analysisList;
    int position;
    boolean submit = false;
    String exp;

    Button prevButton, nextButton;
    TextView qno, questionText, noneTxt, p1Txt, p2Txt, p3Txt, p4Txt, p5Txt, p6Txt;
    RadioButton radioOp1, radioOp2, radioOp3, radioOp4, radioOp5, radioOp6;
    ImageView opImg1, opImg2, opImg3, opImg4, opImg5, opImg6;

    ProgressDialog progressDialog;


    public AnalysisMcqFragment(ArrayList<Analysis> analysisList, int i) {
        // Required empty public constructor
        this.analysisList = analysisList;
        this.position = i;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis_mcq, container, false);

        prevButton = view.findViewById(R.id.previous_btn);
        nextButton = view.findViewById(R.id.next_btn);

        if (position == analysisList.size() - 1) {
            nextButton.setText("Close");
            submit = true;
        } else {
            nextButton.setText("Next");
            submit = false;
        }

        qno = view.findViewById(R.id.question_no);
        questionText = view.findViewById(R.id.question);
        noneTxt = view.findViewById(R.id.notAttended);

        radioOp1 = view.findViewById(R.id.op1);
        radioOp2 = view.findViewById(R.id.op2);
        radioOp3 = view.findViewById(R.id.op3);
        radioOp4 = view.findViewById(R.id.op4);
        radioOp5 = view.findViewById(R.id.op5);
        radioOp6 = view.findViewById(R.id.op6);

        p1Txt = view.findViewById(R.id.percent_1);
        p2Txt = view.findViewById(R.id.percent_2);
        p3Txt = view.findViewById(R.id.percent_3);
        p4Txt = view.findViewById(R.id.percent_4);
        p5Txt = view.findViewById(R.id.percent_5);
        p6Txt = view.findViewById(R.id.percent_6);

        opImg1 = view.findViewById(R.id.ans_img_1);
        opImg2 = view.findViewById(R.id.ans_img_2);
        opImg3 = view.findViewById(R.id.ans_img_3);
        opImg4 = view.findViewById(R.id.ans_img_4);
        opImg5 = view.findViewById(R.id.ans_img_5);
        opImg6 = view.findViewById(R.id.ans_img_6);

        progressDialog = ProgressDialog.show(getContext(),"","Loading please wait");
        setUI();

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                switch (analysisList.get(position).getQuestionModel().getQType().toUpperCase(Locale.ROOT)) {
                    case "M":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new AnalysisMcqFragment(analysisList, position)).commit();
                        break;
                    case "TF":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new AnalysisTrueFalseFragment(analysisList, position)).commit();
                        break;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (submit) {
                    getActivity().finish();
                    return;
                }

                switch (analysisList.get(position).getQuestionModel().getQType().toUpperCase(Locale.ROOT)) {
                    case "M":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new AnalysisMcqFragment(analysisList, position)).commit();
                        break;
                    case "TF":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new AnalysisTrueFalseFragment(analysisList, position)).commit();
                        break;
                }
            }
        });


        return view;
    }

    private void setUI() {
        // Set Values to UI elements
        QuestionModel result = analysisList.get(position).getQuestionModel();
        // If first question is getting displayed disable previous button
        if (position == 0)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

        int no = result.getQno().intValue();
        qno.setText("Q" + no);
        questionText.setText(result.getQuestion());

        Double ansD = Double.parseDouble(result.getAns());
        int ans = ansD.intValue();
        switch (ans) {
            case 1:
                opImg1.setImageResource(R.drawable.ic_tick);
                break;
            case 2:
                opImg2.setImageResource(R.drawable.ic_tick);
                break;
            case 3:
                opImg3.setImageResource(R.drawable.ic_tick);
                break;
            case 4:
                opImg4.setImageResource(R.drawable.ic_tick);
                break;
            case 5:
                opImg5.setImageResource(R.drawable.ic_tick);
                break;
            case 6:
                opImg6.setImageResource(R.drawable.ic_tick);
                break;
        }

        String op = result.getOp1();
        if (op.equals("NA")) {
            radioOp1.setVisibility(View.GONE);
            opImg1.setVisibility(View.GONE);
            p1Txt.setVisibility(View.GONE);
        } else {
            radioOp1.setText(op);
            p1Txt.setText(analysisList.get(position).getP1()+"%");
        }

        op = result.getOp2();
        if (op.equals("NA")) {
            radioOp2.setVisibility(View.GONE);
            opImg2.setVisibility(View.GONE);
            p2Txt.setVisibility(View.GONE);
        } else {
            radioOp2.setText(op);
            p2Txt.setText(analysisList.get(position).getP2()+"%");
        }

        op = result.getOp3();
        if (op.equals("NA")) {
            radioOp3.setVisibility(View.GONE);
            opImg3.setVisibility(View.GONE);
            p3Txt.setVisibility(View.GONE);
        } else {
            radioOp3.setText(op);
            p3Txt.setText(analysisList.get(position).getP3()+"%");
        }

        op = result.getOp4();
        if (op.equals("NA")) {
            radioOp4.setVisibility(View.GONE);
            opImg4.setVisibility(View.GONE);
            p4Txt.setVisibility(View.GONE);
        } else {
            radioOp4.setText(op);
            p4Txt.setText(analysisList.get(position).getP4()+"%");
        }

        op = result.getOp5();
        if (op.equals("NA")) {
            radioOp5.setVisibility(View.GONE);
            opImg5.setVisibility(View.GONE);
            p5Txt.setVisibility(View.GONE);
        } else {
            radioOp5.setText(op);
            p5Txt.setText(analysisList.get(position).getP5()+"%");
        }

        op = result.getOp6();
        if (op.equals("NA")) {
            radioOp6.setVisibility(View.GONE);
            opImg6.setVisibility(View.GONE);
            p6Txt.setVisibility(View.GONE);
        } else {
            radioOp6.setText(op);
            p6Txt.setText(analysisList.get(position).getP6()+"%");
        }

        noneTxt.setText("Not Attended : "+analysisList.get(position).getPnone()+"%");
        progressDialog.dismiss();

    }
}