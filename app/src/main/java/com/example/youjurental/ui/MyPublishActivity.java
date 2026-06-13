package com.example.youjurental.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;
import com.example.youjurental.adapter.MyPublishAdapter;
import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;
import com.example.youjurental.util.SharedPrefsUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MyPublishActivity extends AppCompatActivity {
    private RecyclerView rvPublish;
    private HouseDBHelper houseDBHelper;
    private MyPublishAdapter adapter;
    private List<House> publishList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvPublish = findViewById(R.id.rv_publish);
        houseDBHelper = HouseDBHelper.getInstance(this);

        loadData();

        if (publishList.isEmpty()) {
            Toast.makeText(this, "暂无发布", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        String phone = SharedPrefsUtil.getPhone(this);
        publishList = houseDBHelper.getHousesByLandlordPhone(phone);

        adapter = new MyPublishAdapter(publishList, new MyPublishAdapter.OnPublishActionListener() {
            @Override
            public void onEdit(House house) {
                Intent intent = new Intent(MyPublishActivity.this, EditHouseActivity.class);
                intent.putExtra("house_id", house.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(House house) {
                new AlertDialog.Builder(MyPublishActivity.this)
                        .setTitle("下架房源")
                        .setMessage("确定要下架「" + house.getCommunityName() + "」吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            houseDBHelper.deleteHouse(house.getId());
                            adapter.removeItem(house);
                            Toast.makeText(MyPublishActivity.this, "已下架", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        rvPublish.setLayoutManager(new LinearLayoutManager(this));
        rvPublish.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
