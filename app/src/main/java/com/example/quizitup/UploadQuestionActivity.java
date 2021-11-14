package com.example.quizitup;

import static com.example.quizitup.libs.DocumentTreeToPath.getPath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.quizitup.libs.DocumentTreeToPath.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class UploadQuestionActivity extends AppCompatActivity {

    TextView downloadTxt;
    Button nextBtn;
    EditText rowTxt;
    ImageButton imageButton;

    Workbook wb;
    String key;

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = ".Question.Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_question);

        nextBtn = findViewById(R.id.next_btn);
        downloadTxt = findViewById(R.id.download_ff);
        rowTxt = findViewById(R.id.row_txt);
        imageButton = findViewById(R.id.upload_btn);

        // Check Storage Permission
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);


//        Set Action bar background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upload Question");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadQuestionActivity.this, QuizDetailsActivity.class);
                intent.putExtra("code",key);
                startActivity(intent);
            }
        });

        downloadTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pick Path of the file
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
// Flow Goes to activity Result
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setType("application/vnd.ms-excel");
                startActivityForResult(i,200);
            }
        });
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(UploadQuestionActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(UploadQuestionActivity.this, new String[]{permission}, requestCode);
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
                Toast.makeText(UploadQuestionActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UploadQuestionActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void generateExcel(View view) {
        // Set Visibility after generating excel
        ProgressDialog progressDialog = ProgressDialog.show(this, "Generating Excel", "Please wait your excel file will be generated in few minutes");

        //Create new workbook
        wb = new HSSFWorkbook();

        Cell c = null;


        //New Sheet
        Sheet sheet = null;
        sheet = wb.createSheet("Sheet1");
        Row row = sheet.createRow(0);

        //Generate column Heading
        c = row.createCell(0);
        c.setCellValue("Q.No");

        c = row.createCell(1);
        c.setCellValue("Question Type* (MCQ - M, True/ false -TF)");

        c = row.createCell(2);
        c.setCellValue("Question*");

        c = row.createCell(3);
        c.setCellValue("Option1(Default NA)");

        c = row.createCell(4);
        c.setCellValue("Option2(Default NA)");

        c = row.createCell(5);
        c.setCellValue("Option3(Default NA)");

        c = row.createCell(6);
        c.setCellValue("Option4(Default NA)");

        c = row.createCell(7);
        c.setCellValue("Option5(Default NA)");

        c = row.createCell(8);
        c.setCellValue("Option6(Default NA)");

        c = row.createCell(9);
        c.setCellValue("Correct Ans* (MCQ- Option number, True or False - T (or) F)");

        c = row.createCell(10);
        c.setCellValue("Explanation(Default NA)");

        c = row.createCell(11);
        c.setCellValue("Shuffle Option(Yes/No)");

        c = row.createCell(12);
        c.setCellValue("Marks*");

        //sheet.setDefaultColumnWidth(15*500);

        // Create Body of excel file
        // Get no of rows needed.
        int no_of_rows = Integer.parseInt(rowTxt.getText().toString());
        for (int i = 1; i <= no_of_rows; i++) {
            Row bodyRow = sheet.createRow(i);

            // Question No
            c = bodyRow.createCell(0);
            c.setCellValue(i);
            //  c.setCellStyle(cs);

            //Question Type
            c = bodyRow.createCell(1);
            c.setCellValue("");
            //c.setCellStyle(cs);

            //Question
            c = bodyRow.createCell(2);
            c.setCellValue("");
            //c.setCellStyle(cs);

            //Op - 1
            c = bodyRow.createCell(3);
            c.setCellValue("NA");
            //c.setCellStyle(cs);

            //Op - 2
            c = bodyRow.createCell(4);
            c.setCellValue("NA");
            //c.setCellStyle(cs);

            //Op - 3
            c = bodyRow.createCell(5);
            c.setCellValue("NA");
            //c.setCellStyle(cs);

            //Op - 4
            c = bodyRow.createCell(6);
            c.setCellValue("NA");
            //c.setCellStyle(cs);

            //Op - 5
            c = bodyRow.createCell(7);
            c.setCellValue("NA");
//            c.setCellStyle(cs);

            //Op - 6
            c = bodyRow.createCell(8);
            c.setCellValue("NA");
//            c.setCellStyle(cs);

            //Correct ANS
            c = bodyRow.createCell(9);
            c.setCellValue("");
//            c.setCellStyle(cs);

            //Explanation
            c = bodyRow.createCell(10);
            c.setCellValue("NA");
            //c.setCellStyle(cs);

            //Shuffle Options
            c = bodyRow.createCell(11);
            c.setCellValue("No");
            //c.setCellStyle(cs);

            //Marks
            c = bodyRow.createCell(12);
            c.setCellValue(1);
//            c.setCellStyle(cs);
        }

        progressDialog.dismiss();
        downloadTxt.setVisibility(View.VISIBLE);
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

            File file = new File(path, "Questions.xls");
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Questions.xls");
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
        } else if (requestCode==200) {
            // Convert Document Tree to Path
            Uri uri = data.getData();
            if (uri != null) {
                //TODO Validate File
                //validateExcel();

                //displaying a progress dialog while upload is going on
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("Quiz");
                DatabaseReference quizRef = databaseReference.push();
                key = quizRef.getKey();
                quizRef.child("Quiz Code").setValue(key);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference("Questions");


                StorageReference riversRef = storageReference.child(key+"/questions.xls");
                riversRef.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                // Get download URL
                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //if the upload is successfull and download url is fetched
                                        //hiding the progress dialog
                                        quizRef.child("Question URL").setValue(uri.toString());

                                        progressDialog.dismiss();
                                        //and displaying a success toast
//                                        Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                        nextBtn.setEnabled(true);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            }

        }
    }
}