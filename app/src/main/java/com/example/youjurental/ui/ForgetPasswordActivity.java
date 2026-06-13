package com.example.youjurental.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youjurental.R;
import com.example.youjurental.db.UserDBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgetPasswordActivity extends AppCompatActivity {
    private TextInputEditText etAccount;
    private MaterialButton btnReset, btnBack;
    private UserDBHelper userDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        userDBHelper = UserDBHelper.getInstance(this);
        etAccount = findViewById(R.id.et_account);
        btnReset = findViewById(R.id.btn_reset);
        btnBack = findViewById(R.id.btn_back);

        btnReset.setOnClickListener(v -> {
            String account = etAccount.getText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!userDBHelper.isAccountExists(account)) {
                Toast.makeText(this, "账号不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userDBHelper.resetPassword(account)) {
                Toast.makeText(this, "密码已重置为123456，请登录", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "重置失败", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
