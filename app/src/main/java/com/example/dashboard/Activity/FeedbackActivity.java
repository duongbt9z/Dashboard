package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dashboard.Adapter.FeedbackAdapter;
import com.example.dashboard.Adapter.UserAdapter;
import com.example.dashboard.Domain.FeedbackDomain;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Domain.UserDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    private RecyclerView feedBackView;
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<FeedbackDomain> listFeedback = new ArrayList<>();
    private FirebaseFirestore firestore;
    private PopularDomain objectPopular;
    ImageView backBtn;
    String name;
    String picURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        initList();
    }

    private void initList() {
        feedBackView = findViewById(R.id.feedBackView);
        feedBackView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        firestore.collection("Feedbacks")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("feedbackListView", "Error getting documents: ", e);
                            return;
                        }

                        // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        listFeedback.clear();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            FeedbackDomain feedback = documentSnapshot.toObject(FeedbackDomain.class);
                            listFeedback.add(feedback);
                        }

                        // Khởi tạo adapter sau khi thêm dữ liệu vào danh sách
                        feedbackAdapter = new FeedbackAdapter(listFeedback, FeedbackActivity.this);
                        feedBackView.setAdapter(feedbackAdapter);

                        // Thông báo cho adapter sau khi thêm tất cả các phần tử vào danh sách
                        feedbackAdapter.notifyDataSetChanged();
                    }
                });
    }
}