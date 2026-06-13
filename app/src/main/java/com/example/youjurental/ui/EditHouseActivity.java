package com.example.youjurental.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.youjurental.R;
import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditHouseActivity extends AppCompatActivity {
    private TextInputEditText etCommunity, etRent, etAreaSize, etLandlordName, etPhone, etDescription;
    private Spinner spCity, spDistrict, spHouseType, spOrientation;
    private RadioGroup rgRentType, rgDecoration;
    private RadioButton rbWhole, rbShare, rbJing, rbJian, rbMao, rbHao;
    private MaterialButton btnGallery, btnCamera, btnClearPhotos, btnSave;
    private TextView tvCurrentPhotos, tvNewPhotos;
    private LinearLayout layoutTags;

    private HouseDBHelper houseDBHelper;
    private House house;
    private List<String> existingPhotos = new ArrayList<>();
    private List<String> newPhotos = new ArrayList<>();
    private List<CheckBox> tagCheckBoxes = new ArrayList<>();
    private String[][] allDistricts;
    private Uri currentCameraUri;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> requestCameraPermLauncher;
    private ActivityResultLauncher<String> requestStoragePermLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        houseDBHelper = HouseDBHelper.getInstance(this);

        // View bindings
        etCommunity = findViewById(R.id.et_community);
        etRent = findViewById(R.id.et_rent);
        etAreaSize = findViewById(R.id.et_area_size);
        etLandlordName = findViewById(R.id.et_landlord_name);
        etPhone = findViewById(R.id.et_phone);
        etDescription = findViewById(R.id.et_description);
        spCity = findViewById(R.id.sp_city);
        spDistrict = findViewById(R.id.sp_district);
        spHouseType = findViewById(R.id.sp_house_type);
        spOrientation = findViewById(R.id.sp_orientation);
        rgRentType = findViewById(R.id.rg_rent_type);
        rgDecoration = findViewById(R.id.rg_decoration);
        rbWhole = findViewById(R.id.rb_whole);
        rbShare = findViewById(R.id.rb_share);
        rbJing = findViewById(R.id.rb_jing);
        rbJian = findViewById(R.id.rb_jian);
        rbMao = findViewById(R.id.rb_mao);
        rbHao = findViewById(R.id.rb_hao);
        btnGallery = findViewById(R.id.btn_gallery);
        btnCamera = findViewById(R.id.btn_camera);
        btnClearPhotos = findViewById(R.id.btn_clear_photos);
        btnSave = findViewById(R.id.btn_save);
        tvCurrentPhotos = findViewById(R.id.tv_current_photos);
        tvNewPhotos = findViewById(R.id.tv_new_photos);
        layoutTags = findViewById(R.id.layout_tags);

        // Load house
        int houseId = getIntent().getIntExtra("house_id", -1);
        if (houseId == -1) { finish(); return; }
        house = houseDBHelper.getHouseById(houseId);
        if (house == null) { finish(); return; }

        // Parse existing photos
        if (house.getImageUrls() != null && !house.getImageUrls().isEmpty()) {
            String[] parts = house.getImageUrls().split(",");
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) existingPhotos.add(t);
            }
        }

        // Setup spinners
        setupSpinners();
        setupTags();
        setupImagePickers();
        fillForm();
        updatePhotoText();

        // Image buttons
        btnGallery.setOnClickListener(v -> {
            if (hasStoragePerm()) pickImageLauncher.launch("image/*");
            else requestStoragePermLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        });
        btnCamera.setOnClickListener(v -> {
            if (hasCameraPerm()) openCamera();
            else requestCameraPermLauncher.launch(Manifest.permission.CAMERA);
        });
        btnClearPhotos.setOnClickListener(v -> {
            existingPhotos.clear();
            newPhotos.clear();
            updatePhotoText();
            Toast.makeText(this, "照片已清空（保存后生效）", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private boolean hasStoragePerm() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || android.os.Build.VERSION.SDK_INT >= 33;
    }
    private boolean hasCameraPerm() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setupSpinners() {
        String[] cities = {"成都", "宜宾", "绵阳", "德阳"};
        allDistricts = new String[][]{
            {"锦江区","青羊区","金牛区","武侯区","成华区","高新区","天府新区","双流区","郫都区","温江区"},
            {"翠屏区","叙州区","南溪区"},
            {"涪城区","游仙区","安州区"},
            {"旌阳区","罗江区"}
        };
        spCity.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cities));
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                spDistrict.setAdapter(new ArrayAdapter<>(EditHouseActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, allDistricts[pos]));
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        spHouseType.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.house_types)));
        spOrientation.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.orientations)));
    }

    private void setupTags() {
        String[] tags = getResources().getStringArray(R.array.tags_list);
        tagCheckBoxes.clear();
        layoutTags.removeAllViews();
        for (String tag : tags) {
            CheckBox cb = new CheckBox(this);
            cb.setText(tag);
            cb.setTextSize(13);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            cb.setLayoutParams(params);
            layoutTags.addView(cb);
            tagCheckBoxes.add(cb);
        }
    }

    private void fillForm() {
        etCommunity.setText(house.getCommunityName());
        etRent.setText(String.valueOf(house.getMonthlyRent()));
        etAreaSize.setText(String.valueOf((int) house.getBuildingArea()));
        etLandlordName.setText(house.getLandlordName());
        etPhone.setText(house.getLandlordPhone());
        etDescription.setText(house.getDescription());

        // City / District
        String[] cities = {"成都", "宜宾", "绵阳", "德阳"};
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].equals(house.getCity())) { spCity.setSelection(i); break; }
        }
        if (house.getDistrict() != null) {
            for (int i = 0; i < allDistricts.length; i++) {
                for (int j = 0; j < allDistricts[i].length; j++) {
                    if (allDistricts[i][j].equals(house.getDistrict())) {
                        spCity.setSelection(i);
                        spDistrict.setSelection(j);
                        break;
                    }
                }
            }
        }

        // House type
        String[] types = getResources().getStringArray(R.array.house_types);
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(house.getHouseType())) { spHouseType.setSelection(i); break; }
        }
        // Orientation
        String[] orients = getResources().getStringArray(R.array.orientations);
        for (int i = 0; i < orients.length; i++) {
            if (orients[i].equals(house.getOrientation())) { spOrientation.setSelection(i); break; }
        }
        // Rent type
        if ("合租".equals(house.getRentType())) rbShare.setChecked(true); else rbWhole.setChecked(true);
        // Decoration
        if ("简装修".equals(house.getDecoration())) rbJian.setChecked(true);
        else if ("毛坯".equals(house.getDecoration())) rbMao.setChecked(true);
        else if ("豪华装修".equals(house.getDecoration())) rbHao.setChecked(true);
        else rbJing.setChecked(true);
        // Tags
        if (house.getTags() != null) {
            String[] parts = house.getTags().split(",");
            for (String t : parts) {
                for (CheckBox cb : tagCheckBoxes) {
                    if (cb.getText().toString().equals(t.trim())) cb.setChecked(true);
                }
            }
        }
    }

    private void updatePhotoText() {
        tvCurrentPhotos.setText("现有照片：" + existingPhotos.size() + " 张");
        tvNewPhotos.setText(newPhotos.size() > 0 ? "新增：" + newPhotos.size() + " 张" : "");
    }

    private void setupImagePickers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        String path = saveImageToCache(uri);
                        if (path != null) {
                            newPhotos.add(path);
                            updatePhotoText();
                            Toast.makeText(this, "照片添加成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), success -> {
                    if (success && currentCameraUri != null) {
                        String path = currentCameraUri.getPath();
                        if (new File(path).exists()) {
                            newPhotos.add(path);
                        } else {
                            String cached = saveImageToCache(currentCameraUri);
                            if (cached != null) newPhotos.add(cached);
                        }
                        updatePhotoText();
                        Toast.makeText(this, "拍照成功", Toast.LENGTH_SHORT).show();
                    }
                });
        requestCameraPermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), g -> {
                    if (g) openCamera();
                });
        requestStoragePermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), g -> {
                    if (g) pickImageLauncher.launch("image/*");
                });
    }

    private void openCamera() {
        try {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File f = File.createTempFile("JPEG_" + ts + "_", ".jpg", getCacheDir());
            currentCameraUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", f);
            takePhotoLauncher.launch(currentCameraUri);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开相机", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToCache(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return null;
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File f = new File(getCacheDir(), "EDIT_" + ts + "_" + System.nanoTime() + ".jpg");
            FileOutputStream fos = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            fos.close(); is.close();
            return f.getAbsolutePath();
        } catch (Exception e) { return null; }
    }

    private void saveChanges() {
        String community = etCommunity.getText().toString().trim();
        String rentStr = etRent.getText().toString().trim();
        String areaStr = etAreaSize.getText().toString().trim();
        String landlord = etLandlordName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(community) || TextUtils.isEmpty(rentStr)
                || TextUtils.isEmpty(areaStr) || TextUtils.isEmpty(landlord)
                || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return;
        }

        int rent;
        double area;
        try { rent = Integer.parseInt(rentStr); area = Double.parseDouble(areaStr); }
        catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show(); return;
        }

        house.setCommunityName(community);
        house.setCity(spCity.getSelectedItem().toString());
        house.setDistrict(spDistrict.getSelectedItem().toString());
        house.setArea(spDistrict.getSelectedItem().toString());
        house.setMonthlyRent(rent);
        house.setBuildingArea(area);
        house.setHouseType(spHouseType.getSelectedItem().toString());
        house.setOrientation(spOrientation.getSelectedItem().toString());
        house.setRentType(rbShare.isChecked() ? "合租" : "整租");

        String deco = "精装修";
        if (rbJian.isChecked()) deco = "简装修";
        else if (rbMao.isChecked()) deco = "毛坯";
        else if (rbHao.isChecked()) deco = "豪华装修";
        house.setDecoration(deco);

        house.setLandlordName(landlord);
        house.setLandlordPhone(phone);
        house.setDescription(desc);

        // Tags
        StringBuilder tagsSb = new StringBuilder();
        for (CheckBox cb : tagCheckBoxes) {
            if (cb.isChecked()) {
                if (tagsSb.length() > 0) tagsSb.append(",");
                tagsSb.append(cb.getText());
            }
        }
        house.setTags(tagsSb.toString());

        // Photos: merge existing + new
        StringBuilder photosSb = new StringBuilder();
        for (String p : existingPhotos) {
            if (photosSb.length() > 0) photosSb.append(",");
            photosSb.append(p);
        }
        for (String p : newPhotos) {
            if (photosSb.length() > 0) photosSb.append(",");
            photosSb.append(p);
        }
        house.setImageUrls(photosSb.toString());
        house.setBannerUrl("");

        int result = houseDBHelper.updateHouse(house);
        if (result > 0) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
