package com.example.dashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.Activity.PaymentActivity;
import com.example.dashboard.Domain.AddressDomain;
import com.example.dashboard.R;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.Viewholder> {
    ArrayList<AddressDomain> listAddress;
    Context context;
    SelectedAddress selectedAddress;
    private RadioButton selectedRadioBtn;

    public AddressAdapter(ArrayList<AddressDomain> listAddress, Context context, SelectedAddress selectedAddress) {
        this.listAddress = listAddress;
        this.context = context;
        this.selectedAddress = selectedAddress;
    }

    @NonNull
    @Override
    public AddressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.Viewholder holder, int position) {
        holder.txtAddress.setText(listAddress.get(position).getUserAddress() + "");
        holder.select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (AddressDomain addressDomain : listAddress) {
                    addressDomain.setSelected(false);
                }
                listAddress.get(position).setSelected(true);

                if(selectedRadioBtn != null){
                    selectedRadioBtn.setChecked(false);
                }
                selectedRadioBtn = (RadioButton) v;
                selectedRadioBtn.setChecked(true);
                String address = listAddress.get(position).getUserAddress();
                selectedAddress.setAddress(address);

                // Khởi tạo Intent để mở PaymentActivity
                Intent intent = new Intent(context, PaymentActivity.class);
                // Đặt địa chỉ đã chọn làm dữ liệu extra của Intent
                intent.putExtra("selectedAddress", address);
                // Thêm cờ FLAG_ACTIVITY_NEW_TASK vào Intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Mở PaymentActivity
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listAddress.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView txtAddress;
        RadioButton select_address;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            select_address = itemView.findViewById(R.id.select_address);
        }
    }
    public interface SelectedAddress{
        void setAddress(String address);

    }
}
