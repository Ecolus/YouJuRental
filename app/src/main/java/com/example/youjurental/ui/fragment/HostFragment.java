package com.example.youjurental.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.youjurental.R;
import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;
import com.example.youjurental.util.SharedPrefsUtil;
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

public class HostFragment extends Fragment {
    private TextInputEditText etCommunity, etRent, etAreaSize, etLandlordName, etPhone, etDescription;
    private Spinner spCity, spDistrict, spHouseType, spOrientation;
    private RadioGroup rgRentType, rgDecoration;
    private MaterialButton btnGallery, btnCamera, btnAiPrice, btnPublish;
    private TextView tvImageCount;
    private LinearLayout layoutTags;

    private HouseDBHelper houseDBHelper;
    private List<String> imagePaths = new ArrayList<>();
    private List<CheckBox> tagCheckBoxes = new ArrayList<>();
    private Uri currentCameraUri;

    // City-district mapping
    private String[][] allDistricts;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> requestCameraPermLauncher;
    private ActivityResultLauncher<String> requestStoragePermLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host, container, false);

        houseDBHelper = HouseDBHelper.getInstance(requireContext());

        etCommunity = view.findViewById(R.id.et_community);
        etRent = view.findViewById(R.id.et_rent);
        etAreaSize = view.findViewById(R.id.et_area_size);
        etLandlordName = view.findViewById(R.id.et_landlord_name);
        etPhone = view.findViewById(R.id.et_phone);
        etDescription = view.findViewById(R.id.et_description);
        spCity = view.findViewById(R.id.sp_city);
        spDistrict = view.findViewById(R.id.sp_district);
        spHouseType = view.findViewById(R.id.sp_house_type);
        spOrientation = view.findViewById(R.id.sp_orientation);
        rgRentType = view.findViewById(R.id.rg_rent_type);
        rgDecoration = view.findViewById(R.id.rg_decoration);
        btnGallery = view.findViewById(R.id.btn_gallery);
        btnCamera = view.findViewById(R.id.btn_camera);
        btnAiPrice = view.findViewById(R.id.btn_ai_price);
        btnPublish = view.findViewById(R.id.btn_publish);
        tvImageCount = view.findViewById(R.id.tv_image_count);
        layoutTags = view.findViewById(R.id.layout_tags);

        // City spinner
        String[] cities = {"成都", "宜宾", "绵阳", "德阳"};
        allDistricts = new String[][]{
            {"锦江区","青羊区","金牛区","武侯区","成华区","高新区","天府新区","双流区","郫都区","温江区"},
            {"翠屏区","叙州区","南溪区"},
            {"涪城区","游仙区","安州区"},
            {"旌阳区","罗江区"}
        };
        spCity.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, cities));
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                ArrayAdapter<String> distAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, allDistricts[pos]);
                spDistrict.setAdapter(distAdapter);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        // House type spinner
        spHouseType.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.house_types)));

        // Orientation spinner
        spOrientation.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.orientations)));

        String savedPhone = SharedPrefsUtil.getPhone(requireContext());
        if (!TextUtils.isEmpty(savedPhone)) etPhone.setText(savedPhone);

        setupTagCheckBoxes();
        setupImagePickers();

        btnGallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED || android.os.Build.VERSION.SDK_INT >= 33) {
                pickImageLauncher.launch("image/*");
            } else {
                requestStoragePermLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestCameraPermLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnAiPrice.setOnClickListener(v -> estimatePrice());
        btnPublish.setOnClickListener(v -> publishHouse());

        return view;
    }

    private void setupImagePickers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        String path = saveImageToCache(uri);
                        if (path != null) {
                            imagePaths.add(path);
                            tvImageCount.setText("已选 " + imagePaths.size() + " 张");
                            Toast.makeText(getContext(), "图片添加成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), success -> {
                    if (success && currentCameraUri != null) {
                        String path = currentCameraUri.getPath();
                        File file = new File(path);
                        if (file.exists()) {
                            imagePaths.add(path);
                        } else {
                            String cached = saveImageToCache(currentCameraUri);
                            if (cached != null) imagePaths.add(cached);
                        }
                        tvImageCount.setText("已选 " + imagePaths.size() + " 张");
                        Toast.makeText(getContext(), "拍照成功", Toast.LENGTH_SHORT).show();
                    }
                });

        requestCameraPermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) openCamera();
                    else Toast.makeText(getContext(), "需要相机权限", Toast.LENGTH_SHORT).show();
                });

        requestStoragePermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) pickImageLauncher.launch("image/*");
                    else Toast.makeText(getContext(), "需要存储权限", Toast.LENGTH_SHORT).show();
                });
    }

    private void openCamera() {
        try {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File photoFile = File.createTempFile("JPEG_" + ts + "_", ".jpg", requireContext().getCacheDir());
            currentCameraUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", photoFile);
            takePhotoLauncher.launch(currentCameraUri);
        } catch (Exception e) {
            Toast.makeText(getContext(), "无法打开相机", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToCache(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) return null;
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(requireContext().getCacheDir(), "IMG_" + ts + "_" + System.nanoTime() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            fos.close();
            is.close();
            return file.getAbsolutePath();
        } catch (Exception e) { return null; }
    }

    private void setupTagCheckBoxes() {
        String[] tags = getResources().getStringArray(R.array.tags_list);
        tagCheckBoxes.clear();
        layoutTags.removeAllViews();
        for (String tag : tags) {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(tag);
            cb.setTextSize(13);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            cb.setLayoutParams(params);
            layoutTags.addView(cb);
            tagCheckBoxes.add(cb);
        }
    }

    private void estimatePrice() {
        String city = spCity.getSelectedItem().toString();
        String houseType = spHouseType.getSelectedItem().toString();
        String areaStr = etAreaSize.getText().toString().trim();

        if (TextUtils.isEmpty(areaStr)) {
            Toast.makeText(getContext(), "请先输入建筑面积", Toast.LENGTH_SHORT).show();
            return;
        }

        double basePrice;
        switch (city) {
            case "成都": basePrice = 45; break;
            case "宜宾": basePrice = 28; break;
            default: basePrice = 30; break;
        }

        double area = Double.parseDouble(areaStr);
        double coefficient = 1.0;
        if (houseType.contains("1室")) coefficient = 0.9;
        else if (houseType.contains("3室")) coefficient = 1.1;

        int estimatedRent = (int)(basePrice * area * coefficient);
        etRent.setText(String.valueOf(estimatedRent));
        Toast.makeText(getContext(), "AI估价：" + estimatedRent + " 元/月 (基准" + basePrice + "元/㎡)", Toast.LENGTH_SHORT).show();
    }

    private void publishHouse() {
        String community = etCommunity.getText().toString().trim();
        String city = spCity.getSelectedItem().toString();
        String district = spDistrict.getSelectedItem().toString();
        String rentStr = etRent.getText().toString().trim();
        String areaSizeStr = etAreaSize.getText().toString().trim();
        String houseType = spHouseType.getSelectedItem().toString();
        String orientation = spOrientation.getSelectedItem().toString();
        String landlordName = etLandlordName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(community) || TextUtils.isEmpty(rentStr)
                || TextUtils.isEmpty(areaSizeStr) || TextUtils.isEmpty(houseType)
                || TextUtils.isEmpty(orientation) || TextUtils.isEmpty(landlordName)
                || TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return;
        }

        int monthlyRent;
        double buildingArea;
        try {
            monthlyRent = Integer.parseInt(rentStr);
            buildingArea = Double.parseDouble(areaSizeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "月租和建筑面积请输入有效数字", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = rgRentType.getCheckedRadioButtonId();
        String rentType = checkedId == R.id.rb_share ? "合租" : "整租";

        String decoration = "精装修";
        int decoId = rgDecoration.getCheckedRadioButtonId();
        if (decoId == R.id.rb_jian) decoration = "简装修";
        else if (decoId == R.id.rb_mao) decoration = "毛坯";
        else if (decoId == R.id.rb_hao) decoration = "豪华装修";

        StringBuilder tagsBuilder = new StringBuilder();
        for (CheckBox cb : tagCheckBoxes) {
            if (cb.isChecked()) {
                if (tagsBuilder.length() > 0) tagsBuilder.append(",");
                tagsBuilder.append(cb.getText());
            }
        }

        StringBuilder imagesBuilder = new StringBuilder();
        for (String path : imagePaths) {
            if (imagesBuilder.length() > 0) imagesBuilder.append(",");
            imagesBuilder.append(path);
        }

        House house = new House();
        house.setCommunityName(community);
        house.setCity(city);
        house.setDistrict(district);
        house.setArea(district);
        house.setMonthlyRent(monthlyRent);
        house.setHouseType(houseType);
        house.setBuildingArea(buildingArea);
        house.setOrientation(orientation);
        house.setRentType(rentType);
        house.setDecoration(decoration);
        house.setLandlordName(landlordName);
        house.setLandlordPhone(phone);
        house.setDescription(description);
        house.setTags(tagsBuilder.toString());
        house.setImageUrls(imagesBuilder.toString());
        house.setBannerUrl("");

        long result = houseDBHelper.insertHouse(house);
        if (result != -1) {
            Toast.makeText(getContext(), "发布成功！", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(getContext(), "发布失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etCommunity.setText("");
        etRent.setText("");
        etAreaSize.setText("");
        etLandlordName.setText("");
        etDescription.setText("");
        spCity.setSelection(0);
        spHouseType.setSelection(0);
        spOrientation.setSelection(0);
        for (CheckBox cb : tagCheckBoxes) cb.setChecked(false);
        imagePaths.clear();
        tvImageCount.setText("已选 0 张");
    }
}
