package com.example.youjurental.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Random;

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "youjurental.db";
    private static final int DB_VERSION = 4;
    private static final String TABLE_USERS = "users";

    private static UserDBHelper instance;

    public static synchronized UserDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)");

        createTables(db);
        insertSampleData(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS houses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "community_name TEXT, " +
                "city TEXT, " +
                "district TEXT, " +
                "area TEXT, " +
                "monthly_rent INTEGER, " +
                "house_type TEXT, " +
                "building_area REAL, " +
                "orientation TEXT, " +
                "rent_type TEXT, " +
                "decoration TEXT, " +
                "landlord_name TEXT, " +
                "landlord_phone TEXT, " +
                "description TEXT, " +
                "tags TEXT, " +
                "image_urls TEXT, " +
                "banner_url TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS collect (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT NOT NULL, " +
                "house_id INTEGER NOT NULL)");
    }

    private void insertSampleData(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("account", "13800138000");
        cv.put("password", "123456");
        db.insert(TABLE_USERS, null, cv);

        // ====== 成都 ======
        String[] cdDistricts = {"锦江区","青羊区","金牛区","武侯区","成华区","高新区","天府新区","双流区","郫都区","温江区"};
        String[][] cdCommunities = {
            {"龙湖·晶蓝半岛","华润·翡翠城","仁恒·滨河湾","绿地·锦天府","万科·城市花园","新希望·锦官阁","九龙仓·擎天半岛","中粮·鸿云","恒大都汇华庭","阿玛尼艺术公寓"},
            {"中海·城南一号","保利·康桥","建发·金沙里","蓝光·凯丽美域","恒大·翡翠华庭","绿地·新里城","万科·金域西岭","华润·二十四城","光华逸家","龙湖·源著"},
            {"保利·公园里","龙湖·紫宸","华侨城·原岸","恒大·西锦城","碧桂园·城市花园","北京城建·龙樾","华宇·御澜湾","中铁·西城","招商·中央华城","中海国际社区"},
            {"中海·锦城","保利·花园","绿地·威廉公馆","龙湖·金楠天街","蓝光·金双楠","世茂·玉锦湾","万科·金域缇香","置信·丽都花园","中粮·香颂丽都","阳光城·檀府"},
            {"华润·万象城","龙湖·三千城","招商·雍华府","首创·国际城","蓝光·幸福满庭","信和·御龙山","恒大·中央广场","九龙仓·御园","中铁建·青秀城","万科·魅力之城"},
            {"中海·城南华府","龙湖·时代天街","花样年·花郡","万科·公园五号","保利·国际广场","复地·金融岛","融创·香璟台","朗基·御今缘","华润·凤凰城","建发·鹭洲国际"},
            {"麓湖·生态城","万科·天府锦绣","保利·天空之城","龙湖·九里晴川","中海·天府里","德商·御府天骄","融创·玖棠府","绿地·锦业","恒大·天府半岛","建发·中央湾区"},
            {"蓝光·长岛国际","万科·第五城","中海·右岸","保利·叶语","龙湖·听蓝湾","融创·长滩壹号","九龙仓·时代上城","世茂·城品","华宇·楠苑","远大·林语城"},
            {"万科·理想城","保利·熙公馆","龙湖·弗莱明戈","蜀都万达·华府","花样年·龙年中心","蓝光·公园悦府","同森·锦熙","领地·锦巷兰台","中铁·奥维尔","华宇·天府花城"},
            {"恒大·御景半岛","万科·燕南园","保利·西子城","龙湖·听蓝半岛","彩叠园","合能·珍宝琥珀","蓝润·光华春天","佳兆业·丽晶港","洲际·银海湾","金科·天宸"}
        };
        String[][] cdAreas = {
            {"太古里","九眼桥","攀成钢","东湖公园","狮子山","塔子山","合江亭","牛市口","水碾河","春熙路"},
            {"金沙","光华","浣花溪","草堂","府南新区","太升路","人民公园","少城","万家湾","外金沙"},
            {"茶店子","一品天下","欢乐谷","九里堤","交大路","五块石","沙湾","蜀汉路","天回镇","国宾"},
            {"桐梓林","双楠","高升桥","红牌楼","外双楠","新双楠","武侯祠","川大望江","科华北路","五大花园"},
            {"万象城","建设路","东郊记忆","八里庄","二仙桥","万年场","驷马桥","龙潭寺","杉板桥","猛追湾"},
            {"天府软件园","金融城","世纪城","大源","中和","新川","南延线","天府长城","远大","华阳"},
            {"麓湖","兴隆湖","科学城","锦江生态带","万安","华阳南","正兴","麓山","秦皇寺","煎茶"},
            {"航空港","蛟龙港","东升","九江","怡心湖","永安","黄龙溪","公兴","彭镇","黄水"},
            {"犀浦","红光","郫筒","德源","安靖","团结","三道堰","古城","友爱","安德"},
            {"光华大道","珠江新城","大学城","国色天香","金马","永宁","万春","天府","涌泉","公平"}
        };

        generateCityHouses(db, "成都", cdDistricts, cdCommunities, cdAreas, 50, 1.0, 42);

        // ====== 宜宾 ======
        String[] ybDistricts = {"翠屏区","叙州区","南溪区"};
        String[][] ybCommunities = {
            {"邦泰·国际社区","丽雅·龙城","鲁能·山水原著","绿地·城际空间站","恒大·御景半岛","碧桂园·江山赋","华润·公园九里","蓝光·长岛国际"},
            {"鲁能·山水绿城","邦泰·天誉","丽雅·桃源谷","龙湖·昱湖壹号","中铁·蜀南郡","碧桂园·时代之光","金科·集美天宸","阳光城·文澜府"},
            {"美丽·泽京","碧桂园·翡翠湾","丽雅·上游城","邦泰·翡翠城","远达·鹭湖宫","中梁·壹号院","金科·礼悦东方","阳光·碧水长滩"}
        };
        String[][] ybAreas = {
            {"南岸","西区","临港","老城区","上江北","下江北","天池","白沙湖"},
            {"南岸东区","南岸西区","赵场","南部新区","大地坡","金沙新区","普和","柏溪"},
            {"南溪老城","南溪新城","罗龙","裴石","凤凰","九龙","仙源","滨江新城"}
        };
        generateCityHouses(db, "宜宾", ybDistricts, ybCommunities, ybAreas, 30, 0.62, 12);

        // ====== 绵阳 ======
        String[] myDistricts = {"涪城区","游仙区","安州区"};
        String[][] myCommunities = {
            {"领地·观江府","碧桂园·绵州府","恒大·翡翠龙庭","长虹·国际城","万达·御府","东原·观天下","华润·中央公园","世茂·云锦"},
            {"置信·丽都花园","中梁·壹号院","富临·大都会","碧桂园·天麓","九洲·跃进路","蓝光·雍锦湾","朗基·香颂天府","东原·印江山"},
            {"恒跃·花城","安州·长虹世纪城","宏辰·江山云出","碧桂园·安州府","阳光·溪山春晓","圣鼎·锦云里","欧奇·乐享城","文泉·锦樾"}
        };
        String[][] myAreas = {
            {"园艺山","高新区","经开区","市中心","御营坝","高水","圣水","青义"},
            {"小枧","五里梁","科学城","沈家坝","游仙镇","石马","新桥","魏城"},
            {"花荄","界牌","河东新区","河西","塔水","秀水","桑枣","黄土"}
        };
        generateCityHouses(db, "绵阳", myDistricts, myCommunities, myAreas, 30, 0.67, 22);

        // ====== 德阳 ======
        String[] dyDistricts = {"旌阳区","罗江区"};
        String[][] dyCommunities = {
            {"碧桂园·旌湖上境","恒大·翡翠华庭","保利·国际城","华润·置地广场","万达·御府","绿地·新里城","中融·大名城","世家·雍锦湖"},
            {"罗江·碧桂园","万安·翰林苑","嘉士伯·上城","誉城国际","麓峰·御景","江岸林语","秀水湾·江南","黎明·丽景"}
        };
        String[][] dyAreas = {
            {"黄河新城","旌东新区","城南","老城区","经开区","天元","孝感","旌湖"},
            {"万安","金山","调元","略坪","鄢家","白马关","御营","新盛"}
        };
        generateCityHouses(db, "德阳", dyDistricts, dyCommunities, dyAreas, 20, 0.6, 31);
    }

    private void generateCityHouses(SQLiteDatabase db, String city, String[] districts,
                                     String[][] communities, String[][] areas,
                                     int count, double priceFactor, int seed) {
        String[] houseTypes = {"1室0厅","1室1厅","2室1厅","2室2厅","3室1厅","3室2厅","4室2厅"};
        String[] orientations = {"朝南","朝东","朝西","朝北","南北通透"};
        String[] rentTypes = {"整租","合租"};
        String[] decorations = {"精装修","简装修","毛坯","豪华装修"};
        String[][] tagOptions = {
            {"精装修","近地铁","拎包入住"},
            {"近地铁","随时看房","押一付一"},
            {"精装修","拎包入住","随时看房"},
            {"近地铁","首次出租","拎包入住"},
            {"精装修","押一付一","近地铁"},
            {"随时看房","精装修","拎包入住"},
            {"近地铁","拎包入住"},
            {"精装修","首次出租"},
            {"拎包入住","押一付一","近地铁"},
            {"精装修","近地铁","随时看房","拎包入住"},
        };
        String[] descTemplates = {
            "小区环境优美，物业管理完善，%s，生活配套齐全。",
            "%s，%s，周边商超林立，出行便利。",
            "优质好房，%s，%s，小区安保24小时值守。",
            "%s，装修保养好，%s，安静舒适。",
            "房东直租，%s，%s，拎包即可入住。",
            "%s，%s，小区绿化率高，宜居首选。",
            "新出好房，%s，%s，诚心出租价格可谈。",
            "%s房源，%s，%s，欢迎随时看房。",
            "小区中心位置，%s，%s，采光通风俱佳。",
            "%s，%s，%s，交通便利四通八达。",
        };
        String[] landlords = {"张先生","李女士","王先生","刘女士","陈先生","赵女士","周先生","吴女士","黄先生","孙女士","郑先生","钱女士","杨先生","朱女士","马先生"};
        String[] phonePrefix = {"138","139","136","137","135","158","159","188","189","186","177","176","185","187","133"};

        Random rnd = new Random(seed);

        for (int i = 0; i < count; i++) {
            int di = rnd.nextInt(districts.length);
            String district = districts[di];
            String community = communities[di][rnd.nextInt(communities[di].length)];
            String area = areas[di][rnd.nextInt(areas[di].length)];
            String houseType = houseTypes[rnd.nextInt(houseTypes.length)];
            String orientation = orientations[rnd.nextInt(orientations.length)];
            String rentType = rentTypes[rnd.nextInt(rentTypes.length)];
            String decoration = decorations[rnd.nextInt(decorations.length)];

            double baseArea;
            int baseRent;
            if (houseType.startsWith("1室")) {
                baseArea = 30 + rnd.nextDouble() * 25;
                baseRent = 1200 + rnd.nextInt(1300);
            } else if (houseType.startsWith("2室")) {
                baseArea = 55 + rnd.nextDouble() * 35;
                baseRent = 2000 + rnd.nextInt(2000);
            } else if (houseType.startsWith("3室")) {
                baseArea = 85 + rnd.nextDouble() * 40;
                baseRent = 2800 + rnd.nextInt(3200);
            } else {
                baseArea = 120 + rnd.nextDouble() * 40;
                baseRent = 4000 + rnd.nextInt(4000);
            }

            // 城市价格系数
            int rent = (int)(baseRent * priceFactor);

            // 装修影响价格
            if (decoration.equals("豪华装修")) rent = (int)(rent * 1.2);
            else if (decoration.equals("简装修")) rent = (int)(rent * 0.9);
            else if (decoration.equals("毛坯")) rent = (int)(rent * 0.75);

            double buildingArea = Math.round(baseArea * 10) / 10.0;

            String landlord = landlords[rnd.nextInt(landlords.length)];
            String phone = phonePrefix[rnd.nextInt(phonePrefix.length)] + String.format("%04d", rnd.nextInt(10000));

            String[] tagSet = tagOptions[rnd.nextInt(tagOptions.length)];
            String tags = String.join(",", tagSet);

            String[] descParts = {decoration, houseType, orientation + "向", community};
            String d1 = descParts[rnd.nextInt(descParts.length)];
            String d2 = descParts[rnd.nextInt(descParts.length)];
            while (d2.equals(d1)) d2 = descParts[rnd.nextInt(descParts.length)];
            String d3 = descParts[rnd.nextInt(descParts.length)];
            while (d3.equals(d1) || d3.equals(d2)) d3 = descParts[rnd.nextInt(descParts.length)];

            String template = descTemplates[rnd.nextInt(descTemplates.length)];
            String description = String.format(template, d1, d2, d3);

            // 随机生成2-5张真实照片(存储照片索引0-29)
            int photoCount = 2 + rnd.nextInt(4);
            StringBuilder images = new StringBuilder();
            for (int p = 0; p < photoCount; p++) {
                if (images.length() > 0) images.append(",");
                images.append(rnd.nextInt(30));
            }

            insertHouseDirect(db, community, city, district, area, rent, houseType,
                    buildingArea, orientation, rentType, decoration, landlord, phone,
                    description, tags, images.toString());
        }
    }

    private void insertHouseDirect(SQLiteDatabase db, String community, String city,
                                   String district, String area, int rent, String houseType,
                                   double buildingArea, String orientation, String rentType,
                                   String decoration, String landlord, String phone,
                                   String desc, String tags, String imageUrls) {
        ContentValues cv = new ContentValues();
        cv.put("community_name", community);
        cv.put("city", city);
        cv.put("district", district);
        cv.put("area", area);
        cv.put("monthly_rent", rent);
        cv.put("house_type", houseType);
        cv.put("building_area", buildingArea);
        cv.put("orientation", orientation);
        cv.put("decoration", decoration);
        cv.put("rent_type", rentType);
        cv.put("landlord_name", landlord);
        cv.put("landlord_phone", phone);
        cv.put("description", desc);
        cv.put("tags", tags);
        cv.put("image_urls", imageUrls);
        cv.put("banner_url", imageUrls.contains(",") ? imageUrls.split(",")[0] : imageUrls);
        db.insert("houses", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS houses");
        db.execSQL("DROP TABLE IF EXISTS collect");
        createTables(db);
        insertSampleData(db);
    }

    public boolean register(String account, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("account", account);
        cv.put("password", password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean login(String account, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "account=? AND password=?",
                new String[]{account, password},
                null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean isAccountExists(String account) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "account=?", new String[]{account},
                null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean resetPassword(String account) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", "123456");
        int rows = db.update(TABLE_USERS, cv, "account=?",
                new String[]{account});
        return rows > 0;
    }

    public boolean changePassword(String account, String oldPassword, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "account=? AND password=?",
                new String[]{account, oldPassword},
                null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update(TABLE_USERS, cv, "account=?",
                new String[]{account});
        return rows > 0;
    }
}
