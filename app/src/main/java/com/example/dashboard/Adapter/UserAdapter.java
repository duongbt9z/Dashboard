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
import com.example.dashboard.Domain.ProductDomain;
import com.example.dashboard.Domain.UserDomain;
import com.example.dashboard.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
    ArrayList<UserDomain> user;
    Context context;
    public UserAdapter(ArrayList<UserDomain> user, Context context) {
        this.user = user;
        this.context = context;
    }
    @NonNull
    @Override
    public UserAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //chuyển đổi một layout xml thành một đối tượng View
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_user, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.Viewholder holder, int position) {
        holder.idUserTxt.setText(user.get(position).getId());
        holder.usernameTxt.setText(user.get(position).getName());
        holder.emailTxt.setText(user.get(position).getEmail());
        holder.phoneTxt.setText(user.get(position).getPhoneNumber());
        holder.passwordTxt.setText(user.get(position).getPassword());

        Glide.with(context)
                .load(user.get(position).getAvatar())
                .circleCrop() //bo tròn góc ảnh
                .into(holder.userAvatarImageView);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView idUserTxt, usernameTxt, emailTxt, passwordTxt, phoneTxt;
        ImageView userAvatarImageView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            idUserTxt = itemView.findViewById(R.id.idUserTxt);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            emailTxt = itemView.findViewById(R.id.emailTxt);
            passwordTxt = itemView.findViewById(R.id.passwordTxt);
            phoneTxt = itemView.findViewById(R.id.phoneTxt);

            userAvatarImageView = itemView.findViewById(R.id.userAvatarImageView);
        }
    }
}
