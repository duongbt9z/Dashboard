package com.example.dashboard.Adapter;

import android.annotation.SuppressLint;
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
import com.example.dashboard.Activity.TypedetailActivity;
import com.example.dashboard.Domain.ProductTypeDomain;
import com.example.dashboard.R;

import java.util.ArrayList;

public class ProductTypeAdapter extends RecyclerView.Adapter<ProductTypeAdapter.Viewholder> {

    ArrayList<ProductTypeDomain> items_type;
    Context context;

    public ProductTypeAdapter(ArrayList<ProductTypeDomain> items_type) {
        this.items_type = items_type;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pop_list, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.titleTxtt.setText(items_type.get(position).getName());
        holder.feeTxt.setText(items_type.get(position).getPrice() + " VNĐ");
        holder.scoreTxt.setText("" + items_type.get(position).getScore());
        holder.reviewTxt.setText("" + items_type.get(position).getReview());

        Glide.with(context)
                .load(items_type.get(position).getPicURL())
                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
                .into(holder.pic); //hiển thị trên ImageView tương ứng

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), TypedetailActivity.class);
                intent.putExtra("items_type", items_type.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items_type.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView scoreTxt, titleTxtt, feeTxt, reviewTxt;
        ImageView pic;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxtt = itemView.findViewById(R.id.titleTxtt);
            feeTxt = itemView.findViewById(R.id.feeTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
            reviewTxt = itemView.findViewById(R.id.reviewTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
