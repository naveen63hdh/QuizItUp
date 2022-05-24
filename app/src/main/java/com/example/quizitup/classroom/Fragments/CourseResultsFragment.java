package com.example.quizitup.classroom.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.quizitup.Results.ParticipantResultActivity;
import com.example.quizitup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CourseResultsFragment extends Fragment {

    RadioButton allRadio, bestOfRadio;
    Spinner bestOfSpinner;
    RadioGroup typeGrp;
    Button fetchResultBtn;
    LinearLayout spinnerLayout;

    FirebaseDatabase database;
    DatabaseReference classRef,quizRef;

    String code,uid,uname;
    boolean isOwner;
    long size = 0;
    double total_percentage = 0;
    ArrayList<String> quizList;

    ProgressDialog progressDialog;
    public CourseResultsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        allRadio = view.findViewById(R.id.all_radio);
        bestOfRadio = view.findViewById(R.id.best_of_radio);
        typeGrp = view.findViewById(R.id.type_grp);
        bestOfSpinner = view.findViewById(R.id.spinner);
        fetchResultBtn = view.findViewById(R.id.fetch_result);
        spinnerLayout = view.findViewById(R.id.spinner_layout);

        quizList = new ArrayList<>();
        uid = FirebaseAuth.getInstance().getUid();

        Bundle bundle = this.getArguments();
        code = bundle.getString("code");
        isOwner = bundle.getBoolean("isOwner");

        progressDialog = ProgressDialog.show(getContext(),"Loading","Please wait...");

        database = FirebaseDatabase.getInstance();
        classRef = database.getReference("Classrooms").child(code);
        quizRef = database.getReference("Quiz");

        typeGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (bestOfRadio.isChecked()) {
                    spinnerLayout.setVisibility(View.VISIBLE);
                } else {
                    spinnerLayout.setVisibility(View.GONE);
                }
            }
        });

        classRef.child("Quiz").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = snapshot.getChildrenCount();
                if (size == 0) {
                    fetchResultBtn.setEnabled(false);
                } else {
                    fetchResultBtn.setEnabled(true);
                    String[] array = new String[(int) size];
                    for (int i = 1; i <= size; i++) {
                        array[i] = String.valueOf(i);
                    }
                    ArrayAdapter ad
                            = new ArrayAdapter(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            array);

                    ad.setDropDownViewResource(
                            android.R.layout
                                    .simple_spinner_dropdown_item);

                    bestOfSpinner.setAdapter(ad);

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        quizList.add(snap.getKey());
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fetchResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(getContext(),"Fetching Result","Please Wait");
                if (isOwner) {

                } else {
                    fetchResultFor(uid,(allRadio.isChecked()?size:Integer.parseInt(bestOfSpinner.getSelectedItem().toString())),true);
                }
            }
        });

        return view;
    }

    private void fetchResultFor(String uid, long l,boolean isLast) {

        HashMap<String,Object> returnMap = new HashMap<>();
        HashMap<String,Double> percentageMap = new HashMap<>();


        classRef.child("Participants").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname = snapshot.child("uname").getValue().toString();
                returnMap.put("name",uname);
                for (String quiz : quizList) {
                    quizRef.child(quiz).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("quiz_name").getValue().toString();
                            int total = Integer.parseInt(snapshot.child("total").getValue().toString());
                            double score = Double.parseDouble(snapshot.child("Participants").child(uid).child("score").getValue().toString());
                            double percentage = (score/total) *100;
                            percentageMap.put(name,percentage);
                            returnMap.put("total",total);


                            LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                percentageMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                        .limit(l).forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
                            }

                            for (double val:percentageMap.values()) {
                                total_percentage += val;
                            }
                            returnMap.put("scored",total_percentage);
                            returnMap.put("marks",percentageMap);
                            if (isLast) {
                                progressDialog.dismiss();
                                if (isOwner) {
//                                    TODO Generate Excel file
                                } else {
                                    Intent intent = new Intent(getContext(), ParticipantResultActivity.class);
                                    intent.putExtra("output",returnMap);
                                    getContext().startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}