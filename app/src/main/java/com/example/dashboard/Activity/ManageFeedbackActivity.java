package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.dashboard.Adapter.FeedbackAdapter;
import com.example.dashboard.Domain.FeedbackDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageFeedbackActivity extends AppCompatActivity {
    private RecyclerView feedbackListView;
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<FeedbackDomain> listFeedback = new ArrayList<>();
    private FirebaseFirestore firestore;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_feedback);
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
        feedbackListView = findViewById(R.id.feedbackListView);
        feedbackListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
                        feedbackAdapter = new FeedbackAdapter(listFeedback, ManageFeedbackActivity.this);
                        feedbackListView.setAdapter(feedbackAdapter);

                        // Thông báo cho adapter sau khi thêm tất cả các phần tử vào danh sách
                        feedbackAdapter.notifyDataSetChanged();
                    }
                });

    }
}