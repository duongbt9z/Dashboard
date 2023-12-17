package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Domain.ProductTypeDomain;
import com.example.dashboard.Helper.ManagementCart;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class TypedetailActivity extends AppCompatActivity {
    private Button addToCartBtn, buyBtn;
    private TextView titleTxt, priceTxt, scoreTxt, reviewTxt, descriptionTxt, txtQuanity, plusBtn, minusBtn;
    private ImageView itemPic, backBtn, wishListBtn;
    private ProductTypeDomain ProductTypeAdapter;
    private CartDomain objectCart;
    private int numberOrder = 0;
    private double totalPrice = 0;
    private ManagementCart managementCart;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typedetail);

        managementCart = new ManagementCart(this);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        initView();
        getPopularObject();
    }

    private void getPopularObject() {
        ProductTypeAdapter = (ProductTypeDomain) getIntent().getSerializableExtra("items_type"); // lấy PopularDomain từ Intent thông qua object
        //Dùng thư viện glide để load ảnh
        String pic = ProductTypeAdapter.getPicURL();
        Glide.with(this)
                .load(pic)
                .into(itemPic); //hiển thị trên ImageView tương ứng
        titleTxt.setText(ProductTypeAdapter.getTitle());
        priceTxt.setText(ProductTypeAdapter.getPrice() + " VNĐ");
        scoreTxt.setText(ProductTypeAdapter.getScore() + "");
        reviewTxt.setText(ProductTypeAdapter.getReview() + "");
        descriptionTxt.setText(ProductTypeAdapter.getDescription());

        double price = Double.parseDouble(ProductTypeAdapter.getPrice());
        totalPrice = price * numberOrder;

        //nút thêm vào giỏ hàng
        addToCartBtn.setOnClickListener(v -> {
            //set số lượng đơn hàng trong giỏi
//            addToCart();
            Toast.makeText(this, "click add vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TypedetailActivity.this, "click mua ngay", Toast.LENGTH_SHORT).show();
//                ProductTypeAdapter.setNumberInCart(numberOrder); //set số lượng đơn hàng trong giỏi
//                startActivity(new Intent(TypedetailActivity.this, PaymentActivity.class));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOrder > 0){
                    numberOrder--;
                    txtQuanity.setText(String.valueOf(numberOrder));
                    if(ProductTypeAdapter != null){
                        double price = Double.parseDouble(ProductTypeAdapter.getPrice());
                        totalPrice = price * numberOrder;
                    }
                }
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOrder < 100){
                    numberOrder++;
                    txtQuanity.setText(String.valueOf(numberOrder));
                    if(ProductTypeAdapter != null){
                        double price = Double.parseDouble(ProductTypeAdapter.getPrice());
                        totalPrice = price * numberOrder;
                    }
                }
            }
        });

        //nút back
        backBtn.setOnClickListener(v -> finish());

        //nút wishlist
        wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishListBtn.setImageResource(R.drawable.wishlist2);
            }
        });
    }

    private void addToCart() {
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference df = fStore.collection("AddToCart").document(user.getUid());
        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("productName", titleTxt.getText().toString());
        cartMap.put("productPrice", priceTxt.getText().toString());
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("totalQuanity", txtQuanity.getText().toString());
        cartMap.put("totalPrice", totalPrice);

        df.collection("User").whereEqualTo("productName", titleTxt.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Kiểm tra sản phẩm đã tổn tại, tăng số lượng trong giỏ hàng.
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    CartDomain cartDomain = document.toObject(CartDomain.class);

                                    int quanity = Integer.parseInt(cartDomain.getTotalQuanity()); //Lấy số lượng sản phẩm hiện tại
                                    int newQuanity = quanity + Integer.parseInt(txtQuanity.getText().toString()); //Tăng số lượng sản phẩm sau khi thêm
                                    cartDomain.setTotalQuanity(String.valueOf(newQuanity)); //Cập nhật vào CartDomain
                                    int price = Integer.parseInt(cartDomain.getProductPrice().replace(" VNĐ", "")); //Lấy giá tiền
                                    totalPrice = price * newQuanity;
                                    cartDomain.setTotalPrice(totalPrice); // Cập nhật totalPrice trong CartDomain

                                    //Tạo giỏ hàng rieeng cho user hiện tại
                                    df.collection("User").document(document.getId()).set(cartDomain);
                                    Toast.makeText(TypedetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                // Nếu chưa tồn tại, thêm vào FireStore.
                                df.collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Toast.makeText(TypedetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    private void initView() {
        addToCartBtn = findViewById(R.id.addToCartBtn);
        buyBtn = findViewById(R.id.buyBtn);
        wishListBtn = findViewById(R.id.wishListBtn);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        backBtn = findViewById(R.id.backBtn);

        titleTxt = findViewById(R.id.titleTxt);
        priceTxt = findViewById(R.id.priceTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        reviewTxt = findViewById(R.id.reviewTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        txtQuanity = findViewById(R.id.txtQuanity);

        itemPic = findViewById(R.id.itemPic);

    }
}