package com.example.dashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.dashboard.Activity.AdminEditProduct;
import com.example.dashboard.Activity.DetailActivity;
import com.example.dashboard.Activity.EditActivity;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.R;

import java.util.ArrayList;

public class EditProductAdapter extends RecyclerView.Adapter<EditProductAdapter.Viewholder> {
    ArrayList<ProductDomain> products;
    Context context;

    public EditProductAdapter(ArrayList<ProductDomain> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //chuyển đổi một layout xml thành một đối tượng View
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_edit_product, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.nameProduct.setText(products.get(position).getName());
        holder.priceProduct.setText(products.get(position).getPrice() + " VNĐ");
        holder.scoreProduct.setText("" + products.get(position).getScore());
        holder.reviewProduct.setText("" + products.get(position).getReview());
        holder.idProduct.setText(products.get(position).getId());
        holder.describeProduct.setText(products.get(position).getDescription());
        holder.categoryProduct.setText(products.get(position).getCategory());

        Glide.with(context)
                .load(products.get(position).getPicURL())
                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
                .into(holder.imageProduct); //hiển thị trên ImageView tương ứng

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), EditActivity.class);
                intent.putExtra("object", products.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView scoreProduct, nameProduct, priceProduct, reviewProduct, idProduct, categoryProduct, describeProduct;
        ImageView imageProduct;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            scoreProduct = itemView.findViewById(R.id.scoreProduct);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            priceProduct = itemView.findViewById(R.id.priceProduct);
            reviewProduct = itemView.findViewById(R.id.reviewProduct);
            idProduct = itemView.findViewById(R.id.idProduct);
            categoryProduct = itemView.findViewById(R.id.categoryProduct);
            describeProduct = itemView.findViewById(R.id.describeProduct);


            imageProduct = itemView.findViewById(R.id.imageProduct);
        }
    }
}
