package com.example.dashboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ConfirmPasswordActivity extends AppCompatActivity {
    EditText edtTextNewPassword;
    Button btnConfirmPassword;

    FirebaseFirestore db;
    FirebaseAuth user;// Change to FirebaseFirestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);

        edtTextNewPassword = findViewById(R.id.edtTextNewPassword);
        btnConfirmPassword = findViewById(R.id.btnConfirmPassword);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance();// Change to FirebaseFirestore

        btnConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = edtTextNewPassword.getText().toString().trim();
                confirmAndResetPassword(newPassword);
                Intent intent = new Intent(ConfirmPasswordActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void confirmAndResetPassword(final String newPassword) {
        // Assuming you have a user ID
        FirebaseUser currentUser = user.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Prepare the data to update
            Map<String, Object> data = new HashMap<>();
            data.put("password", newPassword);

            // Update the password in "User" collection
            db.collection("Users").document(userId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                            Toast.makeText(ConfirmPasswordActivity.this, "cập nhật được", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error updating document", e);
                            Toast.makeText(ConfirmPasswordActivity.this, "Đ cập nhật được", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}