package com.example.quizitup.quiz;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quizitup.SignInFragment;
import com.example.quizitup.SignUpFragment;

public class QuizHomeAdapter  extends FragmentStateAdapter {

    boolean isStudent;
    String code;
    int status;
    public QuizHomeAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,int status,boolean isStudent,String code) {
        super(fragmentManager, lifecycle);
        this.status = status;
        this.isStudent = isStudent;
        this.code = code;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1)
            return new ParticipantsFragment(code);
        else {
            if(status==4 && isStudent)
                return new QuizCompletedHomeFragment(code);
            return new QuizHomeFragment(code,isStudent);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

