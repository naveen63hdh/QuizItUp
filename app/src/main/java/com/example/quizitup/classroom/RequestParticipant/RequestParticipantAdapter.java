package com.example.quizitup.classroom.RequestParticipant;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizitup.R;
import com.example.quizitup.classroom.JoinClassroom;
import com.example.quizitup.mail.GMailSender;
import com.example.quizitup.question.QuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RequestParticipantAdapter extends RecyclerView.Adapter<RequestParticipantAdapter.RequestViewHolder> {

    ArrayList<RequestParticipant> requestParticipantArrayList;
    Context context;
    HashMap<String,String> intentMap;
    DatabaseReference classRef,userRef,uRef,quizRef;
    ProgressDialog progressDialog;

    long count = 0;
    int size = 0;
    int qcount = 0;
    String email;

    public RequestParticipantAdapter(ArrayList<RequestParticipant> requestParticipantArrayList, Context context, HashMap<String, String> intentMap) {
        this.requestParticipantArrayList = requestParticipantArrayList;
        this.context = context;
        this.intentMap = intentMap;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_participant_request,parent,false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestParticipant requestParticipant = requestParticipantArrayList.get(position);
        classRef = FirebaseDatabase.getInstance().getReference("Classrooms");
        String uid = requestParticipant.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Classroom");
        uRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        quizRef = FirebaseDatabase.getInstance().getReference("Quiz");



        Log.i("LOG_CHECK", String.valueOf(intentMap));

        uRef.child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue().toString();
                holder.participantName.setText(requestParticipant.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(context, "Please Wait", "Joining the classroom");
                notifyUsers(email,true);

                HashMap<String, Object> userMapList = new HashMap<>();
                userMapList.put("name", intentMap.get("className"));
                userMapList.put("uname", intentMap.get("creator"));

                HashMap<String, Object> participantMap = new HashMap<>();
                participantMap.put("Name", requestParticipant.getName());
                participantMap.put("isCompleted", 0);
                participantMap.put("score", 0);

                HashMap<String, Object> participantList = new HashMap<>();
                String uname = requestParticipant.getName();
                participantList.put("uname", uname);

                String classCode = intentMap.get("classCode");
                classRef.child(classCode).child("Request_Participant").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        classRef.child(classCode).child("Participants").child(uid).updateChildren(participantList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    userRef.child(classCode).updateChildren(userMapList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                joinAllQuiz(requestParticipant);
//                                            Toast.makeText(context, "Joined " + className + " Successfully", Toast.LENGTH_SHORT).show();

                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
        
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String classCode = intentMap.get("classCode");
                notifyUsers(email,false);
                classRef.child(classCode).child("Request_Participant").child(uid).removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestParticipantArrayList.size();
    }

    void notifyUsers(String recipients,boolean accepted) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("quizitup.official@gmail.com",
                            "Quizitup@123");
                    if (accepted)
                        sender.sendMail("Request Accepted", "Your request to join "+intentMap.get("className")+" has been accepted",
                            "quizitup.official@gmail.com", recipients);
                    else
                        sender.sendMail("Request Rejected", "Your request to join "+intentMap.get("className")+" has been rejected",
                                "quizitup.official@gmail.com", recipients);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }

    private void joinAllQuiz(RequestParticipant requestParticipant) {

        progressDialog = ProgressDialog.show(context, "Please Wait", "Joining all quizzes inside classroom");
        classRef.child(intentMap.get("classCode")).child("Quiz").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    ArrayList<String> quizList = new ArrayList<>();
//                    HashMap<String, Object> participantMap = new HashMap<>();
                    String uid = requestParticipant.getUid();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        HashMap<String, Object> tempMap = new HashMap<>();
                        HashMap<String, Object> tempMap2 = new HashMap<>();
                        tempMap.put("Name", requestParticipant.getName());
                        tempMap.put("isCompleted", 0);
                        tempMap.put("score", 0);
                        String key = snap.getKey();

                        tempMap2.put(uid,tempMap);

                        quizList.add(key);
//                        participantMap.put(key, tempMap2);

                        quizRef.child(key).child("Participants").updateChildren(tempMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    count++;
                                    if(count >= snapshot.getChildrenCount()) {
                                        progressDialog.dismiss();
                                        uploadAllQuestions(quizList);
//                                        Toast.makeText(context, "Joined " + className + " Successfully", Toast.LENGTH_SHORT).show();
//                                        finish();
                                    }

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                }
                else
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        finish();
    }

    private void uploadAllQuestions(ArrayList<String> quizList) {
        progressDialog = ProgressDialog.show(context, "Please Wait", "Creating records for all quiz");
        int size = quizList.size();
        if (size==0)
            progressDialog.dismiss();
        for(String key : quizList) {
            quizRef.child(key).child("Date").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    quizRef.child(key).child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
                        String date = snapshot.getValue().toString();

                        @Override
                        public void onDataChange(@NonNull DataSnapshot quizSnap) {
                            HashMap<String,Object> questions = new HashMap<>();
                            for (DataSnapshot questionSnap : quizSnap.getChildren()) {
                                String qno,ans;
                                Double marks;

                                qno = questionSnap.getKey();
                                ans = questionSnap.child("ans").getValue().toString();
                                marks = Double.valueOf(questionSnap.child("marks").getValue().toString());
                                QuestionModel model = new QuestionModel(marks,ans,"NULL",0);
                                questions.put(qno,model);
                            }
                            date = encodeDate(date);
                            uRef.child("Quiz").child(date).child(key).updateChildren(questions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    qcount++;
                                    if (qcount==size && task.isSuccessful()) {
                                        Toast.makeText(context, "Joined Classroom successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public String encodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("yyyy_MM_dd",Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        
        ImageButton acceptBtn, rejectBtn;
        TextView participantName;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            acceptBtn = itemView.findViewById(R.id.accept_img);
            rejectBtn = itemView.findViewById(R.id.reject_img);
            participantName = itemView.findViewById(R.id.participant_name);
        }
    }
    
    
}
