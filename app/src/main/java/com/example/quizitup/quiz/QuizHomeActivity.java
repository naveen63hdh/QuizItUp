package com.example.quizitup.quiz;

import static com.example.quizitup.libs.DocumentTreeToPath.getPath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quizitup.LoginAdapter;
import com.example.quizitup.R;
import com.example.quizitup.UploadQuestionActivity;
import com.example.quizitup.quiz.model.Answers;
import com.example.quizitup.quiz.model.ParticipantsAns;
import com.example.quizitup.view_analysis.ViewAnalysisActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuizHomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    QuizHomeAdapter quizHomeAdapter;

    boolean isStudent;
    String quizCode, date, uid;
    int statusCode;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference quizRef, userRef;

    ArrayList<ParticipantsAns> ansList;
    ProgressDialog progressDialog;
    private static final int STORAGE_PERMISSION_CODE = 101;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));
        actionBar.setTitle("Quiz-1");

        quizCode = getIntent().getExtras().getString("code");
        isStudent = getIntent().getExtras().getBoolean("isStudent");
        date = getIntent().getExtras().getString("date");
        statusCode = getIntent().getExtras().getInt("status");

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference().child("Quiz").child(quizCode);
        userRef = database.getReference().child("Users");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.container_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Participants"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm = getSupportFragmentManager();
                quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        statusCode = Integer.parseInt(snapshot.child("Status").getValue().toString());
                        String endTime = snapshot.child("End Time").getValue().toString();
                        int isCompleted = 0;
                        if (isStudent)
                            isCompleted = Integer.parseInt(snapshot.child("Participants").child(uid).child("isCompleted").getValue().toString());
                        if (statusCode != 4) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                            try {
                                String time = timeFormat.format(c.getTime());
                                endTime = timeFormat.format(timeFormat.parse(endTime));
                                Date now = timeFormat.parse(time);
                                Date end = timeFormat.parse(endTime);
                                if (now.compareTo(end) >= 0) {
                                    quizRef.child("Status").setValue(4);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        quizHomeAdapter = new QuizHomeAdapter(fm, getLifecycle(), statusCode, isStudent, quizCode, isCompleted);
                        viewPager2.setAdapter(quizHomeAdapter);
                        viewPager2.setCurrentItem(tab.getPosition());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = getSupportFragmentManager();
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusCode = Integer.parseInt(snapshot.child("Status").getValue().toString());
                String endTime = snapshot.child("End Time").getValue().toString();
                int isCompleted = 0;
                if (isStudent)
                    isCompleted = Integer.parseInt(snapshot.child("Participants").child(uid).child("isCompleted").getValue().toString());
                if (statusCode != 4) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                    try {
                        String time = timeFormat.format(c.getTime());
                        endTime = timeFormat.format(timeFormat.parse(endTime));
                        Date now = timeFormat.parse(time);
                        Date end = timeFormat.parse(endTime);
                        if (now.compareTo(end) >= 0) {
                            quizRef.child("Status").setValue(4);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                quizHomeAdapter = new QuizHomeAdapter(fm, getLifecycle(), statusCode, isStudent, quizCode, isCompleted);
                viewPager2.setAdapter(quizHomeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isStudent && statusCode == 4) {
            getMenuInflater().inflate(R.menu.menu_teacher, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.download) {
            // Check Storage Permission
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

            // Pick Path of the file
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);

        } else if (id == R.id.analysis) {
            Intent intent = new Intent(this, ViewAnalysisActivity.class);
            intent.putExtra("code", quizCode);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateAnsDataset() {
        ansList = new ArrayList<>();

        quizRef.child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long partCount = snapshot.getChildrenCount();
                for (DataSnapshot participantSnap : snapshot.getChildren()) {
                    String uid = participantSnap.getKey();
                    String name = participantSnap.child("Name").getValue().toString();
                    String email = participantSnap.child("Email").getValue().toString();
                    String score = participantSnap.child("score").getValue().toString();

                    userRef.child(uid).child("Quiz").child(encodeDate(date)).child(quizCode).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<Answers> answerList = new ArrayList<>();
                            for (DataSnapshot ansSnap : snapshot.getChildren()) {
                                String qno = ansSnap.getKey();
                                String choice = ansSnap.child("choice").getValue().toString();
                                if (choice.equals("NULL"))
                                    choice = "NA";
                                if (choice.equals("T"))
                                    choice = "True";
                                if (choice.equals("F"))
                                    choice = "False";
                                answerList.add(new Answers(qno, choice));
                            }

                            ParticipantsAns participantsAns = new ParticipantsAns(name, email, uid, score, answerList);
                            Log.i("PARTICIPANT_ANS", participantsAns.toString());
                            ansList.add(participantsAns);
                            if (ansList.size() == partCount) {
                                generateExcel();
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

    private void generateExcel() {
        //Create new workbook
        HSSFWorkbook wb = new HSSFWorkbook();

        Cell c = null;


        //New Sheet
        Sheet sheet = null;
        sheet = wb.createSheet("Result Sheet");
        Row row = sheet.createRow(0);

        //Generate column Heading
        c = row.createCell(0);
        c.setCellValue("Student Email");

        c = row.createCell(1);
        c.setCellValue("Student Name");

        c = row.createCell(2);
        c.setCellValue("Marks");

        int i = 3;
        ArrayList<Answers> questions = ansList.get(0).getAnswersList();

        // Excel Columns for each question
        for (Answers ans : questions) {
            c = row.createCell(i);
            c.setCellValue(ans.getQno());
            i++;
        }

        int rows = 1;
        for (ParticipantsAns participantsAns : ansList) {
            Row bodyRow = sheet.createRow(rows);

            c = bodyRow.createCell(0);
            c.setCellValue(participantsAns.getEmail());

            c = bodyRow.createCell(1);
            c.setCellValue(participantsAns.getName());

            c = bodyRow.createCell(2);
            c.setCellValue(participantsAns.getScore());

            i = 3;
            for (Answers ans : participantsAns.getAnswersList()) {
                c = bodyRow.createCell(i);
                c.setCellValue(ans.getAnswers());
                i++;
            }
            rows++;
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this, "File Saved Successfully", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
//                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        progressDialog.dismiss();
    }

    public String encodeDate(String date) {
        try {
            Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9999) {
            // Convert Document Tree to Path
            Uri uri = data.getData();
            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
            String path = "";
            path = getPath(this, docUri);
//            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

            file = new File(path, quizCode + ".xls");
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Questions.xls");

            progressDialog = ProgressDialog.show(this, "Please Wait", "Generating Report");
            populateAnsDataset();
        }
    }
}