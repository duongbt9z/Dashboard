package com.example.dashboard.Activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.Adapter.DeleteProductAdapter;
import com.example.dashboard.Adapter.UserAdapter;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.Domain.UserDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListUser extends AppCompatActivity {
    private RecyclerView recyclerViewUser;
    private UserAdapter userAdapter;
    private ArrayList<UserDomain> listUser = new ArrayList<>();
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setupRecyclerView();
        initView();
        addEvent();

        // Thiết lập ItemTouchHelper để thực hiện chức năng vuốt để xóa
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewUser);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                if (position < listUser.size()) {
                    UserDomain deletedItem = listUser.get(position);
                    String id = deletedItem.getId();

                    // Xóa sản phẩm từ Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Users").document(id)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Cập nhật dataset local và thông báo cho adapter
                                        listUser.remove(position);
                                        userAdapter.notifyItemRemoved(position);

                                        // Hiển thị thông báo Snackbar
                                        Snackbar.make(recyclerViewUser, "Đã xóa " + deletedItem.getName() + " khỏi giỏ hàng", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        // Hiển thị thông báo lỗi nếu quá trình xóa thất bại
                                        Snackbar.make(recyclerViewUser, "Lỗi! Không thể xóa " + deletedItem.getName() + " khỏi giỏ hàng", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView1, @NonNull RecyclerView.ViewHolder viewHolder1,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // Tùy chỉnh hành vi vuốt
            new RecyclerViewSwipeDecorator.Builder(ListUser.this, c, recyclerView1, viewHolder1, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ListUser.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView1, viewHolder1, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void setupRecyclerView() {
        recyclerViewUser = findViewById(R.id.listUserView);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        firestore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String documentID = documentSnapshot.getId();

                                UserDomain user = documentSnapshot.toObject(UserDomain.class);

                                user.setId(documentID);
                                listUser.add(user);
                            }

                            // Khởi tạo adapter sau khi thêm dữ liệu vào danh sách
                            userAdapter = new UserAdapter(listUser, ListUser.this);
                            recyclerViewUser.setAdapter(userAdapter);

                            // Thông báo cho adapter sau khi thêm tất cả các phần tử vào danh sách
                            userAdapter.notifyDataSetChanged();
                        } else {
                            // Xử lý lỗi nếu task không thành công
                            Log.e("recyclerViewUser", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void  initView(){
        backBtn = findViewById(R.id.backBtn);
    }
    private void  addEvent(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}