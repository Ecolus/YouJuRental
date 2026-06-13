package com.example.youjurental.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youjurental.R;
import com.example.youjurental.util.SharedPrefsUtil;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.iv_logo);

        // 缩放动画：0.5 → 1.0
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.3f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.3f, 1.0f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (SharedPrefsUtil.isLoggedIn(this)) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
