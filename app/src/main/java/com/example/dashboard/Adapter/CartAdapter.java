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
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.Helper.ChangeNumberItemsListener;
import com.example.dashboard.Helper.ManagementCart;
import com.example.dashboard.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    ArrayList<PopularDomain> listItemSelected;
    private ManagementCart managementCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<PopularDomain> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        holder.titleTxt.setText(listItemSelected.get(position).getTitle());
        holder.priceEachItem.setText(listItemSelected.get(position).getPrice() + " VNĐ");

        try {
            double price = Double.parseDouble(listItemSelected.get(position).getPrice());
            holder.totalEachItem.setText(Math.round(listItemSelected.get(position).getNumberInCart() * price) + " VNĐ");
        } catch (NumberFormatException e) {
            holder.totalEachItem.setText("Error: Price is not a valid number");
        }

        holder.numberItem.setText(String.valueOf(listItemSelected.get(position).getNumberInCart()));

        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getPicURL())
                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
                .into(holder.pic); //hiển thị trên ImageView tương ứng

        holder.plusCartBtn.setOnClickListener(v -> managementCart.plusNumberItem(listItemSelected, position, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            }
        }));

        holder.minusCartBtn.setOnClickListener(v -> managementCart.minusNumberItem(listItemSelected, position, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            }
        }));
    }


    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView titleTxt, priceEachItem, totalEachItem, minusCartBtn, plusCartBtn, numberItem;
        ImageView pic;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceEachItem = itemView.findViewById(R.id.priceEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            numberItem = itemView.findViewById(R.id.numberItem);

            pic = itemView.findViewById(R.id.pic);
        }
    }

}
