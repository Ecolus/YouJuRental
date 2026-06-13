package com.example.youjurental.util;

import android.content.Context;

import com.example.youjurental.db.HouseDBHelper;
import com.example.youjurental.entity.House;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiChatHelper {

    public static String processQuery(Context context, String query) {
        // ==== 1. Extract search criteria ====
        String city = null;
        String district = null;
        Integer minRent = null;
        Integer maxRent = null;
        String houseType = null;
        String rentType = null;
        String decoration = null;
        int limit = 5;

        // -- City --
        String[] cities = {"成都", "宜宾", "绵阳", "德阳"};
        for (String c : cities) {
            if (query.contains(c)) { city = c; break; }
        }

        // -- District --
        String[] districts = {"锦江区","青羊区","金牛区","武侯区","成华区","高新区","天府新区",
                "双流区","郫都区","温江区","翠屏区","叙州区","南溪区",
                "涪城区","游仙区","安州区","旌阳区","罗江区"};
        for (String d : districts) {
            if (query.contains(d)) { district = d; break; }
        }

        // -- Budget: 弹性匹配 --
        // 模式1: "预算XXX元" / "预算XXX" (含中间可能有"只有/大概/大约"等词)
        Pattern budgetWithWords = Pattern.compile("预算[^\\d]{0,4}(\\d+)");
        Matcher bwm = budgetWithWords.matcher(query);
        if (bwm.find()) {
            int val = Integer.parseInt(bwm.group(1));
            maxRent = val;
            minRent = 0;
        }

        // 模式2: "N-M元" / "N到M" / "N~M" 范围
        if (minRent == null && maxRent == null) {
            Pattern range = Pattern.compile("(\\d+)\\s*[-~到至]\\s*(\\d+)\\s*[元块]?");
            Matcher rm = range.matcher(query);
            if (rm.find()) {
                minRent = Integer.parseInt(rm.group(1));
                maxRent = Integer.parseInt(rm.group(2));
            }
        }

        // 模式3: "不超过N" / "N以内" / "控制.*N" / "N以下"
        if (minRent == null && maxRent == null) {
            Pattern under = Pattern.compile("(?:不超过|控制在|限制在)\\s*(\\d+)|(\\d+)\\s*(?:以内|以下|之内)");
            Matcher um = under.matcher(query);
            if (um.find()) {
                String val = um.group(1) != null ? um.group(1) : um.group(2);
                maxRent = Integer.parseInt(val);
                minRent = 0;
            }
        }

        // 模式4: "N以上" / "至少N"
        if (minRent == null && maxRent == null) {
            Pattern above = Pattern.compile("(\\d+)\\s*(?:以上|往上)|至少\\s*(\\d+)");
            Matcher am = above.matcher(query);
            if (am.find()) {
                String val = am.group(1) != null ? am.group(1) : am.group(2);
                minRent = Integer.parseInt(val);
                maxRent = Integer.MAX_VALUE;
            }
        }

        // 模式5: 孤立的金额 "N元" / "N块" / "N块钱" (合理范围300-20000)
        if (minRent == null && maxRent == null) {
            Pattern price = Pattern.compile("(\\d+)\\s*[元块]");
            Matcher pm = price.matcher(query);
            if (pm.find()) {
                int p = Integer.parseInt(pm.group(1));
                if (p >= 300 && p <= 20000) {
                    maxRent = p;
                    minRent = 0;
                }
            }
        }

        // -- House type --
        String[] types = {"1室0厅","1室1厅","2室1厅","2室2厅","3室1厅","3室2厅","4室2厅"};
        for (String t : types) {
            if (query.contains(t)) { houseType = t; break; }
        }
        if (houseType == null) {
            if (query.contains("一居") || query.contains("一室") || query.contains("单间")) houseType = "1室1厅";
            else if (query.contains("两居") || query.contains("二居") || query.contains("2居") || query.contains("两室") || query.contains("二室")) houseType = "2室1厅";
            else if (query.contains("三居") || query.contains("3居") || query.contains("三室")) houseType = "3室1厅";
            else if (query.contains("四居") || query.contains("4居") || query.contains("四室")) houseType = "4室2厅";
        }

        // -- Rent type --
        if (query.contains("合租")) rentType = "合租";
        else if (query.contains("整租")) rentType = "整租";

        // -- Decoration --
        if (query.contains("豪华")) decoration = "豪华装修";
        else if (query.contains("精装")) decoration = "精装修";
        else if (query.contains("简装")) decoration = "简装修";
        else if (query.contains("毛坯")) decoration = "毛坯";

        // ====== 2. Query DB ======
        HouseDBHelper db = HouseDBHelper.getInstance(context);
        String priceRange = null;
        if (minRent != null && maxRent != null) {
            if (maxRent == Integer.MAX_VALUE) priceRange = minRent + "元以上";
            else if (minRent == 0) priceRange = maxRent + "元以下";
            else priceRange = minRent + "-" + maxRent;
        }

        List<House> results = db.filterHouses(
                priceRange,
                houseType != null && !houseType.isEmpty() ? houseType : "不限",
                rentType != null && !rentType.isEmpty() ? rentType : "不限",
                decoration != null && !decoration.isEmpty() ? decoration : "不限",
                city != null && !city.isEmpty() ? city : "不限",
                district != null && !district.isEmpty() ? district : "不限"
        );

        // ====== 3. Format human-like response ======
        return buildResponse(query, results, city, district, minRent, maxRent, houseType, decoration);
    }

    private static String buildResponse(String query, List<House> results,
                                         String city, String district,
                                         Integer minRent, Integer maxRent,
                                         String houseType, String decoration) {
        // If too many, keep top 5
        List<House> top = results;
        if (top.size() > 5) top = results.subList(0, 5);

        StringBuilder sb = new StringBuilder();

        // ---- Response header ----
        String location = "";
        if (district != null) location = city != null ? city + district : district;
        else if (city != null) location = city;
        if (location.isEmpty()) location = "本地";

        sb.append(headerPhrase(top.size()));

        if (top.isEmpty()) {
            sb.append("😣 很遗憾，在 **").append(location).append("** 没有找到完全匹配的房源。\n\n");
            sb.append("💡 要不试试：\n");
            sb.append("① 把预算放宽一点？\n");
            sb.append("② 换个区域看看？\n");
            sb.append("③ 或者告诉我你的其他需求，我重新帮你找～");
            return sb.toString();
        }

        sb.append("在 **").append(location).append("** ");
        if (maxRent != null && maxRent != Integer.MAX_VALUE) {
            sb.append("预算 **").append(maxRent).append("元以内** ");
        }
        sb.append("帮你找到了 **").append(top.size()).append("** 套不错");
        if (houseType != null) sb.append("的").append(houseType);
        sb.append("的房子：\n\n");

        // ---- House list ----
        for (int i = 0; i < top.size(); i++) {
            House h = top.get(i);
            String emoji = rentEmoji(h.getMonthlyRent());
            sb.append(emoji).append(" **").append(h.getCommunityName()).append("**\n");
            sb.append("   💰 月租 **").append(h.getMonthlyRent()).append("元**");
            sb.append("  |  🏠 ").append(h.getHouseType());
            sb.append("  |  📐 ").append((int) h.getBuildingArea()).append("㎡\n");
            sb.append("   📍 ").append(h.getCity()).append(" ").append(h.getDistrict()).append(" ").append(h.getArea()).append("\n");
            if (h.getTags() != null && !h.getTags().isEmpty()) {
                sb.append("   🏷 ").append(h.getTags()).append("\n");
            }
            sb.append("   📞 房东：").append(h.getLandlordName()).append(" ").append(h.getLandlordPhone()).append("\n\n");
        }

        // ---- Price summary ----
        int minPrice = Integer.MAX_VALUE, maxPrice = 0;
        for (House h : top) {
            int r = h.getMonthlyRent();
            if (r < minPrice) minPrice = r;
            if (r > maxPrice) maxPrice = r;
        }
        sb.append("📊 价格区间：**").append(minPrice).append("～").append(maxPrice).append("元/月**\n");

        // ---- Human-like suggestion ----
        sb.append(suggestionPhrase(top.size(), maxRent, minPrice, maxPrice));

        return sb.toString();
    }

    private static String headerPhrase(int count) {
        if (count == 0) {
            String[] phrases = {
                "让我看看…… ", "嗯，帮你找找…… ", "稍等，我查一下～ ", "正在翻房源列表…… "
            };
            return phrases[(int)(System.currentTimeMillis() % phrases.length)];
        } else if (count <= 2) {
            String[] phrases = {
                "找到啦！虽然选择不多，", "帮你看了，", "好的，",
            };
            return phrases[(int)(System.currentTimeMillis() % phrases.length)];
        } else {
            String[] phrases = {
                "找到啦！", "帮你筛选了一下，", "好消息！", "来看看这几套～",
            };
            return phrases[(int)(System.currentTimeMillis() % phrases.length)];
        }
    }

    private static String rentEmoji(int rent) {
        if (rent < 1000) return "🟢";
        if (rent < 2000) return "🔵";
        if (rent < 3000) return "🟡";
        if (rent < 5000) return "🟠";
        return "🔴";
    }

    private static String suggestionPhrase(int count, Integer maxRent, int minPrice, int maxPrice) {
        StringBuilder sb = new StringBuilder();
        if (maxRent != null && maxRent != Integer.MAX_VALUE && maxPrice > maxRent) {
            sb.append("\n⚠️ 有几套略超你的预算，要不要看看？住得好也很重要呢～");
        }
        if (minPrice > 2000) {
            sb.append("\n💡 如果觉得价格偏高，试试把区域放宽到郫都或温江，那边性价比更高哦！");
        }
        if (count < 3) {
            sb.append("\n🔍 房源不多，要不我帮你扩大范围到全市再找找？");
        }
        if (sb.length() == 0) {
            String[] tips = {
                "\n✨ 有中意的吗？点卡片就能看详细照片哦～",
                "\n🏃 好房不等人，看到合适的赶紧联系房东吧！",
                "\n📱 有任何其他需求随时问我，24小时在线！",
            };
            sb.append(tips[(int)(System.currentTimeMillis() % tips.length)]);
        }
        return sb.toString();
    }
}
