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
import com.example.dashboard.Activity.DetailActivity;
import com.example.dashboard.Domain.PopularDomain;
import com.example.dashboard.R;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    ArrayList<PopularDomain> items;
    Context context;

    public PopularAdapter(ArrayList<PopularDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    //tạo ra một ViewHolder mới để chứa View hiển thị một mục.
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //chuyển đổi một layout xml thành một đối tượng View
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pop_list, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    //gán dữ liệu cho ViewHolder tại một vị trí cụ thể trong danh sách.
    public void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.titleTxtt.setText(items.get(position).getTitle());
        holder.feeTxt.setText(items.get(position).getPrice() + " VNĐ");
        holder.scoreTxt.setText("" + items.get(position).getScore());
        holder.reviewTxt.setText("" + items.get(position).getReview());



        //dùng thư viện để load ảnh
        Glide.with(context)
                .load(items.get(position).getPicURL())
                .transform(new GranularRoundedCorners(30, 30, 0, 0)) //bo tròn góc ảnh
                .into(holder.pic); //hiển thị trên ImageView tương ứng

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("object", items.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
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
