package com.example.dashboard.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ActivityUser extends AppCompatActivity {
    private ImageView imgAvatar, btnBack, saveBtn, showPass;
    private EditText edtName, edtPhone, edtEmail, edtPassWord;
    private TextView changePicTxt;
    private Button logOutBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    private Uri selectedImageUri;
    private Uri defaultAvatarUri;
    private StorageReference avatarsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        imgAvatar = findViewById(R.id.imgAvatar);
        showPass = findViewById(R.id.showPass);
        saveBtn = findViewById(R.id.saveBtn);
        changePicTxt = findViewById(R.id.changePicTxt);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPassWord = findViewById(R.id.edtPassWord);

        btnBack = findViewById(R.id.btnBack);
        logOutBtn = findViewById(R.id.logOutBtn);

        firestore = FirebaseFirestore.getInstance();

        // Load đường dẫn ảnh mặc định từ resources (drawable)
        defaultAvatarUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_user);

        // Tạo một StorageReference cho thư mục "avatars"
        avatarsRef = FirebaseStorage.getInstance().getReference().child("avatars");

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassWord.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    // Hiển thị mật khẩu
                    showPass.setImageResource(R.drawable.pass_visible); // Thay đổi hình ảnh nếu bạn muốn
                    edtPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // Ẩn mật khẩu
                    showPass.setImageResource(R.drawable.pass_invisible); // Thay đổi hình ảnh nếu bạn muốn
                    edtPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUser.this, MainActivity.class));
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityUser.this, Login.class);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
                startActivity(new Intent(ActivityUser.this, MainActivity.class));
            }
        });

        changePicTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgAvatar.setImageURI(selectedImageUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();
        documentReference = firestore.collection("Users").document(currentUid);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String nameUpd = snapshot.getString("name");
                    String phoneUpd = snapshot.getString("phoneNumber");
                    String passUpd = snapshot.getString("password");
                    String pic = snapshot.getString("avatar");
                    String emailUpd = snapshot.getString("email");


                    Glide.with(getApplicationContext())
                            .load(pic)
                            .circleCrop()
                            .into(imgAvatar);
                    edtName.setText(nameUpd);
                    edtEmail.setText(emailUpd);
                    edtPhone.setText(phoneUpd);
                    edtPassWord.setText(passUpd);
                } else {
                    Toast.makeText(ActivityUser.this, "No profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassWord.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();
        DocumentReference doc = firestore.collection("Users").document(currentUid);
        Map<String, Object> userData = new HashMap<>();

        // Kiểm tra xem có ảnh mới được chọn hay không
        if (selectedImageUri != null && !selectedImageUri.equals(defaultAvatarUri)) {
            uploadImageAndSetAvatar(selectedImageUri, userData, doc, name, email, pass, phone);
        } else {
            // Sử dụng ảnh mặc định và cập nhật các trường khác
            userData.put("avatar", defaultAvatarUri.toString());
            updateOtherUserFields(userData, doc, name, email, pass, phone);
        }

        // Cập nhật mật khẩu
        user.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("TAG", "Password updated");
                } else {
                    Log.e("TAG", "Error password not updated");
                }
            }
        });
    }

    private void uploadImageAndSetAvatar(Uri imageUri, final Map<String, Object> userData, final DocumentReference doc,
                                         final String name, final String email, final String pass, final String phone) {
        // Tạo tham chiếu đến Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference avatarRef = avatarsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        // Thực hiện tải lên avatar
        avatarRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Lấy đường dẫn của ảnh sau khi tải lên thành công
                        avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Update "avatar" field in Firestore with the download URL
                                userData.put("avatar", downloadUri.toString());
                                // Update other user fields
                                updateOtherUserFields(userData, doc, name, email, pass, phone);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi tải lên thất bại
                        Toast.makeText(ActivityUser.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateOtherUserFields(Map<String, Object> userData, DocumentReference doc,
                                       final String name, final String email, final String pass, final String phone) {
        userData.put("name", name);
        userData.put("email", email);
        userData.put("password", pass);
        userData.put("phoneNumber", phone);

        firestore.collection("Users").document(doc.getId()).update(userData);
        firestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(doc, "name", name);
                transaction.update(doc, "email", email);
                transaction.update(doc, "password", pass);
                transaction.update(doc, "phoneNumber", phone);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ActivityUser.this, "Cập nhật thông tin cá nhân thành công!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityUser.this, "Cập nhật thông tin cá nhân thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}