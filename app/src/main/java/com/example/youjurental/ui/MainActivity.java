package com.example.youjurental.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.youjurental.R;
import com.example.youjurental.adapter.MainPagerAdapter;
import com.example.youjurental.ui.fragment.HomeFragment;
import com.example.youjurental.ui.fragment.HostFragment;
import com.example.youjurental.ui.fragment.ProfileFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new HostFragment());
        fragments.add(new ProfileFragment());

        MainPagerAdapter adapter = new MainPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("我要租房");
                    break;
                case 1:
                    tab.setText("我要当房东");
                    break;
                case 2:
                    tab.setText("我的");
                    break;
            }
        }).attach();
    }
}
