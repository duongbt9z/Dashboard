package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboard.Adapter.CartAdapter;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Adapter.EditProductAdapter;
import com.example.dashboard.Helper.ManagementCart;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CartActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private CartAdapter cartAdapter;

    private RecyclerView recyclerView;
    ArrayList<PopularDomain> listItem;
    ArrayList<CartDomain> listCart = new ArrayList<>();
    private ManagementCart managementCart;
    private TextView totalFeeTxt, deliveryTxt, totalTxt;
    private ScrollView scrollView;
    private ImageView backBtn;
    private Button checkOutBtn;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        initView();
        setVariable();
        initList();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {

                if (position < listCart.size()) {
                    CartDomain deletedItem = listCart.get(position);
                    String productID = listCart.get(position).getProductID();

                    // Xóa khỏi giỏ hàng
                    listCart.remove(position);
                    cartAdapter.notifyItemRemoved(position); //Thông báo xoá item
                    caculateCart(); //Cập nhật lại giá tiền trong giỏ

                    // Xóa sản phẩm khỏi Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser currentUser = fAuth.getCurrentUser();
                    db.collection("AddToCart").document(currentUser.getUid())
                            .collection("User").document(productID)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                    }else {
                                        Snackbar.make(recyclerView, "Lỗi! Không thể xóa " + deletedItem.getProductName() + " khỏi giỏ hàng", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                    Snackbar.make(recyclerView, "Đã xóa " + deletedItem.getProductName() + " khỏi giỏ hàng", Snackbar.LENGTH_LONG).show();
                }
            }
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView1, @NonNull RecyclerView.ViewHolder viewHolder1
                , float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(CartActivity.this, c, recyclerView1, viewHolder1, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(CartActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView1, viewHolder1, dX, dY, actionState, isCurrentlyActive);
        }
    };

    //Hàm tạo danh sách mặt hàng trong giỏ hàng và hiển thị
    private void initList() {
        // Tạo linear layout hướng dọc
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); //set RecyclerView vào Linear Layout
        listCart = new ArrayList<>(); //Khởi tạo ArrayList cho CartActivity
        cartAdapter = new CartAdapter(listCart,this); //tạo 1 adapter mới quản lý dữ liệu của arraylist
        recyclerView.setAdapter(cartAdapter); //set adapter cho recyclerView

        //Lấy dữ liệu trong collection của user hiện tại
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            CollectionReference cartRef = fStore.collection("AddToCart").document(currentUser.getUid()).collection("User");
            cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        listCart.clear(); //Xóa dữ liệu trong ArrayList hiện tại
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Duyệt qua tất cả đối tượng trong Collection và chuyển thành 1 cartDomain
                            String documentID = document.getId();

                            CartDomain cartDomain = document.toObject(CartDomain.class);

                            cartDomain.setProductID(documentID);
                            listCart.add(cartDomain); //Lưu dữ liệu vào Domain
                        }
                        cartAdapter.notifyDataSetChanged();
                        caculateCart();
                    } else {
                        Log.d("TAG", "Lỗi: Không thể lấy dữ liệu: " + toString(), task.getException());
                        // Thông báo cho người dùng về lỗi
                        Toast.makeText(CartActivity.this, "Lỗi: Không thể tải dữ liệu giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Thông báo cho người dùng rằng họ chưa đăng nhập
            Toast.makeText(CartActivity.this, "Lỗi: Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }


    //Hàm tình tiền
    private void caculateCart() {
        DecimalFormat formatter = new DecimalFormat("#,###");

        double delivery = 20000;

        double total = 0;
        for (CartDomain item : listCart) {
            total += item.getTotalPrice();
        }
        total = Math.round(total * 100) / 100;

        double itemTotal = total;
        total = Math.round((total + delivery) * 100) / 100;

        totalFeeTxt.setText(formatter.format(itemTotal) + " VNĐ");
        deliveryTxt.setText(formatter.format(delivery) + " VNĐ");
        totalTxt.setText(formatter.format(total) + " VNĐ");
    }


    private void setVariable() {
        backBtn.setOnClickListener(v -> {
            finish();
        });

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, PaymentActivity.class));
            }
        });
    }



    private void initView() {
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);

        checkOutBtn = findViewById(R.id.checkOutBtn);

        recyclerView = findViewById(R.id.cartView);

        scrollView = findViewById(R.id.scrollView);

        backBtn = findViewById(R.id.backBtn);
    }

    public static class AdminEditProduct extends AppCompatActivity {
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
}