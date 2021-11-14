package com.example.quizitup.question;

import static android.content.Context.MODE_PRIVATE;

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

import com.example.quizitup.R;

import java.util.ArrayList;
import java.util.Locale;

public class McqFragment extends Fragment implements View.OnClickListener{

    ArrayList<QuestionModel> questions;
    int position;
    boolean submit = false;
    Button prevButton,nextButton;
    TextView qno,questionText;
    RadioGroup ansGroup;
    RadioButton radioOp1, radioOp2, radioOp3, radioOp4, radioOp5, radioOp6;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor ansEditor;

    public McqFragment(ArrayList<QuestionModel> questions, int position) {
        this.questions = questions;
        this.position = position;
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

        if(position == questions.size()-1) {
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

        // Storing data into SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("TEST_ANS", MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        ansEditor = sharedPreferences.edit();

        // Set Values to UI elements
        QuestionModel q = questions.get(position);
        // If first question is getting displayed disable previous button
        if (position == 0)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

        getFromPreference();
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

        radioOp1.setOnClickListener(this);
        radioOp2.setOnClickListener(this);
        radioOp3.setOnClickListener(this);
        radioOp4.setOnClickListener(this);
        radioOp5.setOnClickListener(this);
        radioOp6.setOnClickListener(this);

        prevButton.setOnClickListener(v -> {
            setToPreference();
            position--;
            switch (questions.get(position).getQType().toUpperCase(Locale.ROOT)) {
                case "M":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new McqFragment(questions, position)).commit();
                    break;
                case "TF":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TrueFalseFragment(questions, position)).commit();
                    break;
            }
        });
        nextButton.setOnClickListener(v -> {

            setToPreference();
            position++;
            if (submit) {
                getActivity().finish();
                return;
            }
            switch (questions.get(position).getQType().toUpperCase(Locale.ROOT)) {
                case "M":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new McqFragment(questions, position)).commit();
                    break;
                case "TF":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TrueFalseFragment(questions, position)).commit();
                    break;
            }
        });
        return view;
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

    private void setToPreference() {
        int ans = ansGroup.getCheckedRadioButtonId();
        ansEditor.putInt(String.valueOf(position), ans);
        ansEditor.commit();
    }

    private void getFromPreference() {
        int ansId = sharedPreferences.getInt(String.valueOf(position), -1);
        if (ansId != -1) {
            switch (ansId) {
                case R.id.op1:
                    radioOp1.setChecked(true);
                    break;
                case R.id.op2:
                    radioOp2.setChecked(true);
                    break;
                case R.id.op3:
                    radioOp3.setChecked(true);
                    break;
                case R.id.op4:
                    radioOp4.setChecked(true);
                    break;
                case R.id.op5:
                    radioOp5.setChecked(true);
                    break;
                case R.id.op6:
                    radioOp6.setChecked(true);
                    break;
            }
        }
    }

    private void enableNext() {
        if (!nextButton.isEnabled())
            nextButton.setEnabled(true);
    }

}