package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.example.dashboard.Adapter.AddressAdapter;
import com.example.dashboard.Adapter.CartAdapter;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;


import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener{
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
    DocumentReference documentReference;
    double total = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        initView();
        setVariable();
        initList();

        // Lấy địa chỉ đã chọn từ dữ liệu extra của Intent
        String selectedAddress = getIntent().getStringExtra("selectedAddress");
        // Sử dụng địa chỉ đã chọn để gọi hàm setAddress()
        setAddress(selectedAddress);
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
        if (cashBtn.isChecked()){
            Toast.makeText(PaymentActivity.this, "Đặt hàng thành công! \nĐơn hàng sẽ được vận chuyển trong thời gian sớm nhất."
                    , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PaymentActivity.this, MainActivity.class));
        } else {
            Checkout checkout = new Checkout();
            Checkout.preload(getApplicationContext());
            checkout.setKeyID("rzp_test_ZGzwvqtiebIM5D");

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

    }

    private void saveOrderToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        String user = currentUser.getUid();
        ListenerRegistration registration = db.collection("Users").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String username = snapshot.getString("name");
                    for (CartDomain cartItem : listCart) {
                        String productID = cartItem.getProductID();
                        String pic = cartItem.getPic();
                        String productName = cartItem.getProductName();
                        String quanity = cartItem.getTotalQuanity();
                        double totalPriceValue = cartItem.getTotalPrice();
                        String totalPrice = String.valueOf(totalPriceValue); // Giá tiền

                        // Tạo một đối tượng Map để lưu thông tin đơn hàng
                        Map<String, Object> orderData = new HashMap<>();
                        orderData.put("username", username);
                        orderData.put("pic", pic);
                        orderData.put("productID", productID);
                        orderData.put("productName", productName);
                        orderData.put("quanity", quanity);
                        orderData.put("totalPrice", totalPrice);
                        orderData.put("orderDate", getCurrentDate());
                        orderData.put("orderTime", getCurrentTime());
                        orderData.put("address", addressTxt.getText().toString());

                        // Lưu đơn hàng vào Firestore
                        db.collection("orders")
                                .add(orderData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("TAG", "Đã thêm đơn hàng vào collection 'orders'");
                                })
                                .addOnFailureListener((exception) -> {
                                    Log.w("TAG", "Lỗi khi thêm đơn hàng vào collection 'orders'", e);
                                });
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "No profile", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void removeFromCartAndFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        // Lắng nghe sự thay đổi trong collection "User" của Firestore
        db.collection("AddToCart").document(currentUser.getUid())
                .collection("User")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    // Tạo một danh sách để lưu trữ các ID của các mục cần xóa
                    List<String> itemsToRemoveIds = new ArrayList<>();

                    // Xử lý dữ liệu từ snapshot
                    if (value != null) {
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String productId = document.getId();
                            itemsToRemoveIds.add(productId);
                        }
                    }

                    // Xóa các mục từ Firestore
                    for (String productId : itemsToRemoveIds) {
                        db.collection("AddToCart").document(currentUser.getUid())
                                .collection("User").document(productId)
                                .delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "Mục đã được xóa khỏi Firestore");
                                    } else {
                                        Log.w("TAG", "Lỗi khi xóa mục từ Firestore", task.getException());
                                    }
                                });
                    }

                    // Xóa sạch listCart cục bộ sau khi các thao tác Firestore được hoàn tất
                    listCart.clear();
                    cartAdapter.notifyDataSetChanged();
                    caculateCart(); // Cập nhật hiển thị giỏ hàng
                });
    }

    private String getCurrentDate() {
        // Bạn có thể sử dụng thư viện SimpleDateFormat để định dạng ngày giờ theo ý muốn
        String saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        return saveCurrentDate; // Thay bằng định dạng ngày thực tế bạn muốn
    }

    private String getCurrentTime() {
        String saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());
        return saveCurrentTime; // Thay bằng định dạng giờ thực tế bạn muốn
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
                    // Lưu dữ liệu vào Firestore
                    saveOrderToFirestore();
                    removeFromCartAndFirestore();
                }
            }
        });
    }
    //hàm khỏi tạo các view
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

    public void setAddress(String address){
        addressTxt.setText(address);
    }
}