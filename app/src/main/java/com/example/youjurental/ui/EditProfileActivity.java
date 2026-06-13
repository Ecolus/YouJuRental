package com.example.youjurental.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youjurental.R;
import com.example.youjurental.util.PhotoUtil;
import com.example.youjurental.util.SharedPrefsUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {
    private static final String PROFILE_PREFS = "profile_prefs";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_AVATAR = "avatar_path";

    private ImageView ivAvatar;
    private TextInputEditText etNickname, etAge, etEmail, etSignature;
    private RadioButton rbMale, rbFemale;
    private MaterialButton btnSave;
    private String avatarPath;

    private ActivityResultLauncher<String> pickAvatarLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.iv_avatar);
        TextView tvChangeAvatar = findViewById(R.id.tv_change_avatar);
        etNickname = findViewById(R.id.et_nickname);
        etAge = findViewById(R.id.et_age);
        etEmail = findViewById(R.id.et_email);
        etSignature = findViewById(R.id.et_signature);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        btnSave = findViewById(R.id.btn_save);

        pickAvatarLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        String path = saveAvatarToCache(uri);
                        if (path != null) {
                            avatarPath = path;
                            PhotoUtil.loadPhoto(this, ivAvatar, avatarPath);
                            Toast.makeText(this, "头像已更换", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        tvChangeAvatar.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));
        ivAvatar.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));

        loadProfile();
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences(PROFILE_PREFS, MODE_PRIVATE);
        String nickname = prefs.getString(KEY_NICKNAME, "");
        String gender = prefs.getString(KEY_GENDER, "男");
        String age = prefs.getString(KEY_AGE, "");
        String email = prefs.getString(KEY_EMAIL, "");
        String signature = prefs.getString(KEY_SIGNATURE, "");
        avatarPath = prefs.getString(KEY_AVATAR, "");

        if (!TextUtils.isEmpty(nickname)) etNickname.setText(nickname);
        if (!TextUtils.isEmpty(age)) etAge.setText(age);
        if (!TextUtils.isEmpty(email)) etEmail.setText(email);
        if (!TextUtils.isEmpty(signature)) etSignature.setText(signature);

        if ("女".equals(gender)) rbFemale.setChecked(true); else rbMale.setChecked(true);

        if (!TextUtils.isEmpty(avatarPath) && new File(avatarPath).exists()) {
            PhotoUtil.loadPhoto(this, ivAvatar, avatarPath);
        }
    }

    private void saveProfile() {
        String nickname = etNickname.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String signature = etSignature.getText().toString().trim();
        String gender = rbFemale.isChecked() ? "女" : "男";

        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PROFILE_PREFS, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_NICKNAME, nickname)
                .putString(KEY_GENDER, gender)
                .putString(KEY_AGE, age)
                .putString(KEY_EMAIL, email)
                .putString(KEY_SIGNATURE, signature)
                .putString(KEY_AVATAR, avatarPath != null ? avatarPath : "")
                .apply();

        // Save nickname to main prefs for ProfileFragment display
        SharedPrefsUtil.saveNickname(this, nickname);

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String saveAvatarToCache(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return null;
            File f = new File(getCacheDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            fos.close(); is.close();
            return f.getAbsolutePath();
        } catch (Exception e) { return null; }
    }
}
