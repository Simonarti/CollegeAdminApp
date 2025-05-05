package com.example.ngpadminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class UploadPdf extends AppCompatActivity {

    private CardView addPdf;
    private EditText pdfTitle;
    private Button uploadPdfBtn;
    private TextView pdfTextView;

    private final int REQ = 1;
    private Uri pdfData;
    private String pdfName = "";
    private String title = "";

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ProgressDialog pd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("pdf");
        storageReference = FirebaseStorage.getInstance().getReference().child("pdf");

        pd = new ProgressDialog(this);

        // Bind views
        addPdf = findViewById(R.id.addPdf);
        pdfTitle = findViewById(R.id.PdfTitle);
        uploadPdfBtn = findViewById(R.id.uploadPdfBtn);
        pdfTextView = findViewById(R.id.pdfTextView);

        addPdf.setOnClickListener(view -> openFilePicker());

        uploadPdfBtn.setOnClickListener(view -> {
            title = pdfTitle.getText().toString().trim();
            if (title.isEmpty()) {
                pdfTitle.setError("Title required");
                pdfTitle.requestFocus();
            } else if (pdfData == null) {
                Toast.makeText(UploadPdf.this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
            } else {
                uploadPdf();
            }
        });
    }

    private void uploadPdf() {
        pd.setTitle("Uploading");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        StorageReference filePath = storageReference.child(pdfName + "-" + System.currentTimeMillis() + ".pdf");
        filePath.putFile(pdfData)
                .addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl()
                        .addOnSuccessListener(uri -> uploadData(uri.toString()))
                        .addOnFailureListener(e -> {
                            pd.dismiss();
                            Toast.makeText(UploadPdf.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(UploadPdf.this, "PDF Upload Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData(String downloadUrl) {
        String uniqueKey = databaseReference.push().getKey();

        HashMap<String, String> data = new HashMap<>();
        data.put("pdfTitle", title);
        data.put("pdfUrl", downloadUrl);

        databaseReference.child(uniqueKey).setValue(data)
                .addOnSuccessListener(unused -> {
                    pd.dismiss();
                    Toast.makeText(UploadPdf.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(UploadPdf.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        pdfTitle.setText("");
        pdfTextView.setText("No file selected");
        pdfData = null;
        pdfName = "";
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), REQ);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfData = data.getData();
            if (pdfData.toString().startsWith("content://")) {
                try (Cursor cursor = getContentResolver().query(pdfData, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (pdfData.toString().startsWith("file://")) {
                pdfName = new File(pdfData.getPath()).getName();
            }

            pdfTextView.setText(pdfName);
        }
    }
}
