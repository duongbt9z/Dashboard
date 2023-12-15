package com.example.dashboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.R;

public class Login extends AppCompatActivity {
    TextView txtSignUp;
    EditText edtEmailLogin, edtPasswordLogin;
    Button btnLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_user);
        initView();
        addEvent();
    }
    private  void initView(){
        txtSignUp = findViewById(R.id.txtSignUp);
        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = findViewById(R.id.edtEmailLogin);
        btnLogin = findViewById(R.id.btnLogin);
    }
    private  void addEvent(){
        txtSignUp = findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Registration.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
