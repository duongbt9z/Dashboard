package com.example.dashboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextView txtSignUp, txtForgetPass;
    EditText edtEmailLogin, edtPasswordLogin;
    Button btnLogin;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_user);
        initView();
        addEvent();
        auth = FirebaseAuth.getInstance();
    }

    private void initView() {
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForgetPass = findViewById(R.id.txtForgetPass);
        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = findViewById(R.id.edtPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void addEvent() {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });
        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin đầu vào từ người dùng
                String email = edtEmailLogin.getText().toString().trim();
                String password = edtPasswordLogin.getText().toString().trim();

                // Kiểm tra xem thông tin có hợp lệ không
                if (email.isEmpty() || password.isEmpty()) {
                    edtEmailLogin.setError("Không được để trống!");
                    edtPasswordLogin.setError("Không được để trống!");
                    return;
                }

                // Xác thực người dùng với Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, task -> {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công
                                FirebaseUser user = auth.getCurrentUser();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                // Thêm mã để chuyển đến màn hình tiếp theo hoặc thực hiện các hành động khác
                            } else {
                                // Nếu đăng nhập thất bại, hiển thị thông báo cho người dùng
                                Toast.makeText(Login.this, "Sai tài khoản hoặc mật khẩu!\n Vui lòng kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}