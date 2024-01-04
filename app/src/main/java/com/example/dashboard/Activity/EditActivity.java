package com.example.dashboard.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ProductDomain objectProduct;
    private ImageView backBtn, imgProduct;
    private EditText edtNameProduct, edtPrice, edtdescribe, edtProductId, edtScore, edtReview, edtCategoryProduct;
    private Button btnDone, chooseFile_edt;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.dashboard.R.layout.activity_edit);

        initView();
        getData();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chooseFile_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });
    }

    private void getData() {
        objectProduct = (ProductDomain) getIntent().getSerializableExtra("object");

        String pic = objectProduct.getPicURL();
        Glide.with(EditActivity.this)
                .load(pic)
                .into(imgProduct);

        edtProductId.setText(String.valueOf(objectProduct.getId()));
        edtNameProduct.setText(objectProduct.getName());
        edtPrice.setText(objectProduct.getPrice());
        edtScore.setText(String.valueOf(objectProduct.getScore()));
        edtReview.setText(String.valueOf(objectProduct.getReview()));
        edtdescribe.setText(objectProduct.getDescription());
        edtCategoryProduct.setText(objectProduct.getCategory());
    }

    private void initView() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        backBtn = findViewById(R.id.backBtn);
        edtNameProduct = findViewById(R.id.edtNameProduct);
        edtPrice = findViewById(R.id.edtPrice);
        edtdescribe = findViewById(R.id.edtdescribe);
        edtScore = findViewById(R.id.edtScore);
        edtReview = findViewById(R.id.edtReview);
        edtProductId = findViewById(R.id.edtProductId);
        edtCategoryProduct = findViewById(R.id.edtCategoryProduct);
        btnDone = findViewById(R.id.btnDone);
        chooseFile_edt = findViewById(R.id.chooseFile_edt);
        imgProduct = findViewById(R.id.imgProduct);
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .into(imgProduct);
        }
    }

    private void updateProduct() {
        String name = edtNameProduct.getText().toString();
        String price = edtPrice.getText().toString();
        String describe = edtdescribe.getText().toString();
        String id = edtProductId.getText().toString();
        String score = edtScore.getText().toString();
        String review = edtReview.getText().toString();
        String category = edtCategoryProduct.getText().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("id", id);
        updatedData.put("name", name);
        updatedData.put("price", price);
        updatedData.put("description", describe);
        updatedData.put("score", Double.parseDouble(score));
        updatedData.put("review", Integer.parseInt(review));
        updatedData.put("category", category);

        if (imageUri != null) {
            uploadImage(updatedData);
        } else {
            updateProductData(null, updatedData);
        }
    }

    private void uploadImage(Map<String, Object> updatedData) {
        StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateProductData(uri.toString(), updatedData);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditActivity.this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProductData(String imageUrl, Map<String, Object> updatedData) {
        if (imageUrl != null) {
            updatedData.put("picURL", imageUrl);
        }

        String id = objectProduct.getId();

        firestore.collection("products")
                .document(id)
                .update(updatedData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditActivity.this, "Sản phẩm đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e("EditActivity", "Lỗi khi cập nhật tài liệu", task.getException());
                        Toast.makeText(EditActivity.this, "Không thể cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }
}