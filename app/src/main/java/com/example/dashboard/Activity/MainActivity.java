package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboard.Adapter.PopularAdapter;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Popular RecyclerView
    private RecyclerView.Adapter adapterPopular;
    //add list view ms
    private RecyclerView recyclerViewPopular,recyclerview_Laptop,recyclerview_phone,recyclerview_watch, recyclerview_tv ;
    FirebaseFirestore fStore;


    //add update menu
    TextView textView13,textView6;
    LinearLayout view_Laptop, view_phone,view_watch,view_tv;
    boolean btn=true;
    boolean check_title_menu = true;

    LinearLayout cat_phone,cat_laptop,cat_watch,cat_tv,cat_view_all,title_menu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fStore = FirebaseFirestore.getInstance();

        popRecyclerView();
        bottomNavigation();


        /// add duongbt

        view_Laptop = findViewById(R.id.view_Laptop);
        view_phone = findViewById(R.id.view_phone);
        view_watch = findViewById(R.id.view_watch);
        view_tv = findViewById(R.id.view_tv);
        textView13 = findViewById(R.id.textView13);
        textView6 = findViewById(R.id.textView6);
        recyclerViewPopular = findViewById(R.id.view1);
        title_menu = findViewById(R.id.title_menu);
        textView6.setOnClickListener(view -> {
            if(check_title_menu == true){
                title_menu.setVisibility(View.GONE);
                check_title_menu = false;
            }else {
                title_menu.setVisibility(View.VISIBLE);
                check_title_menu = true;
            }
            Log.d("hehe", "onCreate: duongfix");
        });
        textView13.setOnClickListener(view -> hide_appear());
        cat_laptop = findViewById(R.id.cat_laptop);
        cat_laptop.setOnClickListener((View v) -> smoothScrollTo(view_Laptop));
        cat_phone = findViewById(R.id.cat_phone);
        cat_phone.setOnClickListener(view -> smoothScrollTo(view_phone));
        cat_watch = findViewById(R.id.cat_watch);
        cat_watch.setOnClickListener(v -> smoothScrollTo(view_watch));
        cat_tv = findViewById(R.id.cat_tv);
        cat_tv.setOnClickListener(v -> smoothScrollTo(view_tv));




        cat_view_all = findViewById(R.id.cat_view_all);
    }
    // ẩn hiện danh mục menu
    public void hide_appear(){
        if(btn == true){
            recyclerViewPopular.setVisibility(View.GONE);
            btn = false;
        }else {
            recyclerViewPopular.setVisibility(View.VISIBLE);
            btn = true;
        }
    }
    //hàm tele đến vị trí của id scrollView2
    public void smoothScrollTo(LinearLayout view){
        // Cuộn đến vị trí đó
        ScrollView scrollView = findViewById(R.id.scrollView2);
        if (view != null) {
            Log.e("TAG", "flag1 is yepp");
            scrollView.smoothScrollTo(0, view.getTop());
        } else {
            Log.e("TAG", "flag1 is null");
        }

    }



    //Thanh điều hướng
    private void bottomNavigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        });
        cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });
    }

    //Sản phẩm nổi bật
    private void popRecyclerView() {
        ArrayList<PopularDomain> items = new ArrayList<>();


        recyclerViewPopular = findViewById(R.id.view1);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fStore.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
                                items.add(popularDomain);
                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                            adapterPopular = new PopularAdapter(items);
                            recyclerViewPopular.setAdapter(adapterPopular);
                            // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                            adapterPopular.notifyDataSetChanged();

                        } else {
                            // Xử lý lỗi khi task không thành công
                            Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                        }
                    }
                });



        // add data iphone

        recyclerview_Laptop = findViewById(R.id.recyclerview_Laptop);
        recyclerview_Laptop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fStore.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
//                                items.add(popularDomain);
//                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
//                            adapterPopular = new PopularAdapter(items);
                            recyclerview_Laptop.setAdapter(adapterPopular);
                            // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                            adapterPopular.notifyDataSetChanged();

                        } else {
                            // Xử lý lỗi khi task không thành công
                            Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //add data phone
        recyclerview_phone = findViewById(R.id.recyclerview_phone);
        recyclerview_phone.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fStore.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
//                                items.add(popularDomain);
//                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
//                            adapterPopular = new PopularAdapter(items);
                            recyclerview_phone.setAdapter(adapterPopular);
                            // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                            adapterPopular.notifyDataSetChanged();

                        } else {
                            // Xử lý lỗi khi task không thành công
                            Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                        }
                    }
                });
        //add data watch
        recyclerview_watch = findViewById(R.id.recyclerview_watch);
        recyclerview_watch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fStore.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
//                                items.add(popularDomain);
//                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
//                            adapterPopular = new PopularAdapter(items);
                            recyclerview_watch.setAdapter(adapterPopular);
                            // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                            adapterPopular.notifyDataSetChanged();

                        } else {
                            // Xử lý lỗi khi task không thành công
                            Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //add data tv
        recyclerview_tv = findViewById(R.id.recyclerview_tv);
        recyclerview_tv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fStore.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                PopularDomain popularDomain = documentSnapshot.toObject(PopularDomain.class);
//                                items.add(popularDomain);
//                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
//                            adapterPopular = new PopularAdapter(items);
                            recyclerview_tv.setAdapter(adapterPopular);
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