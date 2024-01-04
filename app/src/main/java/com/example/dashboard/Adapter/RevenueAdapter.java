package com.example.dashboard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.dashboard.Domain.RevenueDomain;
import com.example.dashboard.Domain.UserDomain;
import com.example.dashboard.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.Viewholder> {
    ArrayList<RevenueDomain> revenue;
    Context context;
    public RevenueAdapter(ArrayList<RevenueDomain> revenue, Context context) {
        this.revenue = revenue;
        this.context = context;
    }
    @NonNull
    @Override
    public RevenueAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //chuyển đổi một layout xml thành một đối tượng View
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_revenue, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueAdapter.Viewholder holder, int position) {
        holder.nameProduct.setText(revenue.get(position).getProductName());
        holder.quanityProduct.setText(revenue.get(position).getQuanity());
        holder.priceProduct.setText(revenue.get(position).getTotalPrice());
        holder.orderTime.setText(revenue.get(position).getOrderTime());
        holder.orderDate.setText(revenue.get(position).getOrderDate());
        holder.address.setText(revenue.get(position).getAddress());

        Glide.with(context)
                .load(revenue.get(position).getPic())
                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
                .into(holder.imageProduct);

    }

    @Override
    public int getItemCount() {
        return revenue.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView nameProduct, quanityProduct, priceProduct, orderTime, orderDate, address;
        ImageView imageProduct;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            quanityProduct = itemView.findViewById(R.id.quanityProduct);
            priceProduct = itemView.findViewById(R.id.priceProduct);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderDate = itemView.findViewById(R.id.orderDate);
            address = itemView.findViewById(R.id.address);
            imageProduct = itemView.findViewById(R.id.imageProduct);
        }

    }
}
