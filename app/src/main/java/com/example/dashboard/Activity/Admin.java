package com.example.dashboard.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.dashboard.R;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LinearLayout AddProduct, User;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        AddProduct = findViewById(R.id.Add_Product);
        User = findViewById(R.id.User);

        if (AddProduct != null) {
            AddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Admin.this, AdminAddProduct.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

//        if (User != null) {
//            User.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Admin.this, ListUser.class);
//                    try {
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//        }
    }
}
