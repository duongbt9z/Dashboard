package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.dashboard.Adapter.CartAdapter;
import com.example.dashboard.Adapter.DeleteProductAdapter;
import com.example.dashboard.Adapter.PopularAdapter;
import com.example.dashboard.Adapter.SearchAdapter;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Popular RecyclerView
    private RecyclerView.Adapter adapterPopular;
    private  RecyclerView recyclerViewPopular;
    private RecyclerView productListView;
    private SearchAdapter searchAdapter;
    private ArrayList<ProductDomain> listProductView;
    private SearchView edtSearch;
    private TextView tv_not, userTxt;
    private DatabaseReference databaseRef;
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearch = findViewById(R.id.edtSearch);
        tv_not = findViewById(R.id.tv_not);
        userTxt = findViewById(R.id.userTxt);
        productListView = findViewById(R.id.productListView);

        fStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        edtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProduct(newText);
                if (TextUtils.isEmpty(newText)) {
                    tv_not.setVisibility(View.GONE);
                    productListView.setVisibility(View.GONE);
                } else {
                    boolean hasSearchResults = searchAdapter.getItemCount() > 0;
                    tv_not.setVisibility(hasSearchResults ? View.GONE : View.VISIBLE);
                    productListView.setVisibility(hasSearchResults ? View.VISIBLE : View.GONE);
                }
                return true;
            }
        });

        initProductList();
        getData();
        motionPictures();
        popRecyclerView();
        bottomNavigation();
        onClick_evnt();
    }

    private void getData() {
        fStore.collection("Users").document(auth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot snapshot,
                                        @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("TAG", "Listen failed.", error);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            String name = snapshot.getString("name");

                            userTxt.setText(name);

                        } else {
                            Log.e("TAG", "onEvent: Cant get document", error);
                        }
                    }
                });
    }

    private void initProductList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        productListView.setLayoutManager(linearLayoutManager);

        listProductView = new ArrayList<>(); //Khởi tạo ArrayList cho CartActivity
        searchAdapter = new SearchAdapter(listProductView,this); //tạo 1 adapter mới quản lý dữ liệu của arraylist
        productListView.setAdapter(searchAdapter); //set adapter cho recyclerView

        fStore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("productListView", "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ProductDomain object = documentSnapshot.toObject(ProductDomain.class);
                            listProductView.add(object);
                        }

                        // Khởi tạo adapter sau khi thêm dữ liệu vào danh sách

                        // Thông báo cho adapter sau khi thêm tất cả các phần tử vào danh sách
                    }
                });

    }
    private void filterProduct(String searchText) {
        ArrayList<ProductDomain> filteredList = new ArrayList<>();

        if(!TextUtils.isEmpty(searchText)){
            String searchQuery = searchText.toLowerCase().trim();

            for (ProductDomain product : listProductView) {
                if (product.getName().toLowerCase().contains(searchQuery) ||
                        product.getCategory().toLowerCase().contains(searchQuery)) {
                    filteredList.add(product);
                }
            }
        }
        searchAdapter.setFilteredList(filteredList);
        searchAdapter.notifyDataSetChanged();
     }

    private  void onClick_evnt(){
        LinearLayout cat_phone, cat_laptop, cat_watch, cat_tv, cat_view_all,favBtn;
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
        favBtn = findViewById(R.id.favBtn);
        favBtn.setOnClickListener(v -> Event_intent_items_menu(true));
    }
    public void Event_intent_items_menu(Boolean love){
        Bundle data = new Bundle();
        data.putBoolean("love",love);
        Intent intentcheang = new Intent(MainActivity.this, ProductloveActivity.class);
        intentcheang.putExtras(data);
        startActivity(intentcheang);
    }

    public void Event_intent_items(String Title_value,String category){
        Bundle data = new Bundle();
        data.putString("Title_value",Title_value);
        data.putString("category",category);
        Intent intentcheang = new Intent(MainActivity.this, ProductTypeActivity.class);
        intentcheang.putExtras(data);
        startActivity(intentcheang);
    }

    private  void motionPictures(){
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

        LinearLayout cat_view_all = findViewById(R.id.cat_view_all);
        cat_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AllProductActivity.class));
            }
        });

        FloatingActionButton homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout userBtn = findViewById(R.id.userBtn);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        });
        cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });
        userBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ActivityUser.class));
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
                        if (task.isSuccessful()){
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