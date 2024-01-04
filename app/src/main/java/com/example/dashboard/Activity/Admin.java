package com.example.dashboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LinearLayout AddProduct, User,feedback,editProduct, deleteProduct, orderSummary;
        FloatingActionButton homeBtn;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        homeBtn = findViewById(R.id.homeBtn);
        AddProduct = findViewById(R.id.Add_Product);
        User = findViewById(R.id.User);
        feedback = findViewById(R.id.feedback);
        editProduct = findViewById(R.id.editProduct);
        deleteProduct = findViewById(R.id.deleteProduct);
        orderSummary = findViewById(R.id.orderSummary);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin.this, Intro.class));
            }
        });

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

        if (orderSummary != null) {
            orderSummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Admin.this, OrderSummaryActivity.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        if (User != null) {
            User.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Admin.this, ListUser.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        if (feedback != null) {
            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Admin.this, ManageFeedbackActivity.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        if (editProduct != null) {
            editProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Admin.this, AdminEditProduct.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        if (deleteProduct != null) {
            deleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Admin.this, DeleteProductActivity.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}
