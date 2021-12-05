package com.example.quizitup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.quizitup.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    ImageView logoView;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoView = findViewById(R.id.logo);

        auth = FirebaseAuth.getInstance();

        int splashTimeOut = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if (auth.getUid() != null) {
                    i = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    i = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, splashTimeOut);

        Animation myAnim= AnimationUtils.loadAnimation(this,R.anim.anim_splash);
        logoView.startAnimation(myAnim);
    }
}