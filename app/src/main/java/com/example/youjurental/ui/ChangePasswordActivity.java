package com.example.youjurental.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youjurental.R;
import com.example.youjurental.db.UserDBHelper;
import com.example.youjurental.util.SharedPrefsUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnConfirm;
    private UserDBHelper userDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userDBHelper = UserDBHelper.getInstance(this);

        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnConfirm = findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(v -> {
            String oldPwd = etOldPassword.getText().toString().trim();
            String newPwd = etNewPassword.getText().toString().trim();
            String confirmPwd = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)) {
                Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "两次新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPwd.length() < 6) {
                Toast.makeText(this, "新密码至少6位", Toast.LENGTH_SHORT).show();
                return;
            }

            String account = SharedPrefsUtil.getAccount(this);
            if (userDBHelper.changePassword(account, oldPwd, newPwd)) {
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "旧密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
