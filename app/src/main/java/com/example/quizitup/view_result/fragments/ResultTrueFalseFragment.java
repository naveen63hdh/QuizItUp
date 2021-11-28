package com.example.quizitup.view_result.fragments;

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
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.view_result.model.Result;

import java.util.ArrayList;
import java.util.Locale;

public class ResultTrueFalseFragment extends Fragment {

    ArrayList<Result> resultList;
    int position;

    boolean submit = false;
    String exp;

    Button prevButton, nextButton;
    TextView qno, questionText, showExplanation;
    RadioButton radioTrue, radioFalse;
    ImageView trueImg, falseImg;

    ProgressDialog progressDialog;


    public ResultTrueFalseFragment(ArrayList<Result> resultArrayList, int position) {
        this.resultList = resultArrayList;
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_true_false, container, false);

        prevButton = view.findViewById(R.id.previous_btn);
        nextButton = view.findViewById(R.id.next_btn);

        if (position == resultList.size() - 1) {
            nextButton.setText("Close");
            submit = true;
        } else {
            nextButton.setText("Next");
            submit = false;
        }

        qno = view.findViewById(R.id.question_no);
        questionText = view.findViewById(R.id.question);
        showExplanation = view.findViewById(R.id.showExplanation);

        radioTrue = view.findViewById(R.id.option_true);
        radioFalse = view.findViewById(R.id.option_false);

        trueImg = view.findViewById(R.id.true_img);
        falseImg = view.findViewById(R.id.false_img);

        trueImg.setImageResource(R.drawable.ic_close);
        falseImg.setImageResource(R.drawable.ic_close);

        progressDialog = ProgressDialog.show(getContext(),"","Loading please wait");
        setUI();

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                switch (resultList.get(position).getQType().toUpperCase(Locale.ROOT)) {
                    case "M":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new ResultMcqFragment(resultList, position)).commit();
                        break;
                    case "TF":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new ResultTrueFalseFragment(resultList, position)).commit();
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

                switch (resultList.get(position).getQType().toUpperCase(Locale.ROOT)) {
                    case "M":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new ResultMcqFragment(resultList, position)).commit();
                        break;
                    case "TF":
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new ResultTrueFalseFragment(resultList, position)).commit();
                        break;
                }

            }
        });

        showExplanation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exp.equals("NA"))
                    Toast.makeText(getContext(), "No Explanation Found", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), exp, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setUI() {
        // Set Values to UI elements
        Result result = resultList.get(position);
        // If first question is getting displayed disable previous button
        if (position == 0)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

        int no = result.getQno();
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

        String choice = result.getChoice();
        if (!choice.equals("NULL")) {
            if (choice.equals("T"))
                radioTrue.setChecked(true);
            else if (choice.equals("F"))
                radioFalse.setChecked(true);
        }

        exp = resultList.get(position).getExp();
        progressDialog.dismiss();
    }
}