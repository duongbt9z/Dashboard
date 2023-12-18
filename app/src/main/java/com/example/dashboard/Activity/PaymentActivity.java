package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboard.Adapter.AddressAdapter;
import com.example.dashboard.Adapter.CartAdapter;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Helper.ManagementCart;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;


import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener, AddressAdapter.SelectedAddress{
    CartAdapter cartAdapter;
    ArrayList<CartDomain> listCart = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView totalFeeTxt, deliveryTxt, disDeliveryTxt, discountTxt, totalTxt, addressTxt;
    private ScrollView scrollView;
    private ImageView backBtn, chooseAddressBtn;
    private Button orderBtn;
    private RadioButton cashBtn, creditBtn;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    double total = 0;
    String mAddress = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        initView();
        setVariable();
        initList();

    }

    //Hàm tạo danh sách mặt hàng trong giỏ hàng và hiển thị
    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); //set RecyclerView vào Linear Layout
        listCart = new ArrayList<>(); //Khởi tạo ArrayList cho CartActivity
        cartAdapter = new CartAdapter(listCart,this); //tạo 1 adapter mới quản lý dữ liệu của arraylist
        recyclerView.setAdapter(cartAdapter); //set adapter cho recyclerView

        //Lấy dữ liệu trong collection của user hiện tại
        CollectionReference cartRef = fStore.collection("AddToCart").document(fAuth.getCurrentUser().getUid()).collection("User");
        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listCart.clear(); //Xóa dữ liệu trong ArrayList hiện tại
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Duyệt qua tất cả đối tượng trong Collection và chuyển thành 1 cartDomain
                        CartDomain cartDomain = document.toObject(CartDomain.class);
                        listCart.add(cartDomain); //Lưu dữ liệu vào Domain
                    }
                    cartAdapter.notifyDataSetChanged();
                    caculateCart();
                } else {
                    Log.d("TAG", "Lỗi: Không thể lấy dữ liệu: " + toString(), task.getException());
                }
            }
        });
    }

    //Hàm tình tiền
    private void caculateCart() {
        DecimalFormat formatter = new DecimalFormat("#,###");

        double percentDiscount = 0.1;
        double disDelivery = 0;
        double delivery = 20000;

        double discount = 0;

        for (CartDomain item : listCart) {
            discount += item.getTotalPrice();
            total += item.getTotalPrice();
            if (item.getTotalPrice() > 1000000){
                disDelivery = delivery;
            }
        }
        discount = Math.round(discount * percentDiscount * 100.0) / 100.0;
        total = Math.round(total * 100) / 100;

        double itemTotal = total;
        total = Math.round((total + delivery - discount - disDelivery) * 100) / 100;

        totalFeeTxt.setText(formatter.format(itemTotal) + " VNĐ");
        discountTxt.setText(formatter.format(discount) + " VNĐ");
        deliveryTxt.setText(formatter.format(delivery) + " VNĐ");
        disDeliveryTxt.setText(formatter.format(disDelivery) + " VNĐ");
        totalTxt.setText(formatter.format(total) + " VNĐ");
    }


    private void paymentMethod() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_nliw7flc034F8p");

        final Activity activity = PaymentActivity.this;

        try {
            JSONObject info = new JSONObject();
            //Set Company Name
            info.put("name", "Thanh Toán");
            //Ref no
            info.put("description", "Đơn Hàng #123456");
            //Image to be display
            info.put("image", R.drawable.logo);
            // Currency type
            info.put("currency", "USD");
            double amount = Double.parseDouble(totalTxt.getText().toString().replace("VNĐ", "").replace(",", ""));

            amount = amount / 23000;
            //amount
            info.put("amount", amount);
            JSONObject preFill = new JSONObject();
            //email
            preFill.put("email", "minhanh@gmail.com");
            //contact
            preFill.put("contact", "7489347378");

            info.put("prefill", preFill);

            checkout.open(activity, info);
        } catch (Exception e) {
            Log.d("TAG", "Error in starting Razorpay Checkout".toString());;
        }
    }
    private void setVariable() {
        backBtn.setOnClickListener(v -> {
            finish();
        });

        chooseAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, AddressActivity.class));
            }
        });

        creditBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    cashBtn.setChecked(false);
                }
            }
        });

        cashBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    creditBtn.setChecked(false);
                }
            }
        });
        
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!creditBtn.isChecked() && !cashBtn.isChecked()){
                    Toast.makeText(PaymentActivity.this, "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_SHORT).show();
                } else {
                    paymentMethod();
                }
            }
        });
    }
    private void initView() {
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        disDeliveryTxt = findViewById(R.id.disDeliveryTxt);
        discountTxt = findViewById(R.id.discountTxt);
        totalTxt = findViewById(R.id.totalTxt);
        addressTxt = findViewById(R.id.addressTxt);

        recyclerView = findViewById(R.id.paymentView);

        scrollView = findViewById(R.id.scrollView);

        backBtn = findViewById(R.id.backBtn);
        chooseAddressBtn = findViewById(R.id.chooseAddressBtn);
        orderBtn = findViewById(R.id.orderBtn);
        cashBtn = findViewById(R.id.cashBtn);
        creditBtn = findViewById(R.id.creditBtn);
    }


    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setAddress(String address) {
        mAddress = address;
        // Truy xuất địa chỉ được chọn từ Intent
        String selectedAddress = getIntent().getStringExtra("selectedAddress");
        // Đặt địa chỉ được chọn cho addressTxt
        addressTxt.setText(selectedAddress);
        addressTxt.requestLayout();
    }
}