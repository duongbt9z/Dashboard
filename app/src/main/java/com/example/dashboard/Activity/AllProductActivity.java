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
import android.widget.Toast;

import com.example.dashboard.Adapter.PopularAdapter;
import com.example.dashboard.Adapter.ProductTypeAdapter;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Domain.ProductTypeDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllProductActivity extends AppCompatActivity {
    private ProductTypeAdapter ProductTypeAdapter;
    ScrollView ScrollView_allproduct;
    LinearLayout view_phone;
    RecyclerView recyclerview_allproduct_laptop, recyclerview_allproduct_phone, recyclerview_allproduct_watch, recyclerview_allproduct_tivi;
    FirebaseFirestore fStore;
    ImageView backBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        fStore = FirebaseFirestore.getInstance();
        ScrollView_allproduct = findViewById(R.id.ScrollView_allproduct);
        view_phone = findViewById(R.id.view_phone);
        recyclerview_allproduct_laptop = findViewById(R.id.recyclerview_allproduct_laptop);
        recyclerview_allproduct_phone = findViewById(R.id.recyclerview_allproduct_phone);
        recyclerview_allproduct_watch = findViewById(R.id.recyclerview_allproduct_watch);
        recyclerview_allproduct_tivi = findViewById(R.id.recyclerview_allproduct_tivi);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllProductActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
        ArrayList<ProductTypeDomain> items_laptop = new ArrayList<>();
        ArrayList<ProductTypeDomain> items_phone = new ArrayList<>();
        ArrayList<ProductTypeDomain> items_smartwatch = new ArrayList<>();
        ArrayList<ProductTypeDomain> items_tivi = new ArrayList<>();
        popRecyclerView_AllProduct(recyclerview_allproduct_laptop,items_laptop,"LapTop");
        popRecyclerView_AllProduct(recyclerview_allproduct_phone,items_phone,"Điện Thoại");
        popRecyclerView_AllProduct(recyclerview_allproduct_watch,items_smartwatch,"Smart Watch");
        popRecyclerView_AllProduct(recyclerview_allproduct_tivi,items_tivi,"Smart TV");

    }

    private void popRecyclerView_AllProduct( RecyclerView RecyclerView,  ArrayList<ProductTypeDomain> items ,String v ) {
        RecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fStore.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ( documentSnapshot.getData().get("category").equals(v)){
                            ProductTypeDomain ProductTypeDomain = documentSnapshot.toObject(ProductTypeDomain.class);
                            items.add(ProductTypeDomain);
                        }

                    }
                    // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                    ProductTypeAdapter = new ProductTypeAdapter(items);
                    RecyclerView.setAdapter(ProductTypeAdapter);
                    // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                    ProductTypeAdapter.notifyDataSetChanged();

                } else {
                    // Xử lý lỗi khi task không thành công
                    Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}