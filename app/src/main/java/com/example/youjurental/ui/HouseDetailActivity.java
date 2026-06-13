package com.example.youjurental.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.youjurental.R;
import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;
import com.example.youjurental.util.PhotoUtil;
import com.example.youjurental.util.SharedPrefsUtil;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HouseDetailActivity extends AppCompatActivity {
    private ViewPager2 vpImages;
    private TextView tvCommunity, tvRent, tvDetail, tvDescription, tvLandlord, tvPhone;
    private MaterialButton btnCollect, btnCall, btnAppointment;

    private HouseDBHelper houseDBHelper;
    private House house;
    private boolean isCollected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);

        houseDBHelper = HouseDBHelper.getInstance(this);

        vpImages = findViewById(R.id.vp_images);
        tvCommunity = findViewById(R.id.tv_community);
        tvRent = findViewById(R.id.tv_rent);
        tvDetail = findViewById(R.id.tv_detail);
        tvDescription = findViewById(R.id.tv_description);
        tvLandlord = findViewById(R.id.tv_landlord);
        tvPhone = findViewById(R.id.tv_phone);
        btnCollect = findViewById(R.id.btn_collect);
        btnCall = findViewById(R.id.btn_call);
        btnAppointment = findViewById(R.id.btn_appointment);

        int houseId = getIntent().getIntExtra("house_id", -1);
        if (houseId == -1) {
            Toast.makeText(this, "房源不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        house = houseDBHelper.getHouseById(houseId);
        if (house == null) {
            Toast.makeText(this, "房源不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayHouse();
        updateCollectButton();

        btnCollect.setOnClickListener(v -> {
            String userId = SharedPrefsUtil.getAccount(this);
            if (isCollected) {
                houseDBHelper.removeCollect(userId, house.getId());
                isCollected = false;
                btnCollect.setText("收藏");
                Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
            } else {
                houseDBHelper.addCollect(userId, house.getId());
                isCollected = true;
                btnCollect.setText("已收藏");
                Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
            }
        });

        btnCall.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + house.getLandlordPhone()));
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE}, 100);
            }
        });

        btnAppointment.setOnClickListener(v ->
                Toast.makeText(this, "已预约看房，房东将尽快联系您", Toast.LENGTH_SHORT).show());
    }

    private void displayHouse() {
        tvCommunity.setText(house.getCommunityName());
        tvRent.setText(house.getMonthlyRent() + " 元/月");

        String detail = "户型：" + house.getHouseType() + "\n"
                + "面积：" + (int) house.getBuildingArea() + " ㎡\n"
                + "朝向：" + house.getOrientation() + "\n"
                + "租类型：" + house.getRentType() + "\n"
                + "装修：" + house.getDecoration() + "\n"
                + "位置：" + house.getCity() + " " + house.getDistrict() + " " + house.getArea() + "\n"
                + "标签：" + (house.getTags() != null ? house.getTags() : "无");
        tvDetail.setText(detail);

        tvDescription.setText(house.getDescription() != null ? house.getDescription() : "暂无描述");
        tvLandlord.setText("房东：" + house.getLandlordName());
        tvPhone.setText(house.getLandlordPhone());

        // Photo carousel with real images - click to fullscreen
        final List<String> photoUrls = PhotoUtil.getPhotoUrlList(house.getImageUrls());
        final int totalPhotos = photoUrls.size();
        vpImages.setAdapter(new androidx.recyclerview.widget.RecyclerView.Adapter<PhotoHolder>() {
            @NonNull
            @Override
            public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ImageView iv = new ImageView(HouseDetailActivity.this);
                iv.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setClickable(true);
                iv.setFocusable(true);
                return new PhotoHolder(iv);
            }
            @Override
            public void onBindViewHolder(@NonNull PhotoHolder holder, int pos) {
                PhotoUtil.loadPhoto(HouseDetailActivity.this, holder.iv, photoUrls.get(pos));
                final int index = pos;
                holder.iv.setOnClickListener(v -> showFullScreenPhoto(photoUrls, index));
            }
            @Override
            public int getItemCount() { return totalPhotos; }
        });
    }

    private void showFullScreenPhoto(List<String> photoUrls, int startIndex) {
        final int[] currentIndex = {startIndex};

        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_photo);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView ivFull = dialog.findViewById(R.id.iv_fullscreen);
        TextView tvCounter = dialog.findViewById(R.id.tv_counter);
        View btnClose = dialog.findViewById(R.id.btn_close);
        View btnPrev = dialog.findViewById(R.id.btn_prev);
        View btnNext = dialog.findViewById(R.id.btn_next);

        Runnable updateImage = () -> {
            PhotoUtil.loadPhoto(this, ivFull, photoUrls.get(currentIndex[0]));
            tvCounter.setText((currentIndex[0] + 1) + "/" + photoUrls.size());
            btnPrev.setVisibility(currentIndex[0] > 0 ? View.VISIBLE : View.INVISIBLE);
            btnNext.setVisibility(currentIndex[0] < photoUrls.size() - 1 ? View.VISIBLE : View.INVISIBLE);
        };
        updateImage.run();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        ivFull.setOnClickListener(v -> dialog.dismiss());
        btnPrev.setOnClickListener(v -> {
            if (currentIndex[0] > 0) { currentIndex[0]--; updateImage.run(); }
        });
        btnNext.setOnClickListener(v -> {
            if (currentIndex[0] < photoUrls.size() - 1) { currentIndex[0]++; updateImage.run(); }
        });

        dialog.show();
    }

    private void updateCollectButton() {
        String userId = SharedPrefsUtil.getAccount(this);
        isCollected = houseDBHelper.isCollected(userId, house.getId());
        btnCollect.setText(isCollected ? "已收藏" : "收藏");
    }

    private static class PhotoHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView iv;
        PhotoHolder(@NonNull ImageView itemView) {
            super(itemView);
            iv = itemView;
        }
    }
}
