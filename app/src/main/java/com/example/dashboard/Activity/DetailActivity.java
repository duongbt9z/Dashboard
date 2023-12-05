package com.example.dashboard.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Helper.ManagementCart;
import com.example.dashboard.R;

public class DetailActivity extends AppCompatActivity {
    private Button addToCartBtn, buyBtn;
    private TextView titleTxt, priceTxt, scoreTxt, reviewTxt, descriptionTxt;
    private ImageView itemPic, backBtn;
    private PopularDomain object;
    private int numberOrder = 1;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        managementCart = new ManagementCart(this);
        initView();
        getBundle();
    }

    private void getBundle() {
        object = (PopularDomain) getIntent().getSerializableExtra("object"); // lấy PopularDomain từ Intent thông qua object


        //Dùng thư viện glide để load ảnh
        Glide.with(this)
                .load(object.getPicURL())
                .into(itemPic); //hiển thị trên ImageView tương ứng
        titleTxt.setText(object.getTitle());
        priceTxt.setText(object.getPrice() + " VNĐ");
        scoreTxt.setText(object.getScore() + "");
        reviewTxt.setText(object.getReview() + "");
        descriptionTxt.setText(object.getDescription());

        //nút thêm vào giỏ hàng
        addToCartBtn.setOnClickListener(v -> {
            object.setNumberInCart(numberOrder); //set số lượng đơn hàng trong giỏi
            managementCart.insertItem(object);
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setNumberInCart(numberOrder); //set số lượng đơn hàng trong giỏi
                managementCart.insertItem(object);
                startActivity(new Intent(DetailActivity.this, CartActivity.class));
            }
        });

        //nút back
        backBtn.setOnClickListener(v -> finish());
    }

    private void initView() {
        addToCartBtn = findViewById(R.id.addToCartBtn);
        buyBtn = findViewById(R.id.buyBtn);

        titleTxt = findViewById(R.id.titleTxt);
        priceTxt = findViewById(R.id.priceTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        reviewTxt = findViewById(R.id.reviewTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);

        itemPic = findViewById(R.id.itemPic);
        backBtn = findViewById(R.id.backBtn);
    }
}