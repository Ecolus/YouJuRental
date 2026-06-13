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
import com.example.youjurental.util.PhotoUtil;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
    private List<House> houses;
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(House house);
    }

    public BannerAdapter(List<House> houses, OnBannerClickListener listener) {
        this.houses = houses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        House house = houses.get(position);
        holder.tvTitle.setText(house.getCommunityName() + "  " + house.getMonthlyRent() + "元/月");

        // Load banner photo
        PhotoUtil.loadFirstPhoto(holder.itemView.getContext(), holder.ivBanner, house.getImageUrls());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBannerClick(house);
        });
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    static class BannerHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle;

        BannerHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
        }
    }
}
