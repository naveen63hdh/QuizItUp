package com.example.quizitup.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizitup.R;
import com.example.quizitup.quiz.QuizHomeActivity;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.QuizViewHolder> {

    ArrayList<QuizHomeModel> quizHomeModels;
    Context context;
    public HomeAdapter(ArrayList<QuizHomeModel> quizHomeModels, Context context) {
        this.quizHomeModels = quizHomeModels;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_quiz_home, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        QuizViewHolder viewHolder = new QuizViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {

        holder.llayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                switch (holder.getAdapterPosition()) {
                    case 0:
                        intent = new Intent(context, QuizHomeActivity.class);
                        intent.putExtra("completed",false);
                        context.startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(context, QuizHomeActivity.class);
                        intent.putExtra("completed",true);
                        context.startActivity(intent);
                        break;
                }
            }
        });
        holder.nameTxt.setText(quizHomeModels.get(position).getQuizName());
        String time = quizHomeModels.get(position).getStartTime()+" - "+quizHomeModels.get(position).getEndTime();
        holder.timeTxt.setText(time);
        holder.dateTxt.setText(quizHomeModels.get(position).getqDate());
        holder.statusTxt.setText(quizHomeModels.get(position).getqStatus());
    }

    @Override
    public int getItemCount() {
        return quizHomeModels.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llayout;
        TextView nameTxt,timeTxt,dateTxt,statusTxt;
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            llayout= itemView.findViewById(R.id.linear_layout);
            nameTxt = itemView.findViewById(R.id.quizName);
            timeTxt = itemView.findViewById(R.id.quizTime);
            dateTxt = itemView.findViewById(R.id.quizDate);
            statusTxt = itemView.findViewById(R.id.quizStatus);
        }
    }
}
