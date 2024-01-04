package com.example.dashboard.Activity;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dashboard.Adapter.FeedbackAdapter;
import com.example.dashboard.Adapter.ProductTypeAdapter;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Domain.FeedbackDomain;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Domain.ProductTypeDomain;

import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TypedetailActivity extends AppCompatActivity {
    private Button addToCartBtn, buyBtn;
    private TextView titleTxt, priceTxt, scoreTxt, reviewTxt, descriptionTxt, txtQuanity, plusBtn, minusBtn, txtID;
    private ImageView itemPic, backBtn, wishListBtn, imgCMT;
    private ProductTypeDomain ProductTypeAdapter;
    private ArrayList<ProductTypeDomain> listLove;
    private com.example.dashboard.Adapter.ProductTypeAdapter productTypeAdapter;
    private int numberOrder = 0;
    private double totalPrice = 0;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ArrayList<FeedbackDomain> listFeedback = new ArrayList<>();
    String picURL;
    String name;
    boolean checklove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typedetail);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        initView();
        getProductObject();
        getLove();
    }


    private void getProductObject() {
        ProductTypeAdapter = (ProductTypeDomain) getIntent().getSerializableExtra("items_type"); // lấy PopularDomain từ Intent thông qua object
        //Dùng thư viện glide để load ảnh
        String pic = ProductTypeAdapter.getPicURL();
        picURL = ProductTypeAdapter.getPicURL();
        Glide.with(this)
                .load(pic)
                .into(itemPic); //hiển thị trên ImageView tương ứng
        txtID.setText(ProductTypeAdapter.getId() + "");
        titleTxt.setText(ProductTypeAdapter.getName());
        priceTxt.setText(ProductTypeAdapter.getPrice() + " VNĐ");
        scoreTxt.setText(ProductTypeAdapter.getScore() + "");
        reviewTxt.setText(ProductTypeAdapter.getReview() + "");
        descriptionTxt.setText(ProductTypeAdapter.getDescription());
        double price = Double.parseDouble(ProductTypeAdapter.getPrice());


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
                    Toast.makeText(TypedetailActivity.this, "Vui lòng nhập số lượng sản phẩm!", Toast.LENGTH_SHORT).show();
                } else {
                    addToCart();
                    startActivity(new Intent(TypedetailActivity.this, PaymentActivity.class));
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOrder > 0) {
                    numberOrder--;
                    txtQuanity.setText(String.valueOf(numberOrder));
                    if (ProductTypeAdapter != null) {
                        double price = Double.parseDouble(ProductTypeAdapter.getPrice());
                        totalPrice = price * numberOrder;
                    }
                }
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOrder < 100) {
                    numberOrder++;
                    txtQuanity.setText(String.valueOf(numberOrder));
                    if (ProductTypeAdapter != null) {
                        double price = Double.parseDouble(ProductTypeAdapter.getPrice());
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

        //nút wishlist
        wishListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (checklove == false) {
                    addLove(true);
                    checklove = true;
                    wishListBtn.setImageResource(R.drawable.wishlist2);

                } else if (checklove == true) {
                    wishListBtn.setImageResource(R.drawable.wishlist);
                    checklove = false;
                    addLove(false);
                }
            }
        });
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

    }


    private void getLove() {
        FirebaseUser user = fAuth.getCurrentUser();
        String id = ProductTypeAdapter.getId();
        if (user != null) {
            DocumentReference df = fStore.collection("AddTolove").document(user.getUid());
            df.collection("User").document(id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Boolean test = document.getBoolean("love");
                                if (document.exists()) {
                                    checklove = true;
                                    wishListBtn.setImageResource(R.drawable.wishlist2);
                                } else {
                                    checklove = false;
                                    wishListBtn.setImageResource(R.drawable.wishlist);
                                    // Sản phẩm chưa được yêu thích
                                    Log.d("TAG", "Document does not exist or is null");
                                }
                            } else {
                                Log.d("TAG", "Error getting love status: ", task.getException());
                            }
                        }
                    });
        } else {
            Log.d("TAG", "User is not logged in");
        }
    }

    private void addLove(boolean love) {
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) { // Đảm bảo người dùng đã đăng nhập
            DocumentReference df = fStore.collection("AddTolove").document(user.getUid());

            Map<String, Object> data = new HashMap<>();
            String name = ProductTypeAdapter.getName();
            String price = ProductTypeAdapter.getPrice();
            String description = ProductTypeAdapter.getDescription();
            String picURL = ProductTypeAdapter.getPicURL();
            String id = ProductTypeAdapter.getId();
            double score = Double.valueOf(String.valueOf(ProductTypeAdapter.getScore()));
            double review = Double.parseDouble(String.valueOf(ProductTypeAdapter.getReview()));
            data.put("name", name);
            data.put("price", price);
            data.put("description", description);
            data.put("picURL", picURL);
            data.put("id", id);
            data.put("score", score);
            data.put("review", review);
            data.put("love", love);
            Map<String, Object> datalove = new HashMap<>();
            if (love == true) {

                df.get() // Lấy tài liệu của người dùng hiện tại
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    df.collection("User").document(id).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.w("TAG", " thêm sản phẩm yêu thích: ");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Lỗi thêm sản phẩm yêu thích: ", e);
                                                }
                                            });

                                } else {
                                    Log.d("TAG", "Error getting document: ", task.getException());
                                }
                            }
                        });
            } else {
                df.collection("User").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                datalove.put("love", FieldValue.delete());
                                startActivity(new Intent(TypedetailActivity.this, MainActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "Error getting document: ", e);
                            }
                        });
            }

        }
    }


    private void initView() {
        addToCartBtn = findViewById(R.id.addToCartBtn);
        buyBtn = findViewById(R.id.buyBtn);
        wishListBtn = findViewById(R.id.wishListBtn);
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

    private void showComment() {
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
        name = ProductTypeAdapter.getName();
        picURL = ProductTypeAdapter.getPicURL();

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = nameFeedbackTxt.getText().toString().trim();
                String feedback = editTextFeedback.getText().toString().trim();
                if (!TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(feedback)) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    CollectionReference feedbackCollection = firestore.collection("Feedbacks");
                    Map<String, Object> feedbackData = new HashMap<>();
                    feedbackData.put("nickname", nickName);
                    feedbackData.put("feedback", feedback);
                    feedbackData.put("name", name);
                    feedbackData.put("pic", picURL);
                    feedbackCollection.add(feedbackData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(TypedetailActivity.this, "Phản hồi đã được gửi thành công!Cảm ơn bạn đã phản hồi", Toast.LENGTH_SHORT).show();
                            nameFeedbackTxt.setText("");
                            editTextFeedback.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TypedetailActivity.this, "Lỗi gửi phản hồi,vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Hiển thị thông báo nếu người dùng không nhập đủ dữ liệu
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập tên và phản hồi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        ; // Adjust the fraction as needed
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }
}