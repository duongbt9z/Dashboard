package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dashboard.Adapter.RevenueAdapter;
import com.example.dashboard.Adapter.UserAdapter;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Domain.RevenueDomain;
import com.example.dashboard.Domain.UserDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class OrderSummaryActivity extends AppCompatActivity {
    ImageView backBtn;
    TextView totalQuanityTxt, totalRevenueTxt, totalTxt;
    private RecyclerView renevuerView;
    private RevenueDomain revenueDomain;
    private RevenueAdapter revenueAdapter;
    private ArrayList<RevenueDomain> listRevenue = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);


        totalQuanityTxt = findViewById(R.id.totalQuanityTxt);
        totalRevenueTxt = findViewById(R.id.totalRevenueTxt);
        totalTxt = findViewById(R.id.totalTxt);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Lấy dữ liệu từ Firestore và cập nhật giao diện
        initView();
        calculateTotal();
    }

    private void initView() {
        renevuerView = findViewById(R.id.renevuerView);
        renevuerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                RevenueDomain revenue = documentSnapshot.toObject(RevenueDomain.class);

                                listRevenue.add(revenue);
                            }

                            // Khởi tạo adapter sau khi thêm dữ liệu vào danh sách
                            revenueAdapter = new RevenueAdapter(listRevenue, OrderSummaryActivity.this);
                            renevuerView.setAdapter(revenueAdapter);

                            // Thông báo cho adapter sau khi thêm tất cả các phần tử vào danh sách
                            revenueAdapter.notifyDataSetChanged();
                        } else {
                            // Xử lý lỗi nếu task không thành công
                            Log.e("renevuerView", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    private void calculateTotal() {
        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double totalRevenue = 0;
                            int totalQuantity = 0;

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                RevenueDomain revenue = documentSnapshot.toObject(RevenueDomain.class);
                                int quantity = Integer.parseInt(revenue.getQuanity());
                                double price = Double.parseDouble(revenue.getTotalPrice());

                                totalQuantity += quantity;
                                totalRevenue += price;
                            }

                            double total = totalRevenue * totalQuantity;

                            DecimalFormat formatter = new DecimalFormat("#,###");
                            String formattedTotalQuantity = formatter.format(totalQuantity);
                            String formattedTotalRevenue = formatter.format(totalRevenue);
                            String formattedTotal = formatter.format(total);

                            totalQuanityTxt.setText(formattedTotalQuantity);
                            totalRevenueTxt.setText(formattedTotalRevenue + " VNĐ");
                            totalTxt.setText(formattedTotal + " VNĐ");

                            Log.d("Total", "Total: " + total);
                        } else {
                            // Xử lý lỗi nếu task không thành công
                            Log.e("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
