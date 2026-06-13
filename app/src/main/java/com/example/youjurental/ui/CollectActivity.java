package com.example.youjurental.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;
import com.example.youjurental.adapter.CollectAdapter;
import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;
import com.example.youjurental.util.SharedPrefsUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class CollectActivity extends AppCompatActivity {
    private RecyclerView rvCollect;
    private HouseDBHelper houseDBHelper;
    private CollectAdapter adapter;
    private List<House> collectedHouses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvCollect = findViewById(R.id.rv_collect);
        houseDBHelper = HouseDBHelper.getInstance(this);

        String userId = SharedPrefsUtil.getAccount(this);
        collectedHouses = houseDBHelper.getCollectedHouses(userId);

        adapter = new CollectAdapter(collectedHouses, new CollectAdapter.OnCollectActionListener() {
            @Override
            public void onItemClick(House house) {
                Intent intent = new Intent(CollectActivity.this, HouseDetailActivity.class);
                intent.putExtra("house_id", house.getId());
                startActivity(intent);
            }

            @Override
            public void onRemoveCollect(House house) {
                houseDBHelper.removeCollect(userId, house.getId());
                adapter.removeItem(house);
                Toast.makeText(CollectActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
            }
        });

        rvCollect.setLayoutManager(new LinearLayoutManager(this));
        rvCollect.setAdapter(adapter);

        if (collectedHouses.isEmpty()) {
            Toast.makeText(this, "暂无收藏", Toast.LENGTH_SHORT).show();
        }
    }
}
