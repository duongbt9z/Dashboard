package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

public class SmartWatchActicvity extends AppCompatActivity {
    private RecyclerView.Adapter adapterPopular;
    ScrollView ScrollView_watch;
    LinearLayout view_watch;
    RecyclerView recyclerview_watch;
    FirebaseFirestore fStore;
    ImageView backBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_watch_acticvity);
        fStore = FirebaseFirestore.getInstance();
        ScrollView_watch = findViewById(R.id.ScrollView_watch);
        view_watch = findViewById(R.id.view_watch);
        recyclerview_watch = findViewById(R.id.recyclerview_watch);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmartWatchActicvity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        popRecyclerView_Laptop();
    }
    private void popRecyclerView_Laptop() {
        ArrayList<PopularDomain> items_phone = new ArrayList<>();
        recyclerview_watch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fStore.collection("PopularProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
                        items_phone.add(popularDomain);
                    }
                    // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                    adapterPopular = new PopularAdapter(items_phone);
                    recyclerview_watch.setAdapter(adapterPopular);
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