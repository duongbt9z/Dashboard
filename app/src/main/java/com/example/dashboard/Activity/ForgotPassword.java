package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dashboard.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    Button btnReset,back;
    EditText edtTextForgotPassword;
    ProgressBar forgetPasswordPB;
    FirebaseAuth mAuth;

    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        back = findViewById(R.id.back);
        btnReset = findViewById(R.id.btnReset);
        edtTextForgotPassword = findViewById(R.id.edtTextForgotPassword);
        forgetPasswordPB = findViewById(R.id.forgetPasswordPB);
        mAuth = FirebaseAuth.getInstance();
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtTextForgotPassword.getText().toString().trim();
                if(!TextUtils.isEmpty(email)){
                    ResetPassword();
                }else {
                    edtTextForgotPassword.setError("Không được để trống");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private  void ResetPassword(){
        forgetPasswordPB.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassword.this, "Đã gửi đường dẫn xác nhận đến Email bạn đăng ký", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassword.this,ConfirmPasswordActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassword.this, "Error:-"+e.getMessage(), Toast.LENGTH_SHORT).show();
                forgetPasswordPB.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
    }
}