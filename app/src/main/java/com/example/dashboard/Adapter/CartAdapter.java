package com.example.dashboard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.Domain.CartDomain;
import com.example.dashboard.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    ArrayList<CartDomain> listItem;
    Context context;
//    private ManagementCart managementCart;

    public CartAdapter(ArrayList<CartDomain> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.titleTxt.setText(listItem.get(position).getProductName());
        holder.date.setText(listItem.get(position).getCurrentDate() + "\n" + listItem.get(position).getCurrentTime());


        try {
            double totalEachItem = listItem.get(position).getTotalPrice();
            String format = formatter.format(totalEachItem);
            holder.totalEachItem.setText("\t" + format + " VNĐ");
        } catch (NumberFormatException e) {
            holder.totalEachItem.setText("Lôi: Giá tiền không hợp lệ");
        }

        holder.numberItem.setText(String.valueOf(listItem.get(position).getTotalQuanity()));

//        Glide.with(holder.itemView.getContext())
//                .load(listItem.get(position).getPic())
//                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
//                .into(holder.pic); //hiển thị trên ImageView tương ứng
    }



    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public ArrayList<CartDomain> getListItem() {
        return listItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, date, totalEachItem, numberItem;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            date = itemView.findViewById(R.id.date);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            numberItem = itemView.findViewById(R.id.numberItem);

            pic = itemView.findViewById(R.id.pic);

            date.setVisibility(View.GONE);
        }
    }
}


