package com.example.quizitup.view_result.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.question.McqFragment;
import com.example.quizitup.question.QuestionModel;
import com.example.quizitup.question.TrueFalseFragment;
import com.example.quizitup.view_result.model.Result;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ResultMcqFragment extends Fragment {

    ArrayList<Result> resultList;
    int position;
    boolean submit = false;
    String exp;

    Button prevButton, nextButton;
    TextView qno, questionText, showExplanation;
    RadioButton radioOp1, radioOp2, radioOp3, radioOp4, radioOp5, radioOp6;
    ImageView opImg1, opImg2, opImg3, opImg4, opImg5, opImg6;

    ProgressDialog progressDialog;

    public ResultMcqFragment(ArrayList<Result> resultArrayList, int position) {
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
        View view = inflater.inflate(R.layout.fragment_result_mcq, container, false);

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

        radioOp1 = view.findViewById(R.id.op1);
        radioOp2 = view.findViewById(R.id.op2);
        radioOp3 = view.findViewById(R.id.op3);
        radioOp4 = view.findViewById(R.id.op4);
        radioOp5 = view.findViewById(R.id.op5);
        radioOp6 = view.findViewById(R.id.op6);

        opImg1 = view.findViewById(R.id.ans_img_1);
        opImg2 = view.findViewById(R.id.ans_img_2);
        opImg3 = view.findViewById(R.id.ans_img_3);
        opImg4 = view.findViewById(R.id.ans_img_4);
        opImg5 = view.findViewById(R.id.ans_img_5);
        opImg6 = view.findViewById(R.id.ans_img_6);

//        opImg1.setImageResource(R.drawable.ic_close);
//        opImg2.setImageResource(R.drawable.ic_close);
//        opImg3.setImageResource(R.drawable.ic_close);
//        opImg4.setImageResource(R.drawable.ic_close);
//        opImg5.setImageResource(R.drawable.ic_close);
//        opImg6.setImageResource(R.drawable.ic_close);

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
        } else {
            radioOp1.setText(op);
        }

        op = result.getOp2();
        if (op.equals("NA")) {
            radioOp2.setVisibility(View.GONE);
            opImg2.setVisibility(View.GONE);
        } else {
            radioOp2.setText(op);
        }

        op = result.getOp3();
        if (op.equals("NA")) {
            radioOp3.setVisibility(View.GONE);
            opImg3.setVisibility(View.GONE);
        } else {
            radioOp3.setText(op);
        }

        op = result.getOp4();
        if (op.equals("NA")) {
            radioOp4.setVisibility(View.GONE);
            opImg4.setVisibility(View.GONE);
        } else {
            radioOp4.setText(op);
        }

        op = result.getOp5();
        if (op.equals("NA")) {
            radioOp5.setVisibility(View.GONE);
            opImg5.setVisibility(View.GONE);
        } else {
            radioOp5.setText(op);
        }

        op = result.getOp6();
        if (op.equals("NA")) {
            radioOp6.setVisibility(View.GONE);
            opImg6.setVisibility(View.GONE);
        } else {
            radioOp6.setText(op);
        }

        String choice = result.getChoice();
        if (!choice.equals("NULL")) {
            if (choice.equals(resultList.get(position).getOp1()))
                radioOp1.setChecked(true);
            else if (choice.equals(resultList.get(position).getOp2()))
                radioOp2.setChecked(true);
            else if (choice.equals(resultList.get(position).getOp3()))
                radioOp3.setChecked(true);
            else if (choice.equals(resultList.get(position).getOp4()))
                radioOp4.setChecked(true);
            else if (choice.equals(resultList.get(position).getOp5()))
                radioOp5.setChecked(true);
            else if (choice.equals(resultList.get(position).getOp6()))
                radioOp6.setChecked(true);
        }

        exp = resultList.get(position).getExp();
        progressDialog.dismiss();

    }

}