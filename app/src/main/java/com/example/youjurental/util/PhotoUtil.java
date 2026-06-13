package com.example.youjurental.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.youjurental.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoUtil {

    private static final String[] REAL_PHOTOS = {
        "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1560185893-a55cbc8c57e8?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1585412727339-54e4bae3bbf9?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1560185008-b033106af5c3?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600573472550-8090b5e0745e?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600566753086-00f18f6b0050?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600047509807-ba8f99d2cdde?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1598928506311-c55u597cfe36?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1599427303058-f04cbcf4756f?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1574362848149-11496d93a7c7?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1583608205776-bfd35f0d9f83?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1616137466211-f939a420be84?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1617103996702-96ff29b1c467?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1449844908441-8829872d2607?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1460317442991-0ec209397118?w=600&h=400&fit=crop",
    };

    /**
     * Parse imageUrls string into a list of loadable paths:
     * - Integer (0-29) → Unsplash URL
     * - File path (/data/... or /storage/...) → kept as-is for local loading
     */
    public static List<String> getPhotoUrlList(String imageUrls) {
        List<String> result = new ArrayList<>();
        if (imageUrls == null || imageUrls.isEmpty()) {
            result.add(REAL_PHOTOS[0]);
            return result;
        }
        String[] parts = imageUrls.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;

            // Check if it's a local file path
            if (trimmed.contains("/") || trimmed.contains("\\")) {
                // Local file - verify it exists
                if (new File(trimmed).exists()) {
                    result.add(trimmed);
                }
            } else {
                // Try parsing as integer index for Unsplash URL
                try {
                    int i = Integer.parseInt(trimmed);
                    if (i >= 0 && i < REAL_PHOTOS.length) {
                        result.add(REAL_PHOTOS[i]);
                    }
                } catch (NumberFormatException e) {
                    // Neither a file path nor a valid index — try as file
                    if (new File(trimmed).exists()) {
                        result.add(trimmed);
                    }
                }
            }
        }
        if (result.isEmpty()) result.add(REAL_PHOTOS[0]);
        return result;
    }

    public static void loadFirstPhoto(Context context, ImageView imageView, String imageUrls) {
        List<String> urls = getPhotoUrlList(imageUrls);
        loadPhoto(context, imageView, urls.get(0));
    }

    public static void loadPhoto(Context context, ImageView imageView, String path) {
        if (path == null || path.isEmpty()) {
            imageView.setImageResource(R.drawable.house_photo_1);
            return;
        }
        Glide.with(context)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.house_photo_1)
                .error(R.drawable.house_photo_1)
                .into(imageView);
    }

    public static void loadPhotoRes(Context context, ImageView imageView, int resId) {
        Glide.with(context)
                .load(resId)
                .centerCrop()
                .into(imageView);
    }
}
