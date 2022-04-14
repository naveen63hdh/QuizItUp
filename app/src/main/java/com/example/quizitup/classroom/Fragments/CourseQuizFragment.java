package com.example.quizitup.classroom.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.quizitup.R;
import com.example.quizitup.UploadQuestionActivity;
import com.example.quizitup.classroom.AddClassroomActivity;
import com.example.quizitup.home.HomeActivity;
import com.example.quizitup.home.HomeAdapter;
import com.example.quizitup.home.MoreDialog;
import com.example.quizitup.home.QuizHomeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CourseQuizFragment extends Fragment {

    FloatingActionButton moreFab;
    RecyclerView recyclerView;
    ArrayList<QuizHomeModel> quizHomeModels;
    FirebaseDatabase database;
    DatabaseReference userReference, classReference, statusReference, quizReference;
    FirebaseAuth auth;
    String uid, classId;
    HashMap<Integer, String> status_map;

    ProgressDialog progressDialog;

    public CourseQuizFragment() {
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
        View view = inflater.inflate(R.layout.fragment_quizzes, container, false);

        Bundle bundle = this.getArguments();
        classId = bundle.getString("classId");

        moreFab = view.findViewById(R.id.more_btn);
        recyclerView = view.findViewById(R.id.contentRecycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("Users").child(uid);
        classReference = database.getReference("Classrooms").child(classId);
        quizReference = database.getReference("Quiz");
        statusReference = database.getReference("Status");

        moreFab.setVisibility(View.GONE);
        classReference.child("createdBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals(uid)) {
                    moreFab.setVisibility(View.VISIBLE);
                } else {
                    moreFab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        moreFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadQuestionActivity.class);
                intent.putExtra("classId", classId);
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    private void populateRecycler() {
        List<String> quizCodes = new ArrayList<>();
        classReference.child("Quiz").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot quiz : snapshot.getChildren()) {
                        String code = quiz.getKey();
                        quizCodes.add(code);

                    }

                    quizReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                Iterable<DataSnapshot> quizzes_snapshot = snapshot.getChildren();
                                for (DataSnapshot quiz : quizzes_snapshot) {
                                    String code = quiz.getKey();

                                    if (quizCodes.contains(code)) {

                                        String name, startTime, endTime, status, created_by;
                                        boolean isStudent;
                                        int status_code;
                                        if (quiz.child("quiz name").getValue() != null) {
                                            name = quiz.child("quiz name").getValue().toString();
                                            startTime = quiz.child("Start Time").getValue().toString();
                                            endTime = quiz.child("End Time").getValue().toString();
                                            status_code = Integer.parseInt(quiz.child("Status").getValue().toString());
                                            created_by = quiz.child("Created by").getValue().toString();
                                            isStudent = !created_by.equals(uid);
                                            String quiz_date = quiz.child("Date").getValue().toString();

                                            quizReference.child(code).child("Status").setValue(status_code);
                                            status_code = updateStatus(status_code, quiz_date, startTime, endTime);
                                            quizReference.child(code).child("Status").setValue(status_code);
                                            status = status_map.get(status_code);
                                            quizHomeModels.add(new QuizHomeModel(isStudent, name, code, startTime, endTime, quiz_date, status, status_code));

                                        }
                                    }
                                }
                                HomeAdapter homeAdapter = new HomeAdapter(classId,quizHomeModels, getContext());
                                recyclerView.setAdapter(homeAdapter);
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else{
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        quizHomeModels = new ArrayList<>();
        progressDialog = ProgressDialog.show(getContext(), "", "Loading Please wait");
        status_map = new HashMap<>();
        statusReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot statusSnapshot : snapshot.getChildren()) {
                        int code = Integer.parseInt(statusSnapshot.getKey());
                        String status = statusSnapshot.getValue().toString();
                        status_map.put(code, status);
                    }
                    populateRecycler();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int updateStatus(int status_code, String quiz_date, String startTime, String endTime) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        today = decodeToDate(encodeDate(today));
        Date quiz = decodeToDate(quiz_date);
        Date now = null, start = null, end = null, endJoin = null;

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        try {
            String time = timeFormat.format(c.getTime());
            startTime = timeFormat.format(timeFormat.parse(startTime));
            endTime = timeFormat.format(timeFormat.parse(endTime));
//            endJoinTime = timeFormat.format(timeFormat.parse(endJoinTime));
            now = timeFormat.parse(time);
            start = timeFormat.parse(startTime);
            end = timeFormat.parse(endTime);
//            endJoin = timeFormat.parse(endJoinTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (today.compareTo(quiz) > 0) {
            if (status_code == 1) {
                status_code = 4;
            }
        } else if (today.compareTo(quiz) == 0) {
//            if (now.compareTo(start)>=0 && status_code<2)
//                status_code = 2;
//            else
            if (status_code==1) {
                if (now.compareTo(start) >= 0)
                    status_code = 2;
                if (now.compareTo(end) >= 0)
                    status_code = 4;
            }
        }


        Log.i("TIME_FORMATTER_Start", start.toString());
        Log.i("TIME_FORMATTER_Now", now.toString());
        return status_code;
    }

    private String encodeDate(Date date) {
        String d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
        return d;
    }

    private String decodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private Date decodeToDate(String date) {
        Date d = null;
        try {
            d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}