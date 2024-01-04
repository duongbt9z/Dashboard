package com.example.dashboard.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dashboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class admin_login extends AppCompatActivity {
    EditText edtEmailLoginAdmin, edtPasswordLoginAdmin;
    Button btnLoginAdmin;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        initView();
        addEvent();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void initView() {
        edtEmailLoginAdmin = findViewById(R.id.edtEmailLoginAdmin);
        edtPasswordLoginAdmin = findViewById(R.id.edtPasswordLoginAdmin);
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
    }

    private void addEvent() {
        btnLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin đầu vào từ người dùng
                String email = edtEmailLoginAdmin.getText().toString().trim();
                String password = edtPasswordLoginAdmin.getText().toString().trim();

                // Kiểm tra xem thông tin có hợp lệ không
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(admin_login.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Truy vấn Firestore để kiểm tra xem tài khoản có tồn tại hay không
                firestore.collection("Admin_Account")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    // Tài khoản admin khớp, tiến hành đăng nhập
                                    signInAdmin(email, password);
                                } else {
                                    // Nếu không tìm thấy tài khoản khớp
                                    Toast.makeText(admin_login.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Xử lý khi truy vấn thất bại
                                Toast.makeText(admin_login.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show();
                                Log.e("Firestore", "Error getting documents: ", task.getException());
                            }
                        });
            }
        });
    }

    private void signInAdmin(String email, String password) {
        // Xác thực admin với Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Firebase Authentication thành công
                        FirebaseUser user = auth.getCurrentUser();
                        Intent intent = new Intent(admin_login.this, Admin.class);
                        startActivity(intent);
                        Toast.makeText(admin_login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Firebase Authentication thất bại
                        Toast.makeText(admin_login.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseAuth", "signInWithEmailAndPassword failed", task.getException());
                    }
                });
    }
}