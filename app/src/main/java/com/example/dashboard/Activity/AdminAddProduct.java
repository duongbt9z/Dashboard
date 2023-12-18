package com.example.dashboard.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminAddProduct extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Spinner Items;
    private ImageView backBtn, imgProduct;
    private EditText edtNameProduct, edtPrice, edtdescribe, edtProductId, edtScore, edtReview;
    private Button add, chooseFile;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        initView();
        addEvent();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Items = findViewById(R.id.Items);
        backBtn = findViewById(R.id.backBtn);
        edtNameProduct = findViewById(R.id.edtNameProduct);
        edtPrice = findViewById(R.id.edtPrice);
        edtdescribe = findViewById(R.id.edtdescribe);
        edtScore = findViewById(R.id.edtScore);
        edtReview = findViewById(R.id.edtReview);
        edtProductId = findViewById(R.id.edtProductId);
        add = findViewById(R.id.addPr);
        chooseFile = findViewById(R.id.chooseFile);
        imgProduct = findViewById(R.id.imgProduct);


    }

    private void addEvent() {
        String[] categories = new String[]{"LapTop", "Điện Thoại", "Smart Watch", "Smart TV"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        Items.setAdapter(adapter);


        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 456);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameProduct = edtNameProduct.getText().toString().trim();
                String price = edtPrice.getText().toString().trim();
                String describe = edtdescribe.getText().toString().trim();
                String productId = edtProductId.getText().toString().trim();
                int review = Integer.parseInt(edtReview.getText().toString().trim());
                double score = Double.parseDouble(edtScore.getText().toString().trim());

                if (nameProduct.isEmpty() || price.isEmpty() || describe.isEmpty() || productId.isEmpty() || review == 0 || score == 0) {
                    Toast.makeText(AdminAddProduct.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedImageUri == null) {
                    Toast.makeText(AdminAddProduct.this, "Vui lòng chọn ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImageToStorage(nameProduct, price, describe, productId, review, score);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 456 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProduct.setImageURI(selectedImageUri);
        }
    }

    private Uri getImageUri() {
        Drawable drawable = imgProduct.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();

            String path = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    "TempImage",
                    null
            );
            return Uri.parse(path);
        }
        return null;
    }

    private void uploadImageToStorage(String name, String price, String description, String productId, int review, double score) {
        imgProduct.setDrawingCacheEnabled(true);
        imgProduct.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgProduct.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String imagePath = "product_images/" + System.currentTimeMillis() + ".jpg";

        StorageReference imageRef = storageReference.child(imagePath);
        imageRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                addProductToFirestore(name, price, description, downloadUri.toString(), productId, review, score);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminAddProduct.this, "Lỗi khi tải ảnh lên Firebase Storage", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProductToFirestore(String name, String price, String description, String imageUrl, String productId, int review, double score) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("price", price);
        productData.put("description", description);
        productData.put("picURL", imageUrl);
        productData.put("category", Items.getSelectedItem().toString());
        productData.put("id", productId);
        productData.put("score", score);
        productData.put("review", review);

        checkDuplicateProductId(productId, productData);
    }

    private void checkDuplicateProductId(String productId, Map<String, Object> productData) {
        firestore.collection("products")
                .whereEqualTo("id", productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            addProductToFirestore(productData);
                        } else {
                            Toast.makeText(AdminAddProduct.this, "ID đã tồn tại, vui lòng chọn ID khác", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AdminAddProduct.this, "Lỗi khi kiểm tra trùng lặp ID", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProductToFirestore(Map<String, Object> productData) {
        firestore.collection("products")
                .add(productData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AdminAddProduct.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminAddProduct.this, "Lỗi khi thêm sản phẩm vào Firestore", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error adding product to Firestore", e);
                    }
                });
    }
}
