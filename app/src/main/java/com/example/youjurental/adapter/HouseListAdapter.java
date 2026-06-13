package com.example.youjurental.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;
import com.example.youjurental.entity.House;
import com.example.youjurental.util.PhotoUtil;
import com.google.android.material.chip.Chip;

import java.util.List;

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.HouseHolder> {
    private List<House> houses;
    private OnHouseClickListener listener;

    public interface OnHouseClickListener {
        void onHouseClick(House house);
    }

    public HouseListAdapter(List<House> houses, OnHouseClickListener listener) {
        this.houses = houses;
        this.listener = listener;
    }

    public void updateData(List<House> newHouses) {
        this.houses = newHouses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HouseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_house, parent, false);
        return new HouseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseHolder holder, int position) {
        House house = houses.get(position);
        holder.tvCommunity.setText(house.getCommunityName());
        holder.tvRent.setText(house.getMonthlyRent() + " 元/月");
        String info = house.getHouseType() + " | " + (int) house.getBuildingArea() + "㎡ | " + house.getDistrict() + house.getArea();
        holder.tvInfo.setText(info);

        // Set banner color based on position
        // Load first photo
        PhotoUtil.loadFirstPhoto(
                holder.itemView.getContext(), holder.ivHouse, house.getImageUrls());

        // Tags
        holder.layoutTags.removeAllViews();
        if (house.getTags() != null && !house.getTags().isEmpty()) {
            String[] tags = house.getTags().split(",");
            int count = 0;
            for (String tag : tags) {
                if (count >= 3) break;
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    Chip chip = new Chip(holder.itemView.getContext());
                    chip.setText(trimmed);
                    chip.setTextSize(10);
                    chip.setChipStrokeWidth(1f);
                    chip.setChipStrokeColorResource(R.color.primary);
                    chip.setChipBackgroundColorResource(R.color.tag_bg);
                    chip.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.tag_text));
                    chip.setPadding(4, 2, 4, 2);
                    chip.setEnsureMinTouchTargetSize(false);
                    chip.setTextStartPadding(8);
                    chip.setTextEndPadding(8);
                    chip.setChipMinHeight(24);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 6, 0);
                    chip.setLayoutParams(params);
                    holder.layoutTags.addView(chip);
                    count++;
                }
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHouseClick(house);
        });
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    static class HouseHolder extends RecyclerView.ViewHolder {
        ImageView ivHouse;
        TextView tvCommunity, tvRent, tvInfo;
        LinearLayout layoutTags;

        HouseHolder(@NonNull View itemView) {
            super(itemView);
            ivHouse = itemView.findViewById(R.id.iv_house);
            tvCommunity = itemView.findViewById(R.id.tv_community);
            tvRent = itemView.findViewById(R.id.tv_rent);
            tvInfo = itemView.findViewById(R.id.tv_info);
            layoutTags = itemView.findViewById(R.id.layout_tags);
        }
    }
}
