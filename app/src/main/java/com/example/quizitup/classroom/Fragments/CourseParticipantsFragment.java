package com.example.quizitup.classroom.Fragments;

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
import android.widget.LinearLayout;

import com.example.quizitup.R;
import com.example.quizitup.classroom.RequestParticipant.RequestParticipant;
import com.example.quizitup.classroom.RequestParticipant.RequestParticipantAdapter;
import com.example.quizitup.quiz.ParticipantModel;
import com.example.quizitup.quiz.QuizParticipantAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class CourseParticipantsFragment extends Fragment {

    RecyclerView recyclerView,requestedRecycler;
    ArrayList<ParticipantModel> participantModels,requestedParticipantModels;
    LinearLayout requestLayout;
    ProgressDialog progressDialog;
    String code;
    boolean isOwner;

    FirebaseDatabase database;
    DatabaseReference participantRef;
    HashMap<String,String> intentMap;

    public CourseParticipantsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants2, container, false);

        Bundle bundle = this.getArguments();
        code = bundle.getString("code");
        isOwner = bundle.getBoolean("isOwner");

        requestLayout = view.findViewById(R.id.requestedLayout);

        recyclerView = view.findViewById(R.id.participantRecycler);
        requestedRecycler = view.findViewById(R.id.requestedRecycler);
        recyclerView.setHasFixedSize(true);
        requestedRecycler.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        requestedRecycler.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        requestedParticipantModels = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        participantRef = database.getReference("Classrooms").child(code).child("Participants");
        database.getReference("Classrooms").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String creator = snapshot.child("creator").getValue().toString();
                String createdBy = snapshot.child("createdBy").getValue().toString();
                String className = snapshot.child("name").getValue().toString();
                String uid = FirebaseAuth.getInstance().getUid();
                String classCode = code;

                intentMap = new HashMap<>();
                intentMap.put("creator",creator);
                intentMap.put("className",className);
                intentMap.put("uid",uid);
                intentMap.put("classCode",classCode);

                boolean isOpen = Boolean.parseBoolean(snapshot.child("isOpen").getValue().toString());

                if (!isOpen && createdBy.equals(uid)) {
                    populateRequiredDataset();
                    requestLayout.setVisibility(View.VISIBLE);
                } else {
                    requestLayout.setVisibility(View.GONE);
                    populateDataset();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateRequiredDataset() {
        progressDialog = ProgressDialog.show(getContext(),"Please Wait","Loading Participants Details");

        DatabaseReference requestRef = database.getReference("Classrooms").child(code).child("Request_Participant");
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<RequestParticipant> requestParticipantArrayList = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String name = snap.child("uname").getValue().toString();
                    boolean accepted = Boolean.parseBoolean(snap.child("accepted").getValue().toString());
                    boolean ignore = Boolean.parseBoolean(snap.child("ignore").getValue().toString());

                    if (!accepted && !ignore)
                        requestParticipantArrayList.add(new RequestParticipant(snap.getKey(),name));

                }
                RequestParticipantAdapter participantAdapter = new RequestParticipantAdapter(requestParticipantArrayList,getContext(),intentMap);
                requestedRecycler.setAdapter(participantAdapter);

                progressDialog.dismiss();
                populateDataset();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void populateDataset() {
        progressDialog = ProgressDialog.show(getContext(),"Please Wait","Loading Participants Details");
        int i = 0;
        participantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                participantModels = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String name = snap.child("uname").getValue().toString();
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