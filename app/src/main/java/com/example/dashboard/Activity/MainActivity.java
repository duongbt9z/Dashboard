package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.dashboard.Adapter.PopularAdapter;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Popular RecyclerView
    private RecyclerView.Adapter adapterPopular;
    private RecyclerView recyclerViewPopular;
    FirebaseFirestore fStore;
    ViewFlipper viewFlipper;


    //add and update menu
    TextView textView13, textView6;
    boolean btn = true;
    boolean check_title_menu = true;

    LinearLayout cat_phone, cat_laptop, cat_watch, cat_tv, cat_view_all, title_menu;
    ProductTypeActivity recyclerview_producttype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fStore = FirebaseFirestore.getInstance();

        motionPictures();
        popRecyclerView();
        bottomNavigation();


        textView13 = findViewById(R.id.textView13);
        textView6 = findViewById(R.id.textView6);
        recyclerViewPopular = findViewById(R.id.view1);
        title_menu = findViewById(R.id.title_menu);
        textView6.setOnClickListener(view -> {
            if (check_title_menu == true) {
                title_menu.setVisibility(View.GONE);
                check_title_menu = false;
            } else {
                title_menu.setVisibility(View.VISIBLE);
                check_title_menu = true;
            }
            Log.d("hehe", "onCreate: duongfix");
        });
        textView13.setOnClickListener(view -> hide_appear());
        cat_laptop = findViewById(R.id.cat_laptop);
        cat_laptop.setOnClickListener((View v) -> {
            Event_intent_items("LapTop","LapTop");
        });
        cat_phone = findViewById(R.id.cat_phone);
        cat_phone.setOnClickListener(view -> {
            Event_intent_items("Điện Thoại","Điện Thoại");
        });
        cat_watch = findViewById(R.id.cat_watch);
        cat_watch.setOnClickListener(v -> {
            Event_intent_items("Smart Watch","Smart Watch");
        });
        cat_tv = findViewById(R.id.cat_tv);
        cat_tv.setOnClickListener(v -> {
            Event_intent_items("Smart TV","Smart TV");
        });
        cat_view_all = findViewById(R.id.cat_view_all);
        cat_view_all.setOnClickListener(view -> {
           Intent intent = new Intent(MainActivity.this,AllProductActivity.class);
           startActivity(intent);
        });
    }

    public void Event_intent_items(String Title_value,String category){
        Bundle data = new Bundle();
        data.putString("Title_value",Title_value);
        data.putString("category",category);
        Intent intent = new Intent(MainActivity.this, ProductTypeActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    // ẩn hiện danh mục menu
    public void hide_appear() {
        if (btn == true) {
            recyclerViewPopular.setVisibility(View.GONE);
            btn = false;
        } else {
            recyclerViewPopular.setVisibility(View.VISIBLE);
            btn = true;
        }
    }

    //hàm tele đến vị trí của id scrollView2
    public void smoothScrollTo(LinearLayout view) {
        // Cuộn đến vị trí đó
        ScrollView scrollView = findViewById(R.id.scrollView2);
        if (view != null) {
            Log.e("TAG", "flag1 is yepp");
            scrollView.smoothScrollTo(0, view.getTop());
        } else {
            Log.e("TAG", "flag1 is null");
        }

    }

    private void motionPictures() {
        viewFlipper = findViewById(R.id.viewFlipper);

        // Thiết lập chuyển đổi tự động giữa các view
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(2000); // Thời gian chờ giữa các chuyển đổi (1,5 giây)

        // Khai báo các sự kiện chuyển đổi
        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        // Tạo một ObjectAnimator để thay đổi thuộc tính "alpha" của viewFlipper
        ObjectAnimator animator = ObjectAnimator.ofFloat(viewFlipper, "alpha", 0f, 1f);
        animator.setDuration(4000); // Thời gian chuyển đổi là 4 giây

        // Bắt đầu chuyển đổi
        animator.start();
    }


    //Thanh điều hướng
    private void bottomNavigation() {
        FloatingActionButton homeBtn = findViewById(R.id.homeBtn);
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