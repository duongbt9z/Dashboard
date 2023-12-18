package com.example.dashboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    EditText edtNameSignUp,edtEmailSignUp,edtPasswordSignUp,edtPasswordSignUpCF;
    Button btnSignUp;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
        initView();
        addEvent();
    }
    private  void  initView(){
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        edtNameSignUp = findViewById(R.id.edtName_SignUp);
        edtEmailSignUp = findViewById(R.id.edtEmail_SignUp);
        edtPasswordSignUp = findViewById(R.id.edtPassword_SignUp);
        edtPasswordSignUpCF = findViewById(R.id.edtPassword_SignUpCF);
        btnSignUp = findViewById(R.id.btnSignUp);
    }
    private  void addEvent(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtNameSignUp.getText().toString().trim();
                String email = edtEmailSignUp.getText().toString().trim();
                String passWord = edtPasswordSignUp.getText().toString().trim();
                String passWordCF = edtPasswordSignUpCF.getText().toString().trim();
                if(name.length() <= 0||email.length() <= 0||passWord.length() <= 0||passWordCF.length() <= 0){
                    Toast.makeText(Registration.this, "Vui Lòng Nhập Đầy Đủ Thông Tin!", Toast.LENGTH_SHORT).show();
                }else{
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        Toast.makeText(Registration.this, "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show();
                    }
                    if(passWord.length() < 6 &&passWordCF.length() < 6){
                        Toast.makeText(Registration.this, "Mật Khẩu có ít nhất 7 ký tự", Toast.LENGTH_SHORT).show();
                    }else if(passWord.equals(passWordCF)==false){
                        Toast.makeText(Registration.this, "Mật Khẩu không khớp", Toast.LENGTH_SHORT).show();
                    }else {
                        // kiểm tra xem email đã được dùng hay chưa
                        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if(task.isSuccessful()){
                                    SignInMethodQueryResult result = task.getResult();

                                    if(result.getSignInMethods().isEmpty()){
                                        // Email chưa được sử dụng , tiến hành đăng ký
                                        auth.createUserWithEmailAndPassword(email,passWord).addOnSuccessListener(Registration.this, new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                FirebaseUser user = auth.getCurrentUser();
                                                DocumentReference df = fStore.collection("Users").document(user.getUid());
                                                Map<String, Object> userInfo = new HashMap<>();
                                                userInfo.put("name", edtNameSignUp.getText().toString());
                                                userInfo.put("email", edtEmailSignUp.getText().toString());
                                                userInfo.put("password", edtPasswordSignUp.getText().toString());
                                                df.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(Registration.this, "Bố m được lưu rồi", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Registration.this, "Vẫn đ được", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Toast.makeText(Registration.this, "Đăng Ký Thành Công!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Registration.this,Login.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Registration.this, "Tạo Tài Khoản Thất Bại", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        Toast.makeText(Registration.this, "Email đã được sử dụng. Vui lòng chọn email khác.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}


