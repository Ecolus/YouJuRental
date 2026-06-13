package com.example.youjurental.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.youjurental.R;
import com.example.youjurental.adapter.BannerAdapter;
import com.example.youjurental.adapter.HouseListAdapter;
import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;
import com.example.youjurental.ui.HouseDetailActivity;
import com.example.youjurental.util.AiChatHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView tvCity;
    private EditText etSearch;
    private TextView tvAi;
    private ViewPager2 vpBanner;
    private RecyclerView rvHouses;
    private com.google.android.material.chip.Chip chipPrice, chipType, chipRentType, chipDecoration;

    private HouseDBHelper houseDBHelper;
    private List<House> houseList = new ArrayList<>();
    private List<House> bannerHouses = new ArrayList<>();
    private HouseListAdapter houseAdapter;
    private BannerAdapter bannerAdapter;
    private Handler bannerHandler = new Handler();
    private int currentBannerIndex = 0;

    private String selectedCity = "成都";
    private String filterPrice = null;
    private String filterHouseType = null;
    private String filterRentType = null;
    private String filterDecoration = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        houseDBHelper = HouseDBHelper.getInstance(requireContext());

        tvCity = view.findViewById(R.id.tv_city);
        etSearch = view.findViewById(R.id.et_search);
        tvAi = view.findViewById(R.id.tv_ai);
        vpBanner = view.findViewById(R.id.vp_banner);
        rvHouses = view.findViewById(R.id.rv_houses);
        chipPrice = view.findViewById(R.id.chip_price);
        chipType = view.findViewById(R.id.chip_type);
        chipRentType = view.findViewById(R.id.chip_rent_type);
        chipDecoration = view.findViewById(R.id.chip_decoration);

        tvCity.setText(selectedCity + " ▾");
        tvCity.setOnClickListener(v -> showCityDialog());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                List<House> results = houseDBHelper.searchHouses(keyword);
                houseAdapter.updateData(results);
                if (results.isEmpty()) {
                    Toast.makeText(getContext(), "未找到相关房源", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

        tvAi.setOnClickListener(v -> showFilterDialog());

        // AI Chat FAB
        FloatingActionButton fabAi = view.findViewById(R.id.fab_ai);
        fabAi.setOnClickListener(v -> showAiChatDialog());

        chipPrice.setOnClickListener(v -> showSingleChoiceDialog("选择租金区间",
                R.array.price_ranges, (choice) -> {
            filterPrice = choice;
            chipPrice.setText(choice.equals("不限") ? "租金区间" : choice);
            applyFilters();
        }));

        chipType.setOnClickListener(v -> showSingleChoiceDialog("选择户型",
                R.array.house_types, (choice) -> {
            filterHouseType = choice;
            chipType.setText(choice.equals("不限") ? "户型" : choice);
            applyFilters();
        }));

        chipRentType.setOnClickListener(v -> showSingleChoiceDialog("选择租类型",
                R.array.rent_types, (choice) -> {
            filterRentType = choice;
            chipRentType.setText(choice.equals("不限") ? "整租/合租" : choice);
            applyFilters();
        }));

        chipDecoration.setOnClickListener(v -> showSingleChoiceDialog("选择装修",
                R.array.decoration_types, (choice) -> {
            filterDecoration = choice;
            chipDecoration.setText(choice.equals("不限") ? "装修" : choice);
            applyFilters();
        }));

        rvHouses.setLayoutManager(new LinearLayoutManager(getContext()));
        houseAdapter = new HouseListAdapter(houseList, house -> {
            Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
            intent.putExtra("house_id", house.getId());
            startActivity(intent);
        });
        rvHouses.setAdapter(houseAdapter);

        loadData();

        return view;
    }

    private void loadData() {
        bannerHouses = houseDBHelper.getTopHouses(3);
        bannerAdapter = new BannerAdapter(bannerHouses, house -> {
            Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
            intent.putExtra("house_id", house.getId());
            startActivity(intent);
        });
        vpBanner.setAdapter(bannerAdapter);
        startBannerAutoScroll();

        houseList = houseDBHelper.getAllHouses();
        houseAdapter.updateData(houseList);
    }

    private void applyFilters() {
        List<House> filtered = houseDBHelper.filterHouses(
                filterPrice, filterHouseType, filterRentType, filterDecoration, selectedCity, null);
        houseAdapter.updateData(filtered);
        if (filtered.isEmpty()) {
            Toast.makeText(getContext(), "没有符合条件的房源", Toast.LENGTH_SHORT).show();
        }
    }

    private void startBannerAutoScroll() {
        bannerHandler.removeCallbacksAndMessages(null);
        if (bannerHouses.size() <= 1) return;
        bannerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentBannerIndex = (currentBannerIndex + 1) % bannerHouses.size();
                vpBanner.setCurrentItem(currentBannerIndex, true);
                bannerHandler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void showCityDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_city_select);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Spinner spProvince = dialog.findViewById(R.id.sp_province);
        Spinner spCity = dialog.findViewById(R.id.sp_city);
        Spinner spDistrict = dialog.findViewById(R.id.sp_district);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        String[] provinces = {"四川省"};
        String[][] cities = {{"成都", "宜宾", "绵阳", "德阳"}};
        String[][][] districts = {{
                {"锦江区","青羊区","金牛区","武侯区","成华区","高新区","天府新区"},
                {"翠屏区","叙州区","南溪区"},
                {"涪城区","游仙区","安州区"},
                {"旌阳区","罗江区"}
        }};

        spProvince.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, provinces));

        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, cities[pos]);
                spCity.setAdapter(cityAdapter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int provPos = spProvince.getSelectedItemPosition();
                ArrayAdapter<String> distAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, districts[provPos][pos]);
                spDistrict.setAdapter(distAdapter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String cityName = spCity.getSelectedItem().toString();
            selectedCity = cityName;
            tvCity.setText(cityName + " ▾");
            dialog.dismiss();
            applyFilters();
            Toast.makeText(getContext(), "已选择：" + cityName, Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ai_search);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Spinner spPrice = dialog.findViewById(R.id.sp_price);
        Spinner spType = dialog.findViewById(R.id.sp_type);
        Spinner spRentType = dialog.findViewById(R.id.sp_rent_type);
        Spinner spDecoration = dialog.findViewById(R.id.sp_decoration);
        Spinner spDistrict = dialog.findViewById(R.id.sp_district);
        Button btnSearch = dialog.findViewById(R.id.btn_search);

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.price_ranges));
        spPrice.setAdapter(priceAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.house_types));
        spType.setAdapter(typeAdapter);

        ArrayAdapter<String> rentAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.rent_types));
        spRentType.setAdapter(rentAdapter);

        ArrayAdapter<String> decoAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.decoration_types));
        spDecoration.setAdapter(decoAdapter);

        ArrayAdapter<String> distAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.districts));
        spDistrict.setAdapter(distAdapter);

        btnSearch.setOnClickListener(v -> {
            String price = spPrice.getSelectedItem().toString();
            String type = spType.getSelectedItem().toString();
            String rentType = spRentType.getSelectedItem().toString();
            String decoration = spDecoration.getSelectedItem().toString();
            String district = spDistrict.getSelectedItem().toString();

            List<House> results = houseDBHelper.filterHouses(price, type, rentType, decoration, selectedCity, district);
            houseAdapter.updateData(results);
            dialog.dismiss();
            if (results.isEmpty()) {
                Toast.makeText(getContext(), "未找到匹配房源，请放宽条件", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "为您找到" + results.size() + "套房源", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showSingleChoiceDialog(String title, int arrayResId, OnChoiceSelected callback) {
        Dialog dialog = new Dialog(requireContext(), R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(getResources().getColor(R.color.text_primary));
        tvTitle.setPadding(0, 0, 0, 24);
        layout.addView(tvTitle);

        String[] items = getResources().getStringArray(arrayResId);
        RadioGroup radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < items.length; i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(items[i]);
            rb.setTextSize(14);
            rb.setPadding(0, 8, 0, 8);
            radioGroup.addView(rb);
        }
        layout.addView(radioGroup);

        Button btnConfirm = new Button(getContext());
        btnConfirm.setText("确定");
        btnConfirm.setBackgroundResource(R.drawable.bg_button_primary);
        btnConfirm.setTextColor(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 24;
        btnConfirm.setLayoutParams(params);

        btnConfirm.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRb = dialog.findViewById(selectedId);
                String choice = selectedRb.getText().toString();
                callback.onSelected(choice);
            }
            dialog.dismiss();
        });
        layout.addView(btnConfirm);

        dialog.setContentView(layout);
        dialog.show();
    }

    private interface OnChoiceSelected {
        void onSelected(String choice);
    }

    private void showAiChatDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ai_chat);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        RecyclerView rvMessages = dialog.findViewById(R.id.rv_messages);
        EditText etMessage = dialog.findViewById(R.id.et_message);
        View ivSend = dialog.findViewById(R.id.iv_send);
        View tvClose = dialog.findViewById(R.id.tv_close);
        com.google.android.material.chip.Chip chipS1 = dialog.findViewById(R.id.chip_s1);
        com.google.android.material.chip.Chip chipS2 = dialog.findViewById(R.id.chip_s2);
        com.google.android.material.chip.Chip chipS3 = dialog.findViewById(R.id.chip_s3);

        List<ChatMessage> messages = new ArrayList<>();
        // Welcome message
        messages.add(new ChatMessage("AI助手",
                "👋 你好！我是优居租房AI助手。告诉我你的需求，我帮你精准找房！\n\n"
                + "你可以这样说：\n"
                + "• \"我在宜宾翠屏区，预算2000，有什么推荐？\"\n"
                + "• \"成都高新区精装修两室一厅\"\n"
                + "• \"帮我找武侯区近地铁1000以内的合租\"",
                true));

        ChatAdapter chatAdapter = new ChatAdapter(messages);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(chatAdapter);

        // Send message
        View.OnClickListener sendAction = v -> {
            String userMsg = etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(userMsg)) return;

            messages.add(new ChatMessage("我", userMsg, false));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            rvMessages.scrollToPosition(messages.size() - 1);
            etMessage.setText("");

            // Show typing indicator
            messages.add(new ChatMessage("AI助手", "正在为您查找...", true));
            int typingPos = messages.size() - 1;
            chatAdapter.notifyItemInserted(typingPos);
            rvMessages.scrollToPosition(typingPos);

            // Process query (delay to simulate thinking)
            final String query = userMsg;
            rvMessages.postDelayed(() -> {
                String response = AiChatHelper.processQuery(requireContext(), query);
                messages.set(typingPos, new ChatMessage("AI助手", response, true));
                chatAdapter.notifyItemChanged(typingPos);
                rvMessages.scrollToPosition(typingPos);
            }, 800);
        };

        ivSend.setOnClickListener(sendAction);
        // Also send on Enter key
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendAction.onClick(v);
            return true;
        });

        // Quick suggestion chips
        View.OnClickListener quickAction = v -> {
            String text = ((com.google.android.material.chip.Chip) v).getText().toString();
            etMessage.setText(text);
            sendAction.onClick(v);
        };
        chipS1.setOnClickListener(quickAction);
        chipS2.setOnClickListener(quickAction);
        chipS3.setOnClickListener(quickAction);

        tvClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Chat message model
    private static class ChatMessage {
        String sender;
        String content;
        boolean isAi;
        ChatMessage(String sender, String content, boolean isAi) {
            this.sender = sender; this.content = content; this.isAi = isAi;
        }
    }

    // Chat adapter
    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
        List<ChatMessage> messages;
        ChatAdapter(List<ChatMessage> messages) { this.messages = messages; }

        @NonNull @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message, parent, false);
            return new ChatHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            holder.tvSender.setText(msg.sender);
            holder.tvContent.setText(msg.content);

            if (msg.isAi) {
                holder.tvSender.setGravity(android.view.Gravity.START);
                holder.tvContent.setBackgroundResource(R.drawable.bg_card);
                holder.tvContent.setTextColor(0xFF212121);
                ((ViewGroup.MarginLayoutParams) holder.tvContent.getLayoutParams()).leftMargin = 0;
                ((ViewGroup.MarginLayoutParams) holder.tvContent.getLayoutParams()).rightMargin = 60;
            } else {
                holder.tvSender.setGravity(android.view.Gravity.END);
                holder.tvContent.setBackgroundResource(R.drawable.bg_button_primary);
                holder.tvContent.setTextColor(0xFFFFFFFF);
                ((ViewGroup.MarginLayoutParams) holder.tvContent.getLayoutParams()).leftMargin = 60;
                ((ViewGroup.MarginLayoutParams) holder.tvContent.getLayoutParams()).rightMargin = 0;
            }
        }

        @Override public int getItemCount() { return messages.size(); }

        static class ChatHolder extends RecyclerView.ViewHolder {
            TextView tvSender, tvContent;
            ChatHolder(@NonNull View v) {
                super(v);
                tvSender = v.findViewById(R.id.tv_sender);
                tvContent = v.findViewById(R.id.tv_content);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when coming back to this tab (e.g. after publishing a house)
        if (houseDBHelper != null) {
            bannerHouses = houseDBHelper.getTopHouses(3);
            if (bannerAdapter != null) {
                bannerAdapter = new BannerAdapter(bannerHouses, house -> {
                    Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
                    intent.putExtra("house_id", house.getId());
                    startActivity(intent);
                });
                vpBanner.setAdapter(bannerAdapter);
            }
            startBannerAutoScroll();
            applyFilters();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacksAndMessages(null);
    }
}
