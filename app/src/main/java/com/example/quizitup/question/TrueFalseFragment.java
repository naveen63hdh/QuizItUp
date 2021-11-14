package com.example.quizitup.question;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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


public class TrueFalseFragment extends Fragment {

    ArrayList<QuestionModel> questions;
    int position;
    boolean submit = false;
    Button prevButton,nextButton;
    TextView qno,questionText;
    RadioGroup trueFalseGroup;
    RadioButton trueRadio,falseRadio;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor ansEditor;

    public TrueFalseFragment(ArrayList<QuestionModel> questions, int position) {
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
        View view =  inflater.inflate(R.layout.fragment_true_false, container, false);

        prevButton = view.findViewById(R.id.previous_btn);
        nextButton = view.findViewById(R.id.next_btn);
        qno = view.findViewById(R.id.question_no);
        questionText = view.findViewById(R.id.question);
        trueFalseGroup = view.findViewById(R.id.radio_group);
        trueRadio = view.findViewById(R.id.option_true);
        falseRadio = view.findViewById(R.id.option_false);

        if(position == questions.size()-1) {
            nextButton.setText("Submit");
            submit = true;
        } else {
            nextButton.setText("Next");
            submit = false;
        }

        // Storing data into SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("TEST_ANS", MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        ansEditor = sharedPreferences.edit();
        getFromPreference();

        // Set Values to UI elements
        QuestionModel q = questions.get(position);
        int no = q.getQno().intValue();
        qno.setText("Q"+no);
        questionText.setText(q.getQuestion());

//        If first question is getting displayed disable previous button
        if(position==0)
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);
//      Next button is disabled unless option is clicked
//        nextButton.setEnabled(false);

        prevButton.setOnClickListener(v -> {
            setToPreference();
            position--;
            switch (questions.get(position).getQType().toUpperCase(Locale.ROOT)) {
                case "M":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new McqFragment(questions,position)).commit();
                    break;
                case "TF":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TrueFalseFragment(questions,position)).commit();
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

//            Toast.makeText(getContext(), "Option Selected", Toast.LENGTH_SHORT).show();
            switch (questions.get(position).getQType().toUpperCase(Locale.ROOT)) {
                case "M":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new McqFragment(questions,position)).commit();
                    break;
                case "TF":
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TrueFalseFragment(questions,position)).commit();
                    break;
            }
        });
        return view;
    }
    private void setToPreference() {
        int ans = trueFalseGroup.getCheckedRadioButtonId();
        ansEditor.putInt(String.valueOf(position), ans);
        ansEditor.commit();
    }

    private void getFromPreference() {
        int ansId = sharedPreferences.getInt(String.valueOf(position), -1);
        if (ansId != -1) {
            if (ansId == R.id.option_true) {
                trueRadio.setChecked(true);
            } else if (ansId == R.id.option_false) {
                falseRadio.setChecked(true);
            }
        }
    }
}