package com.example.dashboard.Activity;

import com.example.dashboard.Adapter.FeedbackAdapter;
import com.example.dashboard.Domain.FeedbackDomain;
import com.example.dashboard.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Domain.PopularDomain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private Button addToCartBtn, buyBtn;
    private TextView titleTxt, priceTxt, scoreTxt, reviewTxt, descriptionTxt, txtQuanity, plusBtn, minusBtn, txtID;
    private ImageView itemPic, backBtn,imgCMT;
    private PopularDomain objectPopular;

    private ArrayList<FeedbackDomain> listFeedback = new ArrayList<>();
    private int numberOrder = 0;
    private double totalPrice = 0;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    String picURL;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        initView();
        getPopularObject();
    }

    private void getPopularObject() {
        objectPopular = (PopularDomain) getIntent().getSerializableExtra("object"); // lấy PopularDomain từ Intent thông qua object
        //Dùng thư viện glide để load ảnh
        String pic = objectPopular.getPicURL();
        picURL = objectPopular.getPicURL();
        Glide.with(this)
                .load(pic)
                .into(itemPic); //hiển thị trên ImageView tương ứng
        txtID.setText(objectPopular.getId() + "");
        titleTxt.setText(objectPopular.getTitle());
        priceTxt.setText(objectPopular.getPrice() + " VNĐ");
        scoreTxt.setText(objectPopular.getScore() + "");
        reviewTxt.setText(objectPopular.getReview() + "");
        descriptionTxt.setText(objectPopular.getDescription());

        double price = Double.parseDouble(objectPopular.getPrice());
        totalPrice = price * numberOrder;

        //nút thêm vào giỏ hàng
        addToCartBtn.setOnClickListener(v -> {
            //set số lượng đơn hàng trong giỏi
            addToCart();
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double quanity = Double.parseDouble(txtQuanity.getText().toString());

                if(quanity == 0){
                    Toast.makeText(DetailActivity.this, "Vui lòng nhập số lượng sản phẩm!", Toast.LENGTH_SHORT).show();
                } else {
                    addToCart();
                    startActivity(new Intent(DetailActivity.this, PaymentActivity.class));
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOrder > 0){
                    numberOrder--;
                    txtQuanity.setText(String.valueOf(numberOrder));
                    if(objectPopular != null){
                        double price = Double.parseDouble(objectPopular.getPrice());
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
                    if(objectPopular != null){
                        double price = Double.parseDouble(objectPopular.getPrice());
                        totalPrice = price * numberOrder;
                    }
                }
            }
        });
        imgCMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComment();
            }
        });
        //nút back
        backBtn.setOnClickListener(v -> finish());
    }

    private void addToCart() {
        double quanity = Double.parseDouble(txtQuanity.getText().toString());

        if(quanity == 0){
            Toast.makeText(this, "Vui lòng nhập số lượng sản phẩm!", Toast.LENGTH_SHORT).show();

        } else {
            String saveCurrentDate, saveCurrentTime;

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calForDate.getTime());

            FirebaseUser user = fAuth.getCurrentUser();
            DocumentReference df = fStore.collection("AddToCart").document(user.getUid());
            final HashMap<String, Object> cartMap = new HashMap<>();

            cartMap.put("productID", txtID.getText().toString());
            cartMap.put("productName", titleTxt.getText().toString());
            cartMap.put("productPrice", priceTxt.getText().toString());
            cartMap.put("currentTime", saveCurrentTime);
            cartMap.put("currentDate", saveCurrentDate);
            cartMap.put("totalQuanity", txtQuanity.getText().toString());
            cartMap.put("totalPrice", totalPrice);
            cartMap.put("pic", picURL);

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
                                        Toast.makeText(DetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    // Nếu chưa tồn tại, thêm vào FireStore.
                                    df.collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Toast.makeText(DetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
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

    }

    private void initView() {
        addToCartBtn = findViewById(R.id.addToCartBtn);
        buyBtn = findViewById(R.id.buyBtn);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        backBtn = findViewById(R.id.backBtn);
        imgCMT = findViewById(R.id.imgCMT);
        titleTxt = findViewById(R.id.titleTxt);
        priceTxt = findViewById(R.id.priceTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        reviewTxt = findViewById(R.id.reviewTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        txtQuanity = findViewById(R.id.txtQuanity);
        txtID = findViewById(R.id.txtID);

        txtID.setVisibility(View.GONE);

        itemPic = findViewById(R.id.itemPic);
    }
    private void showComment(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_feedback);
        dialog.setCanceledOnTouchOutside(true);
        EditText nameFeedbackTxt = dialog.findViewById(R.id.nameFeedbackTxt);
        EditText editTextFeedback = dialog.findViewById(R.id.editTextFeedback);
        Button btnFeedback = dialog.findViewById(R.id.btnFeedback);
        // Khởi tạo RecyclerView và Adapter
        RecyclerView feedbackListView = dialog.findViewById(R.id.feedBackView);
        feedbackListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(listFeedback, this);
        feedbackListView.setAdapter(feedbackAdapter);
        name = objectPopular.getTitle();
        picURL = objectPopular.getPicURL();

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = nameFeedbackTxt.getText().toString().trim();
                String feedback = editTextFeedback.getText().toString().trim();
                if (!TextUtils.isEmpty(nickName)&&!TextUtils.isEmpty(feedback)){
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    CollectionReference feedbackCollection = firestore.collection("Feedbacks");
                    Map<String,Object> feedbackData = new HashMap<>();
                    feedbackData.put("nickname",nickName);
                    feedbackData.put("feedback",feedback);
                    feedbackData.put("name",name);
                    feedbackData.put("pic",picURL);
                    feedbackCollection.add(feedbackData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(DetailActivity.this, "Phản hồi đã được gửi thành công!Cảm ơn bạn đã phản hồi", Toast.LENGTH_SHORT).show();
                            nameFeedbackTxt.setText("");
                            editTextFeedback.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Lỗi gửi phản hồi,vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    // Hiển thị thông báo nếu người dùng không nhập đủ dữ liệu
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập tên và phản hồi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;; // Adjust the fraction as needed
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }
}