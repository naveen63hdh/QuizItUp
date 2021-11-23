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
import com.example.quizitup.question.QuestionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizHomeFragment extends Fragment {

    Button startQuiz;
    ImageButton cpyBtn;
    TextView nameTxt,codeTxt,instructionTxt;

    FirebaseDatabase database;
    DatabaseReference quizRef;
    FirebaseAuth auth;

    String code;
    boolean isStudent;
    public QuizHomeFragment(String code, boolean isStudent) {
        this.code = code;
        this.isStudent = isStudent;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_home, container, false);
        startQuiz = view.findViewById(R.id.start_quiz);
        nameTxt = view.findViewById(R.id.quizName);
        codeTxt = view.findViewById(R.id.quiz_code);
        instructionTxt = view.findViewById(R.id.specific_instruction);
        cpyBtn = view.findViewById(R.id.copy_btn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference("Quiz").child(code);


        cpyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("quiz code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QuestionActivity.class);
                intent.putExtra("code",code);
                getContext().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeFrag();

    }

    private void updateStatus() {
    }

    void resumeFrag() {

        ProgressDialog progressDialog = ProgressDialog.show(getContext(),"Please Wait","Loading Details");

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String code = snapshot.getKey();
                String name = snapshot.child("quiz name").getValue().toString();
                String ins = snapshot.child("Description").getValue().toString();
//                String startTime = snapshot.child("Start Time").getValue().toString();
//                String endTime = snapshot.child("End Time").getValue().toString();
//                String endJoinTime = snapshot.child("End Joining Time").getValue().toString();
//                int status_code = Integer.parseInt(snapshot.child("Status").getValue().toString());

                updateStatus();

                nameTxt.setText(name);
                codeTxt.setText(code);
                instructionTxt.setText(ins);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}