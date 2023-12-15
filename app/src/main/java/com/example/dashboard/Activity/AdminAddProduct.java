package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

    Spinner Items;
    ImageView backBtn;
    EditText edtNameProduct, edtPrice, edtdescribe;
    Button add, chooseFile;
    ImageView imgProduct;

    // Firebase
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);
        initView();
        addEvent();
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
        add = findViewById(R.id.addPr);
        chooseFile = findViewById(R.id.chooseFile);
        imgProduct = findViewById(R.id.imgProduct);
    }

    private void addEvent() {
        // Spinner
        String[] danhMuc = new String[]{"LapTop", "Điện Thoại", "Smart Watch", "Smart TV"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, danhMuc);
        Items.setAdapter(adapter);

        // Back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Choose file button
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 456);
            }
        });

        // Add product button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin từ các trường nhập liệu
                String nameProduct = edtNameProduct.getText().toString().trim();
                String price = edtPrice.getText().toString().trim();
                String describe = edtdescribe.getText().toString().trim();

                // Kiểm tra xem đã chọn ảnh từ Gallery chưa
                if (selectedImageUri == null) {
                    Toast.makeText(AdminAddProduct.this, "Vui lòng chọn ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tải ảnh lên Firebase Storage và thêm thông tin vào Firestore
                uploadImageToStorage(nameProduct, price, describe);
            }
        });
    }

    // Sự kiện sau khi chọn ảnh từ Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 456 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProduct.setImageURI(selectedImageUri);
        }
    }

    // Phương thức trợ giúp để lấy URI của ảnh
    private Uri getImageUri() {
        Drawable drawable = imgProduct.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();

            // Lưu bitmap vào một tệp tạm thời và lấy URI
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

    // Tải ảnh lên Firebase Storage và thêm thông tin vào Firestore
    private void uploadImageToStorage(String name, String price, String description) {
        // Chuyển đổi ImageView thành mảng byte
        imgProduct.setDrawingCacheEnabled(true);
        imgProduct.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgProduct.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Đường dẫn lưu trữ trên Firebase Storage
        String imagePath = "product_images/" + System.currentTimeMillis() + ".jpg";

        // Lưu trữ ảnh lên Firebase Storage
        StorageReference imageRef = storageReference.child(imagePath);
        imageRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Lấy URL của ảnh từ Firebase Storage
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Thêm thông tin sản phẩm vào Firestore
                                addProductToFirestore(name, price, description, downloadUri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi tải ảnh lên thất bại
                        Toast.makeText(AdminAddProduct.this, "Lỗi khi tải ảnh lên Firebase Storage", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Thêm thông tin sản phẩm vào Firestore
    private void addProductToFirestore(String name, String price, String description, String imageUrl) {
        // Tạo một Map để đại diện cho dữ liệu sản phẩm
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("price", price);
        productData.put("description", description);
        productData.put("imageUrl", imageUrl);

        // Thêm dữ liệu vào Firestore
        firestore.collection("products")
                .add(productData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Xử lý khi thêm sản phẩm thành công
                        Toast.makeText(AdminAddProduct.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng activity sau khi thêm thành công
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi thêm vào Firestore thất bại
                        Toast.makeText(AdminAddProduct.this, "Lỗi khi thêm sản phẩm vào Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
