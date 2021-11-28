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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.question.QuestionActivity;
import com.example.quizitup.view_analysis.ViewAnalysisActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuizHomeFragment extends Fragment {

    Button startQuiz;
    ImageButton cpyBtn;
    TextView nameTxt, codeTxt, instructionTxt;

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

                quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int status = Integer.parseInt(snapshot.child("Status").getValue().toString());
                        if (isStudent) {
                            if (status <= 2) {
                                Toast.makeText(getContext(), "Your Teacher haven't started the quiz yet", Toast.LENGTH_SHORT).show();
                            } else if (status == 3) {
                                Intent intent = new Intent(getContext(), QuestionActivity.class);
                                intent.putExtra("code", code);
                                getContext().startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Quiz Has been ended", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            int score_type = Integer.parseInt(snapshot.child("Score Type").getValue().toString());
                            if (status == 1)
                                Toast.makeText(getContext(), "Quiz Hasn't Started yet", Toast.LENGTH_SHORT).show();
                            else if (status == 2) {
                                quizRef.child("Status").setValue(3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Quiz Has been started", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Please Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (status == 3) {
                                if (score_type == 1)
                                    Toast.makeText(getContext(), "Score has been released", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getContext(), "Score can be released only after quiz ended", Toast.LENGTH_SHORT).show();
                            } else {
                                if (score_type == 0) {
                                    quizRef.child("Score Type").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Score has been released", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Please Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Score has been released", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBtn();
        resumeFrag();
    }

    private void updateBtn() {
        if (isStudent) {
            startQuiz.setText("Start Quiz");
        } else {
            quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = Integer.parseInt(snapshot.child("Status").getValue().toString());
                    if (status <= 2)
                        startQuiz.setText("Start Quiz For Students");
                    else {
                        startQuiz.setText("Release Answers");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void resumeFrag() {

        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Loading Details");

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String code = snapshot.getKey();
                String name = snapshot.child("quiz name").getValue().toString();
                String ins = snapshot.child("Description").getValue().toString();
                String endTime = snapshot.child("End Time").getValue().toString();
//                String endJoinTime = snapshot.child("End Joining Time").getValue().toString();
                int status_code = Integer.parseInt(snapshot.child("Status").getValue().toString());

//  --------------------------------------- Code TO Update Status to End -------------------------------------------------
                if (status_code != 4) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                    try {
                        String time = timeFormat.format(c.getTime());
                        endTime = timeFormat.format(timeFormat.parse(endTime));
                        Date now = timeFormat.parse(time);
                        Date end = timeFormat.parse(endTime);
                        if (now.compareTo(end) >= 0) {
                            quizRef.child("Status").setValue(4);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//  --------------------------------------- Code TO Update Status to End -------------------------------------------------
                } else {
//                       TODO Quiz Ended
                }

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