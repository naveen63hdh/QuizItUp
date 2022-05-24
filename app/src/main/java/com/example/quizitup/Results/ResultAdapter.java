package com.example.quizitup.Results;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizitup.R;
import com.example.quizitup.home.HomeAdapter;
import com.example.quizitup.home.QuizHomeModel;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    ArrayList<ResultModel> resultList;
    Context context;

    ResultAdapter(ArrayList<ResultModel> resultList,Context context) {
        this.resultList = resultList;
        this.context = context;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_quiz_marks,parent,false);
        ResultViewHolder resultViewHolder = new ResultViewHolder(view);
        return resultViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultModel resultModel = resultList.get(position);
        holder.nameTxt.setText(resultModel.getQuiz());
        holder.scoreTxt.setText(resultModel.getScore()+"%");
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


    static class ResultViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, scoreTxt;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.quizName);
            scoreTxt = itemView.findViewById(R.id.marksTxt);
        }
    }
}
