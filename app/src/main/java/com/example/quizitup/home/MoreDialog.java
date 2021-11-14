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
                DialogJoinQuiz dialog = new DialogJoinQuiz(context);
                dialog.show();
            }
        });

        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UploadQuestionActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
