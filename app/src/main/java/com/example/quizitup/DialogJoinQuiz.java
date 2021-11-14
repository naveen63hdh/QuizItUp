package com.example.quizitup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DialogJoinQuiz  extends Dialog {

    TextInputEditText quizCode;
    Button joinBtn,cancelBtn;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String uid, date;
    DatabaseReference reference;

    public DialogJoinQuiz(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_join_quiz);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();

        quizCode = findViewById(R.id.code_txt);
        joinBtn = findViewById(R.id.join_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = quizCode.getText().toString().trim();

                /*
                Check if code is there in quiz tree else toast no quiz found
                 */
                reference.child("Quiz").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String quiz = snapshot.child("quiz name").getValue().toString();
                            date = snapshot.child("Date").getValue().toString();
                            date = encodeDate(date);
                            // TODO Check if date and time is not ended and then join quiz
                            //Code to join quiz
                            reference.child("Users").child(uid).child("Quiz").child(date).child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {
                                    if (snap.exists()) {
                                        Toast.makeText(getContext(), "Already Joined", Toast.LENGTH_SHORT).show();
                                    } else {
                                        reference.child("Users").child(uid).child("Quiz").child(date).child(code).child("quiz code").setValue(code);
                                        reference.child("Users").child(uid).child("Quiz").child(date).child(code).child("quiz name").setValue(quiz);
                                        Toast.makeText(getContext(), "Joined Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    closeDialog();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                            Toast.makeText(getContext(), "Not valid", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void closeDialog() {
        this.dismiss();
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
