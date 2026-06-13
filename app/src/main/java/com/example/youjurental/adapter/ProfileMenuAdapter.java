package com.example.youjurental.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;

import java.util.List;

public class ProfileMenuAdapter extends RecyclerView.Adapter<ProfileMenuAdapter.MenuHolder> {
    private List<MenuItem> items;
    private OnMenuClickListener listener;

    public static class MenuItem {
        public String title;
        public int iconRes;
        public MenuItem(String title, int iconRes) { this.title = title; this.iconRes = iconRes; }
    }

    public interface OnMenuClickListener {
        void onMenuClick(int position);
    }

    public ProfileMenuAdapter(List<MenuItem> items, OnMenuClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_menu, parent, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.tvTitle.setText(item.title);
        holder.ivIcon.setImageResource(item.iconRes);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMenuClick(position);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class MenuHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        MenuHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
