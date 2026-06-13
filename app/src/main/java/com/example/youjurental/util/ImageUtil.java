package com.example.youjurental.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtil {
    public static final int REQUEST_GALLERY = 2001;
    public static final int REQUEST_CAMERA = 2002;

    public static Uri currentCameraUri;

    public static void openGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_GALLERY);
    }

    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = createImageFile(activity);
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".fileprovider", photoFile);
                currentCameraUri = uri;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    private static File createImageFile(Context context) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "JPEG_" + timeStamp + "_";
            File cacheDir = context.getCacheDir();
            return File.createTempFile(fileName, ".jpg", cacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveImageToCache(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            File file = new File(context.getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String path = saveImageToCache(context, uri);
        if (path != null) return path;
        if (currentCameraUri != null) {
            path = currentCameraUri.getPath();
            if (path != null && new File(path).exists()) return path;
        }
        return uri.getPath();
    }
}
