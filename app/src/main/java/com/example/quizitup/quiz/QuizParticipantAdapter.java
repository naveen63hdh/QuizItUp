package com.example.quizitup.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizitup.R;
import com.example.quizitup.home.HomeAdapter;
import com.example.quizitup.home.QuizHomeModel;

import java.util.ArrayList;

public class QuizParticipantAdapter extends RecyclerView.Adapter<QuizParticipantAdapter.ParticipantViewHolder> {

    ArrayList<ParticipantModel> participantModel;
    Context context;

    public QuizParticipantAdapter(ArrayList<ParticipantModel> participantModel, Context context) {
        this.participantModel = participantModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_participant, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ParticipantViewHolder viewHolder = new ParticipantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        holder.nameTxt.setText(participantModel.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return participantModel.size();
    }

    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView userImg;

        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.participant_name);
            userImg = itemView.findViewById(R.id.user_image);
        }
    }
}
