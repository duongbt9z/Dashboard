package com.example.dashboard.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.Adapter.EditProductAdapter;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminEditProduct extends AppCompatActivity {
    private RecyclerView.Adapter adapterEditProduct;
    private RecyclerView recyclerViewEditProduct;
    FirebaseFirestore fStore;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_product);

        fStore = FirebaseFirestore.getInstance();

        editRecyclerView();

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void editRecyclerView() {
        ArrayList<ProductDomain> products = new ArrayList<>();


        recyclerViewEditProduct = findViewById(R.id.productView);
        recyclerViewEditProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        fStore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                ProductDomain object = documentSnapshot.toObject(ProductDomain.class);
                                products.add(object);
                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                            adapterEditProduct = new EditProductAdapter(products);
                            recyclerViewEditProduct.setAdapter(adapterEditProduct);
                            // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                            adapterEditProduct.notifyDataSetChanged();

                        } else {
                            // Xử lý lỗi khi task không thành công
                            Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
