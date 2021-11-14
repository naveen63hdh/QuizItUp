package com.example.quizitup;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.quizitup.home.HomeActivity;

public class DialogQCreated extends Dialog {
    String code;
    TextView codeTxt;
    ImageButton clipBtn;
    AppCompatButton button;
    Context context;
    public DialogQCreated(Context context, String code) {
        super(context);
        this.context = context;
        this.code = code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.q_created_dialog);
        codeTxt = findViewById(R.id.codeTxt);
        clipBtn = findViewById(R.id.clipboard_btn);
        button = findViewById(R.id.done_dialog);

//        code = "-MnKCY6g__UiYEy93Srke";
        codeTxt.setText(code);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                getContext().startActivity(intent);
                ((Activity) context).finish();
            }
        });


        clipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("quiz code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
