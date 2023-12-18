package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.dashboard.Adapter.CartAdapter;
import com.example.dashboard.Adapter.DeleteProductAdapter;
import com.example.dashboard.Adapter.EditProductAdapter;
import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DeleteProductActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterEditProduct;
    private RecyclerView recyclerViewDeleteProduct;
    private DeleteProductAdapter deleteProductAdapter;
    ArrayList<ProductDomain> listProdcut = new ArrayList<>();
    FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ImageView backBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        deleteRecyclerView();

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewDeleteProduct);
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

                if (position < listProdcut.size()) {
                    // Lưu lại sản phẩm và ID trước khi xóa
                    ProductDomain deletedItem = listProdcut.get(position);
                    String id = deletedItem.getId();

                    // Xóa sản phẩm khỏi Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("products").document(id)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // Xóa khỏi giỏ hàng sau khi xóa thành công từ Firestore
                                        listProdcut.remove(position);
                                        deleteProductAdapter.notifyItemRemoved(position); // Thông báo xoá item
                                        Snackbar.make(recyclerViewDeleteProduct, "Đã xóa " + deletedItem.getName() + " khỏi giỏ hàng", Snackbar.LENGTH_LONG).show();
                                    }else {
                                        Snackbar.make(recyclerViewDeleteProduct, "Lỗi! Không thể xóa " + deletedItem.getName() + " khỏi giỏ hàng", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        }




        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView1, @NonNull RecyclerView.ViewHolder viewHolder1
                , float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(DeleteProductActivity.this, c, recyclerView1, viewHolder1, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(DeleteProductActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView1, viewHolder1, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteRecyclerView() {
        ArrayList<ProductDomain> products = new ArrayList<>();

        recyclerViewDeleteProduct = findViewById(R.id.productView);
        recyclerViewDeleteProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        fStore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                ProductDomain object = documentSnapshot.toObject(ProductDomain.class);
                                products.add(object);
                            }
                            // Khởi tạo adapter sau khi đã thêm dữ liệu vào items
                            deleteProductAdapter = new DeleteProductAdapter(products, DeleteProductActivity.this);
                            recyclerViewDeleteProduct.setAdapter(deleteProductAdapter);
                            // Cập nhật giao diện sau khi đã thêm tất cả các phần tử vào items
                            deleteProductAdapter.notifyDataSetChanged();

                        } else {
                            // Xử lý lỗi khi task không thành công
                            Log.d("popRecyclerView", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}