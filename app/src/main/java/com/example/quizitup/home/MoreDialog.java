package com.example.quizitup.home;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.quizitup.DialogJoinQuiz;
import com.example.quizitup.R;
import com.example.quizitup.UploadQuestionActivity;
import com.example.quizitup.classroom.AddClassroomActivity;
import com.example.quizitup.classroom.JoinClassroom;

public class MoreDialog extends Dialog {

    Context context;
    LinearLayout createQuiz, joinQuiz;

    public MoreDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_quiz_fab);

        createQuiz = findViewById(R.id.createQuiz);
        joinQuiz = findViewById(R.id.joinQuiz);

        joinQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JoinClassroom.class);
                context.startActivity(intent);
//                DialogJoinQuiz dialog = new DialogJoinQuiz(context);
//                dialog.show();
            }
        });

        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, UploadQuestionActivity.class);
                Intent intent = new Intent(context, AddClassroomActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
