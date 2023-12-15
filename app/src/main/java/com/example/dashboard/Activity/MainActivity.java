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
import android.widget.TextView;

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
    private RecyclerView recyclerViewPopular;
    FirebaseFirestore fStore;


    //add
    TextView textView13,textView6;

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
        cat_phone = findViewById(R.id.cat_phone);
        cat_laptop = findViewById(R.id.cat_laptop);
        cat_watch = findViewById(R.id.cat_watch);
        cat_tv = findViewById(R.id.cat_tv);
        cat_view_all = findViewById(R.id.cat_view_all);

        cat_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
    }

}