package com.example.youjurental.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;
import com.example.youjurental.entity.House;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.CollectHolder> {
    private List<House> houses;
    private OnCollectActionListener listener;

    public interface OnCollectActionListener {
        void onItemClick(House house);
        void onRemoveCollect(House house);
    }

    public CollectAdapter(List<House> houses, OnCollectActionListener listener) {
        this.houses = houses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CollectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collect, parent, false);
        return new CollectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectHolder holder, int position) {
        House house = houses.get(position);
        holder.tvCommunity.setText(house.getCommunityName());
        holder.tvRent.setText(house.getMonthlyRent() + " 元/月");
        holder.tvInfo.setText(house.getHouseType() + " | " + (int) house.getBuildingArea() + "㎡ | " + house.getDistrict());

        com.example.youjurental.util.PhotoUtil.loadFirstPhoto(
                holder.itemView.getContext(), holder.ivHouse, house.getImageUrls());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(house);
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveCollect(house);
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

    static class CollectHolder extends RecyclerView.ViewHolder {
        ImageView ivHouse;
        TextView tvCommunity, tvRent, tvInfo;
        MaterialButton btnRemove;

        CollectHolder(@NonNull View itemView) {
            super(itemView);
            ivHouse = itemView.findViewById(R.id.iv_house);
            tvCommunity = itemView.findViewById(R.id.tv_community);
            tvRent = itemView.findViewById(R.id.tv_rent);
            tvInfo = itemView.findViewById(R.id.tv_info);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
