package com.example.quizitup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    LoginAdapter loginAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //        Elements Init

        tabLayout = findViewById(R.id.sign_layout);
        viewPager2 = findViewById(R.id.container_pager);

        //        Setting adapter

        FragmentManager fm = getSupportFragmentManager();
        loginAdapter = new LoginAdapter(fm,getLifecycle());
        viewPager2.setAdapter(loginAdapter);

//        Initializing Tabs for signin and signup
        tabLayout.addTab(tabLayout.newTab().setText("Sign in"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign up"));

        //        on tab selected move to the selected tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        On page change change the tab layout to current layout
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    public void forgetPass(View view) {
//     Go to forget pass if forget pass is clicked on signin fragment
        Intent intent = new Intent(this,ForgetPasswordActivity.class);
        startActivity(intent);

    }
}