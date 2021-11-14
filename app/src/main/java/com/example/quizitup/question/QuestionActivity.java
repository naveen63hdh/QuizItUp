package com.example.quizitup.question;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.quizitup.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = ".Question.Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));
        actionBar.setTitle("Quiz-1");


        // Check Storage Permission
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        ArrayList<QuestionModel> questionList = readExcelFileFromAssets();
        showList(questionList);
        startQuiz(questionList);
    }
    private void startQuiz(ArrayList<QuestionModel> questionList) {
//        int counter = 0;
        QuestionModel question = questionList.get(0);


//        getIntent().putParcelableArrayListExtra("sag",);
        switch (question.getQType().toUpperCase(Locale.ROOT)) {
            case "M":
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new McqFragment(questionList,0)).commit();
                break;
            case "TF":
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new TrueFalseFragment(questionList,0)).commit();
                break;
        }

    }

    private void showList(ArrayList<QuestionModel> questionList) {
        for(QuestionModel ques:questionList) {
            Log.i("EXCEL_OUTPUT","Qno = "+ques.qno+" Type = "+ques.QType+" Ques = "+ques.question+" OP1 = "+ques.op1+" OP2 = "+ques.op2+" OP3 = "+ques.op3+" OP4 = "+ques.op4+" OP5 = "+ques.op5+" OP6 = "+ques.op6+" ANS = "+ques.ans+" explanation = "+ques.exp+" shuffle = "+ques.shuffle+" Marks = "+ques.marks);
        }
    }


    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(QuestionActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(QuestionActivity.this, new String[]{permission}, requestCode);
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
                Toast.makeText(QuestionActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QuestionActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public ArrayList<QuestionModel> readExcelFileFromAssets() {
        ArrayList<QuestionModel> questionList = new ArrayList<>();
        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel sheet
            myInput = assetManager.open("Skeleton.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            //textView.append("\n");
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    Double qno=0.0, marks=0.0;
                    String QType="", question="", op1="", op2="", op3="", op4="", op5="", op6="", ans="", exp="", shuffle="";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            qno = Double.parseDouble(myCell.toString());
                        } else if (colno == 1) {
                            QType = myCell.toString();
                        } else if (colno == 2) {
                            question = myCell.toString();
                        } else if (colno == 3) {
                            op1 = myCell.toString();
                        } else if (colno == 4) {
                            op2 = myCell.toString();
                        } else if (colno == 5) {
                            op3 = myCell.toString();
                        } else if (colno == 6) {
                            op4 = myCell.toString();
                        } else if (colno == 7) {
                            op5 = myCell.toString();
                        } else if (colno == 8) {
                            op6 = myCell.toString();
                        } else if (colno == 9) {
                            ans = myCell.toString();
                        } else if (colno == 10) {
                            exp = myCell.toString();
                        } else if (colno == 11) {
                            shuffle = myCell.toString();
                        } else if (colno == 12) {
                            marks = Double.parseDouble(myCell.toString());
                        }
                        colno++;
                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    QuestionModel ques = new QuestionModel(qno,QType,question,op1,op2,op3,op4,op5,op6,ans,exp,shuffle,marks);
                    questionList.add(ques);
                    //textView.append( sno + " -- "+ date+ "  -- "+ det+"\n");
                }
                rowno++;
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
        return questionList;
    }

}