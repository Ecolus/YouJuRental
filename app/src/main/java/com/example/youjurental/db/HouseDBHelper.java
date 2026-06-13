package com.example.youjurental.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.youjurental.entity.House;

import java.util.ArrayList;
import java.util.List;

public class HouseDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "youjurental.db";
    private static final int DB_VERSION = 4;
    private static final String TABLE_HOUSES = "houses";
    private static final String TABLE_COLLECT = "collect";

    private static HouseDBHelper instance;

    public static synchronized HouseDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HouseDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private HouseDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_HOUSES + " (" +
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

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COLLECT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT NOT NULL, " +
                "house_id INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // UserDBHelper handles the full upgrade including data insertion.
        // This helper just ensures tables exist in case they weren't created yet.
        onCreate(db);
    }

    public long insertHouse(House house) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("community_name", house.getCommunityName());
        cv.put("city", house.getCity());
        cv.put("district", house.getDistrict());
        cv.put("area", house.getArea());
        cv.put("monthly_rent", house.getMonthlyRent());
        cv.put("house_type", house.getHouseType());
        cv.put("building_area", house.getBuildingArea());
        cv.put("orientation", house.getOrientation());
        cv.put("rent_type", house.getRentType());
        cv.put("decoration", house.getDecoration());
        cv.put("landlord_name", house.getLandlordName());
        cv.put("landlord_phone", house.getLandlordPhone());
        cv.put("description", house.getDescription());
        cv.put("tags", house.getTags());
        cv.put("image_urls", house.getImageUrls());
        cv.put("banner_url", house.getBannerUrl() != null ? house.getBannerUrl() : "");
        return db.insert(TABLE_HOUSES, null, cv);
    }

    public int updateHouse(House house) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("community_name", house.getCommunityName());
        cv.put("city", house.getCity());
        cv.put("district", house.getDistrict());
        cv.put("area", house.getArea());
        cv.put("monthly_rent", house.getMonthlyRent());
        cv.put("house_type", house.getHouseType());
        cv.put("building_area", house.getBuildingArea());
        cv.put("orientation", house.getOrientation());
        cv.put("rent_type", house.getRentType());
        cv.put("decoration", house.getDecoration());
        cv.put("landlord_name", house.getLandlordName());
        cv.put("landlord_phone", house.getLandlordPhone());
        cv.put("description", house.getDescription());
        cv.put("tags", house.getTags());
        cv.put("image_urls", house.getImageUrls());
        cv.put("banner_url", house.getBannerUrl() != null ? house.getBannerUrl() : "");
        return db.update(TABLE_HOUSES, cv, "id=?", new String[]{String.valueOf(house.getId())});
    }

    public List<House> getAllHouses() {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, null, null, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    public List<House> getTopHouses(int limit) {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, null, null, null, null,
                "id DESC", String.valueOf(limit));
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    public List<House> getHousesByDistrict(String district) {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, "district=?",
                new String[]{district}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    public List<House> searchHouses(String keyword) {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HOUSES + " WHERE community_name LIKE ? OR area LIKE ? OR district LIKE ?";
        String param = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{param, param, param});
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    public List<House> filterHouses(String priceRange, String houseType, String rentType,
                                    String decoration, String city, String district) {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder where = new StringBuilder("1=1");
        List<String> argsList = new ArrayList<>();

        if (priceRange != null && !priceRange.isEmpty() && !priceRange.equals("不限")) {
            int min = 0, max = Integer.MAX_VALUE;
            if (priceRange.contains("-")) {
                String[] parts = priceRange.replace("元", "").split("-");
                min = Integer.parseInt(parts[0].trim());
                max = Integer.parseInt(parts[1].trim());
            } else if (priceRange.contains("以下")) {
                max = Integer.parseInt(priceRange.replace("元以下", "").trim());
            } else if (priceRange.contains("以上")) {
                min = Integer.parseInt(priceRange.replace("元以上", "").trim());
            }
            where.append(" AND monthly_rent >= ? AND monthly_rent <= ?");
            argsList.add(String.valueOf(min));
            argsList.add(String.valueOf(max));
        }

        if (houseType != null && !houseType.isEmpty() && !houseType.equals("不限")) {
            where.append(" AND house_type=?");
            argsList.add(houseType);
        }

        if (rentType != null && !rentType.isEmpty() && !rentType.equals("不限")) {
            where.append(" AND rent_type=?");
            argsList.add(rentType);
        }

        if (decoration != null && !decoration.isEmpty() && !decoration.equals("不限")) {
            where.append(" AND decoration=?");
            argsList.add(decoration);
        }

        if (city != null && !city.isEmpty() && !city.equals("不限")) {
            where.append(" AND city=?");
            argsList.add(city);
        }

        if (district != null && !district.isEmpty() && !district.equals("不限")) {
            where.append(" AND district=?");
            argsList.add(district);
        }

        String[] args = argsList.toArray(new String[0]);
        Cursor cursor = db.query(TABLE_HOUSES, null, where.toString(), args,
                null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    public House getHouseById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        House house = null;
        if (cursor.moveToFirst()) {
            house = cursorToHouse(cursor);
        }
        cursor.close();
        return house;
    }

    public int deleteHouse(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_HOUSES, "id=?", new String[]{String.valueOf(id)});
    }

    public List<House> getHousesByLandlordPhone(String phone) {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, "landlord_phone=?",
                new String[]{phone}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    // Collect methods
    public boolean addCollect(String userId, int houseId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_COLLECT, null,
                "user_id=? AND house_id=?",
                new String[]{userId, String.valueOf(houseId)},
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("house_id", houseId);
        return db.insert(TABLE_COLLECT, null, cv) != -1;
    }

    public boolean removeCollect(String userId, int houseId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_COLLECT, "user_id=? AND house_id=?",
                new String[]{userId, String.valueOf(houseId)});
        return rows > 0;
    }

    public boolean isCollected(String userId, int houseId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COLLECT, null,
                "user_id=? AND house_id=?",
                new String[]{userId, String.valueOf(houseId)},
                null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public List<House> getCollectedHouses(String userId) {
        List<House> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT h.* FROM " + TABLE_HOUSES + " h INNER JOIN " + TABLE_COLLECT +
                " c ON h.id = c.house_id WHERE c.user_id=? ORDER BY c.id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        while (cursor.moveToNext()) {
            list.add(cursorToHouse(cursor));
        }
        cursor.close();
        return list;
    }

    private House cursorToHouse(Cursor cursor) {
        House house = new House();
        house.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        house.setCommunityName(cursor.getString(cursor.getColumnIndexOrThrow("community_name")));
        house.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
        house.setDistrict(cursor.getString(cursor.getColumnIndexOrThrow("district")));
        house.setArea(cursor.getString(cursor.getColumnIndexOrThrow("area")));
        house.setMonthlyRent(cursor.getInt(cursor.getColumnIndexOrThrow("monthly_rent")));
        house.setHouseType(cursor.getString(cursor.getColumnIndexOrThrow("house_type")));
        house.setBuildingArea(cursor.getDouble(cursor.getColumnIndexOrThrow("building_area")));
        house.setOrientation(cursor.getString(cursor.getColumnIndexOrThrow("orientation")));
        house.setRentType(cursor.getString(cursor.getColumnIndexOrThrow("rent_type")));
        house.setDecoration(cursor.getString(cursor.getColumnIndexOrThrow("decoration")));
        house.setLandlordName(cursor.getString(cursor.getColumnIndexOrThrow("landlord_name")));
        house.setLandlordPhone(cursor.getString(cursor.getColumnIndexOrThrow("landlord_phone")));
        house.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        house.setTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
        house.setImageUrls(cursor.getString(cursor.getColumnIndexOrThrow("image_urls")));
        house.setBannerUrl(cursor.getString(cursor.getColumnIndexOrThrow("banner_url")));
        return house;
    }
}
