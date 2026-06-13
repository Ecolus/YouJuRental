package com.example.youjurental.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youjurental.R;
import com.example.youjurental.adapter.ProfileMenuAdapter;
import com.example.youjurental.ui.ChangePasswordActivity;
import com.example.youjurental.ui.CollectActivity;
import com.example.youjurental.ui.EditProfileActivity;
import com.example.youjurental.ui.LoginActivity;
import com.example.youjurental.ui.MyPublishActivity;
import com.example.youjurental.util.PhotoUtil;
import com.example.youjurental.util.SharedPrefsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private ImageView ivAvatar;
    private TextView tvNickname, tvPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvPhone = view.findViewById(R.id.tv_phone);
        RecyclerView rvMenu = view.findViewById(R.id.rv_menu);

        loadProfileHeader();

        List<ProfileMenuAdapter.MenuItem> items = new ArrayList<>();
        items.add(new ProfileMenuAdapter.MenuItem("修改个人信息", R.drawable.ic_person));
        items.add(new ProfileMenuAdapter.MenuItem("我的收藏", R.drawable.ic_favorite));
        items.add(new ProfileMenuAdapter.MenuItem("我的发布", R.drawable.ic_house));
        items.add(new ProfileMenuAdapter.MenuItem("修改密码", R.drawable.ic_person));
        items.add(new ProfileMenuAdapter.MenuItem("清除缓存", R.drawable.ic_person));
        items.add(new ProfileMenuAdapter.MenuItem("退出登录", R.drawable.ic_person));

        ProfileMenuAdapter adapter = new ProfileMenuAdapter(items, position -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getActivity(), EditProfileActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), CollectActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(getActivity(), MyPublishActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                    break;
                case 4:
                    clearCache();
                    break;
                case 5:
                    showLogoutDialog();
                    break;
            }
        });

        rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMenu.setAdapter(adapter);

        return view;
    }

    private void loadProfileHeader() {
        String account = SharedPrefsUtil.getAccount(requireContext());
        String nickname = SharedPrefsUtil.getNickname(requireContext());

        if (!TextUtils.isEmpty(nickname)) {
            tvNickname.setText(nickname);
        } else {
            tvNickname.setText("用户" + (account.length() > 4 ? account.substring(account.length() - 4) : account));
        }
        tvPhone.setText(account);

        // Load avatar
        SharedPreferences profilePrefs = requireContext().getSharedPreferences("profile_prefs", 0);
        String avatarPath = profilePrefs.getString("avatar_path", "");
        if (!TextUtils.isEmpty(avatarPath) && new File(avatarPath).exists()) {
            ivAvatar.setPadding(0, 0, 0, 0);
            ivAvatar.setBackground(null);
            PhotoUtil.loadPhoto(requireContext(), ivAvatar, avatarPath);
        }
    }

    private void clearCache() {
        try {
            File cacheDir = requireContext().getCacheDir();
            // Keep avatar file
            SharedPreferences profilePrefs = requireContext().getSharedPreferences("profile_prefs", 0);
            String avatarPath = profilePrefs.getString("avatar_path", "");
            for (File f : cacheDir.listFiles()) {
                if (f.isFile() && !f.getAbsolutePath().equals(avatarPath)) {
                    f.delete();
                }
            }
            Toast.makeText(getContext(), "缓存已清除", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "清除缓存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    SharedPrefsUtil.logout(requireContext());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileHeader();
    }
}
