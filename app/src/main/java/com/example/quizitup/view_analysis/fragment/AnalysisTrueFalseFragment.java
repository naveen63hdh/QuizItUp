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

import java.util.ArrayList;
import java.util.Locale;

public class AnalysisTrueFalseFragment extends Fragment {

    ArrayList<Analysis> analysisList;
    int position;
    boolean submit = false;
    String exp;

    Button prevButton, nextButton;
    TextView qno, questionText, noneTxt, pTrue, pFalse;
    RadioButton radioTrue, radioFalse;
    ImageView trueImg, falseImg;

    ProgressDialog progressDialog;


    public AnalysisTrueFalseFragment(ArrayList<Analysis> analysisList, int i) {
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
        View view = inflater.inflate(R.layout.fragment_analysis_true_false, container, false);

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

        radioTrue = view.findViewById(R.id.option_true);
        radioFalse = view.findViewById(R.id.option_false);

        trueImg = view.findViewById(R.id.true_img);
        falseImg = view.findViewById(R.id.false_img);


        pTrue = view.findViewById(R.id.percent_true);
        pFalse = view.findViewById(R.id.percent_false);

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

        String ans = result.getAns();
        switch (ans) {
            case "T":
                trueImg.setImageResource(R.drawable.ic_tick);
                break;
            case "F":
                falseImg.setImageResource(R.drawable.ic_tick);
                break;
        }

        pTrue.setText(analysisList.get(position).getP1()+"%");
        pFalse.setText(analysisList.get(position).getP2()+"%");
        noneTxt.setText("Not Attended : "+analysisList.get(position).getPnone()+"%");
        progressDialog.dismiss();

    }
}