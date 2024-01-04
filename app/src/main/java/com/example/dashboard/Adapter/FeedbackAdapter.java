package com.example.dashboard.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.dashboard.Domain.FeedbackDomain;
import com.example.dashboard.R;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.Viewholder> {
    Context context;
   ArrayList<FeedbackDomain> listFeedback;

    public FeedbackAdapter(ArrayList<FeedbackDomain> listFeedback, Context context) {
        this.listFeedback = listFeedback;
        this.context = context;
    }
    @NonNull
    @Override
    public FeedbackAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_feedback, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.Viewholder holder, int position) {
        holder.productName.setText(listFeedback.get(position).getName());
        holder.nickName.setText(listFeedback.get(position).getNickname());
        holder.feedbackTxt.setText(listFeedback.get(position).getFeedback());
        Glide.with(context)
                .load(listFeedback.get(position).getPic())
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return listFeedback.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        TextView productName, nickName, feedbackTxt;
        ImageView pic;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            nickName = itemView.findViewById(R.id.nickName);
            feedbackTxt = itemView.findViewById(R.id.feedbackTxt);

            pic = itemView.findViewById(R.id.pic);
        }
    }
}
