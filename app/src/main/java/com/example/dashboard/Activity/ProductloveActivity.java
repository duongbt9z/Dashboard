package com.example.dashboard.Activity;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductloveActivity extends AppCompatActivity {
    private com.example.dashboard.Adapter.ProductTypeAdapter ProductTypeAdapter;
    ScrollView ScrollView_productlove;
    LinearLayout view_productlove;
    RecyclerView recyclerview_productlove;
    FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    ImageView backBtn;
    TextView textView_productlove;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlove);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
//        textView_producttype= findViewById(R.id.textView_producttype);

        Intent intent = getIntent();
        Bundle data =intent.getExtras();
        Boolean love = data.getBoolean("love");
        ScrollView_productlove = findViewById(R.id.ScrollView_productlove);
        recyclerview_productlove = findViewById(R.id.recyclerview_productlove);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductloveActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        popRecyclerView_producttypelove();
    }
    private void popRecyclerView_producttypelove( ) {
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference df = fStore.collection("AddTolove").document(user.getUid());
        ArrayList<ProductTypeDomain> items_producttype = new ArrayList<>();
        recyclerview_productlove.setLayoutManager(new GridLayoutManager(this, 2));
        df.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Boolean check= (Boolean) documentSnapshot.getData().get("love");
                        if (check == true){
                            ProductTypeDomain ProductTypeDomain = documentSnapshot.toObject(ProductTypeDomain.class);
                          items_producttype.add(ProductTypeDomain);
                        }
                    }
                    // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                    ProductTypeAdapter = new ProductTypeAdapter(items_producttype);
                    recyclerview_productlove.setAdapter(ProductTypeAdapter);
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