package com.example.quizitup.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.quizitup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddClassroomActivity extends AppCompatActivity {

    EditText classNameTxt, classDescriptionTxt, usernameHintTxt;
    RadioGroup classType;
    RadioButton openRadio, restrictedRadio;
    Button submitBtn;

    String uid;
    DatabaseReference userReference,classroomReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classroom);

//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        classNameTxt = findViewById(R.id.classroomTxt);
        classDescriptionTxt = findViewById(R.id.descriptionTxt);
        usernameHintTxt = findViewById(R.id.usernameTxt);
        classType = findViewById(R.id.classType);
        openRadio = findViewById(R.id.openRadio);
        restrictedRadio = findViewById(R.id.restrictedRadio);
        submitBtn = findViewById(R.id.create_btn);

        

        uid = FirebaseAuth.getInstance().getUid();
        classroomReference = FirebaseDatabase.getInstance().getReference("Classrooms");
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = ProgressDialog.show(AddClassroomActivity.this,"Loading...","Please wait while creating classroom");

                userReference.child(uid).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        submitToDatabase(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void submitToDatabase(String uname) {
        //                Retrieval of Parameters
        String key = classroomReference.child(uid).push().getKey();
        String name = classNameTxt.getText().toString();
        String description = classDescriptionTxt.getText().toString();
        String nameHint = usernameHintTxt.getText().toString();
        boolean isOpen;
        isOpen = openRadio.isChecked();

//                Put User tree Values To Hashmap
        HashMap<String,Object> userTree = new HashMap<>();
        HashMap<String,String> userSubTree = new HashMap<>();
        userSubTree.put("name",name);
        userSubTree.put("uname",uname);
        userTree.put(key,userSubTree);

//                Put Classroom tree values to Hashmap
        HashMap<String,Object> classroomTree = new HashMap<>();
        HashMap<String,Object> classroomSubTree = new HashMap<>();
        classroomSubTree.put("name",name);
        classroomSubTree.put("desc",description);
        classroomSubTree.put("hint",nameHint);
        classroomSubTree.put("createdBy",uid);
        classroomSubTree.put("creator",uname);
        classroomSubTree.put("isOpen",isOpen);
        classroomTree.put(key,classroomSubTree);


        classroomReference.updateChildren(classroomTree).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userReference.child(uid).child("Classroom").updateChildren(userTree).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(AddClassroomActivity.this, "Course Created Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AddClassroomActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddClassroomActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}