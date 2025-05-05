package com.example.ngpadminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImage extends AppCompatActivity {

    private Spinner imageCategory;
    private CardView selectImage;
    private Button uploadImage;
    private ImageView galleryImageView;

    private String category;
    private static final int REQ = 1;
    private Bitmap bitmap;
    private ProgressDialog pd;

    private DatabaseReference reference;
    private StorageReference storageReference;
    private String downloadUrl = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // UI Component Bindings
        selectImage = findViewById(R.id.addGalleryImage);
        imageCategory = findViewById(R.id.image_category);
        uploadImage = findViewById(R.id.uploadImageBtn);
        galleryImageView = findViewById(R.id.galleryImageView);

        // Firebase Initialization
        reference = FirebaseDatabase.getInstance().getReference().child("gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("gallery");

        pd = new ProgressDialog(this);

        // Spinner Configuration
        String[] items = new String[]{"Select Category", "Inauguration", "Independence Day", "Other Events"};
        imageCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));

        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = "Select Category";
            }
        });

        selectImage.setOnClickListener(view -> openGallery());

        uploadImage.setOnClickListener(view -> {
            if (bitmap == null) {
                Toast.makeText(UploadImage.this, "Please select an image", Toast.LENGTH_SHORT).show();
            } else if (category.equals("Select Category")) {
                Toast.makeText(UploadImage.this, "Please select a valid image category", Toast.LENGTH_SHORT).show();
            } else {
                pd.setMessage("Uploading...");
                pd.show();
                uploadImageToFirebase();
            }
        });
    }

    private void uploadImageToFirebase() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();

        final StorageReference filePath = storageReference.child(System.currentTimeMillis() + ".jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadUrl = uri.toString();
            uploadImageData();
        })).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(UploadImage.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImageData() {
        final String uniqueKey = reference.child(category).push().getKey();
        reference.child(category).child(uniqueKey).setValue(downloadUrl)
                .addOnSuccessListener(unused -> {
                    pd.dismiss();
                    Toast.makeText(UploadImage.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(UploadImage.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        galleryImageView.setImageResource(0);
        bitmap = null;
        imageCategory.setSelection(0);
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                galleryImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
