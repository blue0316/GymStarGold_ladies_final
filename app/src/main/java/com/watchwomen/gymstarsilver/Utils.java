package com.watchwomen.gymstarsilver;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mac on 3/13/18.
 */

public class Utils {
    public static Bitmap GetImageFromAssets(Context context, String fileName)
    {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(fileName);
            Bitmap  bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
