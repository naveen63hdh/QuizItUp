package com.example.quizitup.quiz;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quizitup.SignInFragment;
import com.example.quizitup.SignUpFragment;

public class QuizHomeAdapter  extends FragmentStateAdapter {

    boolean isCompleted,isStudent;
    public QuizHomeAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,boolean isCompleted,boolean isStudent) {
        super(fragmentManager, lifecycle);
        this.isCompleted = isCompleted;
        this.isStudent = isStudent;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1)
            return new ParticipantsFragment();
        else {
            if(isCompleted)
                return new QuizCompletedHomeFragment();
            return new QuizHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

