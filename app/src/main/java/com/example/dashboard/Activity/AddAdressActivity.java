package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAdressActivity extends AppCompatActivity {
    private EditText ad_name, ad_phone, ad_city, ad_street;
    private Button btnDone;
    private ImageView backBtn;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adress);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ad_name = findViewById(R.id.ad_name);
        ad_phone = findViewById(R.id.ad_phone);
        ad_city = findViewById(R.id.ad_city);
        ad_street = findViewById(R.id.ad_street);
        btnDone = findViewById(R.id.btnDone);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = ad_name.getText().toString();
                String userPhoneNumber = ad_phone.getText().toString();
                String userCity = ad_city.getText().toString();
                String userStreet = ad_street.getText().toString();

                String final_address = "";

                if (!userName.isEmpty()){
                    final_address += userName + "\n";
                }
                if (!userPhoneNumber.isEmpty()){
                    final_address += userPhoneNumber + "\n";
                }
                if (!userPhoneNumber.isEmpty()){
                    final_address += userCity + "\n";
                }
                if (!userStreet.isEmpty()){
                    final_address += userStreet;
                }
                if (!userName.isEmpty() && !userPhoneNumber.isEmpty() && !userPhoneNumber.isEmpty() && !userStreet.isEmpty()){
                    Map<String, String> addressMap = new HashMap<>();
                    addressMap.put("userAddress", final_address);

                    firestore.collection("Users").document(auth.getCurrentUser().getUid())
                            .collection("Address").add(addressMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(AddAdressActivity.this, "Thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AddAdressActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}