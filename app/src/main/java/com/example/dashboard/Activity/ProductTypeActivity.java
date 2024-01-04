package com.example.dashboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.Adapter.ProductTypeAdapter;
import com.example.dashboard.Domain.ProductTypeDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductTypeActivity extends AppCompatActivity {
    private ProductTypeAdapter ProductTypeAdapter;
    ScrollView ScrollView_producttype;
    LinearLayout view_producttype;
    RecyclerView recyclerview_producttype;
    FirebaseFirestore fStore;
    ImageView backBtn;
    TextView textView_producttype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_type);
        fStore = FirebaseFirestore.getInstance();
        textView_producttype= findViewById(R.id.textView_producttype);

        Intent intent = getIntent();
        Bundle data =intent.getExtras();
        String title = data.getString("Title_value");
        String category = data.getString("category");

        textView_producttype.setText(title);

        view_producttype = findViewById(R.id.view_producttype);
        ScrollView_producttype = findViewById(R.id.ScrollView_producttype);
        recyclerview_producttype = findViewById(R.id.recyclerview_producttype);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductTypeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        popRecyclerView_producttype(category);
    }
    private void popRecyclerView_producttype(String v) {
        ArrayList<ProductTypeDomain> items_producttype = new ArrayList<>();
        recyclerview_producttype.setLayoutManager(new GridLayoutManager(this, 2)); // Số cột là 2
        fStore.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ( documentSnapshot.getData().get("category").equals(v)){
                            ProductTypeDomain ProductTypeDomain = documentSnapshot.toObject(ProductTypeDomain.class);
                            items_producttype.add(ProductTypeDomain);
                        }
                    }
                    // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                    ProductTypeAdapter = new ProductTypeAdapter(items_producttype);
                    recyclerview_producttype.setAdapter(ProductTypeAdapter);
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