package com.example.quizitup.quiz;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizitup.R;
import com.example.quizitup.home.HomeAdapter;
import com.example.quizitup.home.QuizHomeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ParticipantsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ParticipantModel> participantModels;
    ProgressDialog progressDialog;
    String code;

    FirebaseDatabase database;
    DatabaseReference participantRef;

    public ParticipantsFragment(String code) {
        // Required empty public constructor
        this.code = code;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants, container, false);
        recyclerView = view.findViewById(R.id.participantRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        for(int i=0;i<10;i++)
//            participantModels.add(new ParticipantModel(i,"Naveen-"+i,""));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(),"Please Wait","Loading Participants Details");
        participantModels = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        participantRef = database.getReference("Quiz").child(code).child("Participants");
        populateDataset();
    }

    private void populateDataset() {
        int i = 0;
        participantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String name = snap.child("Name").getValue().toString();
                    String url = "";
                    if (snap.child("url").getValue() != null)
                        url = snap.child("url").getValue().toString();

                    participantModels.add(new ParticipantModel(name,url));
                    QuizParticipantAdapter participantAdapter = new QuizParticipantAdapter(participantModels,getContext());
                    recyclerView.setAdapter(participantAdapter);

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}