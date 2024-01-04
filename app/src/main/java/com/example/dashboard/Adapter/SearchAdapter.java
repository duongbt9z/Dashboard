package com.example.dashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.dashboard.Activity.DetailActivity;
import com.example.dashboard.Activity.TypedetailActivity;
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.Domain.UserDomain;
import com.example.dashboard.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Viewholder> {
     Context context;
     ArrayList<ProductDomain> listProduct;
     ArrayList<ProductDomain> filteredList;
    public SearchAdapter(ArrayList<ProductDomain> listProduct, Context context) {
        this.listProduct = listProduct;
        this.filteredList = new ArrayList<>(listProduct);
        this.context = context;
    }
    @NonNull
    @Override
    public SearchAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //chuyển đổi một layout xml thành một đối tượng View
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_product_list, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.Viewholder holder, int position) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        ProductDomain productDomain = filteredList.get(position);
        holder.titleTxt.setText(productDomain.getName());
        try {
            Double price = Double.parseDouble(productDomain.getPrice());
            String format = formatter.format(price);
            holder.totalEachItem.setText("\t" + format + " VNĐ");
        } catch (NumberFormatException e) {
            holder.totalEachItem.setText("Lôi: Giá tiền không hợp lệ");
        }
        Glide.with(context)
                .load(productDomain.getPicURL())
                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
                .into(holder.pic);

        ArrayList<ProductDomain> singleProduct = new ArrayList<>();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDomain product = filteredList.get(holder.getAdapterPosition());
                singleProduct.clear();
                singleProduct.add(product);
                Intent intent = new Intent(holder.itemView.getContext(), TypedetailActivity.class);
                intent.putExtra("object", singleProduct);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, totalEachItem;
        ImageView pic;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);

            pic = itemView.findViewById(R.id.pic);
        }
    }

    public void setFilteredList(ArrayList<ProductDomain> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }
}
