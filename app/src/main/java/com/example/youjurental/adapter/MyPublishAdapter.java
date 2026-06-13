package com.example.youjurental.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;
import com.example.youjurental.entity.House;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MyPublishAdapter extends RecyclerView.Adapter<MyPublishAdapter.PublishHolder> {
    private List<House> houses;
    private OnPublishActionListener listener;

    public interface OnPublishActionListener {
        void onEdit(House house);
        void onDelete(House house);
    }

    public MyPublishAdapter(List<House> houses, OnPublishActionListener listener) {
        this.houses = houses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PublishHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_publish, parent, false);
        return new PublishHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublishHolder holder, int position) {
        House house = houses.get(position);
        holder.tvCommunity.setText(house.getCommunityName());
        holder.tvRent.setText(house.getMonthlyRent() + " 元/月");
        holder.tvInfo.setText(house.getHouseType() + " | " + (int) house.getBuildingArea() + "㎡ | " + house.getDistrict());

        com.example.youjurental.util.PhotoUtil.loadFirstPhoto(
                holder.itemView.getContext(), holder.ivHouse, house.getImageUrls());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(house);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(house);
        });
    }

    @Override
    public int getItemCount() { return houses.size(); }

    public void removeItem(House house) {
        int index = houses.indexOf(house);
        if (index >= 0) {
            houses.remove(index);
            notifyItemRemoved(index);
        }
    }

    static class PublishHolder extends RecyclerView.ViewHolder {
        ImageView ivHouse;
        TextView tvCommunity, tvRent, tvInfo;
        MaterialButton btnEdit, btnDelete;

        PublishHolder(@NonNull View itemView) {
            super(itemView);
            ivHouse = itemView.findViewById(R.id.iv_house);
            tvCommunity = itemView.findViewById(R.id.tv_community);
            tvRent = itemView.findViewById(R.id.tv_rent);
            tvInfo = itemView.findViewById(R.id.tv_info);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
