package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.dashboard.Adapter.PopularAdapter;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LaptopActivity extends AppCompatActivity {
    //Popular RecyclerView
    private RecyclerView.Adapter adapterPopular;
    ScrollView ScrollView_laptop;
    LinearLayout view_Laptop;
    RecyclerView recyclerview_laptop;
    FirebaseFirestore fStore;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laptop);
        fStore = FirebaseFirestore.getInstance();
        ScrollView_laptop = findViewById(R.id.ScrollView_laptop);
        view_Laptop = findViewById(R.id.view_Laptop);
        recyclerview_laptop = findViewById(R.id.recyclerview_Laptop);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaptopActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        popRecyclerView_Laptop();
    }

    //add data phone
    private void popRecyclerView_Laptop() {
        ArrayList<PopularDomain> items_laptop = new ArrayList<>();
        recyclerview_laptop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fStore.collection("PopularProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
                        items_laptop.add(popularDomain);
                    }
                    // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                    adapterPopular = new PopularAdapter(items_laptop);
                    recyclerview_laptop.setAdapter(adapterPopular);
                    // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                    adapterPopular.notifyDataSetChanged();

                } else {
                    // Xử lý lỗi khi task không thành công
                    Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}