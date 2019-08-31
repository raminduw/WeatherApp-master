package com.ramindu.weeraman.weather.utils;

import android.app.Activity;
import android.graphics.Typeface;

public class FontProvider {

    public static Typeface getRobotoFont(Activity activity){
        return Typeface.createFromAsset(activity.getAssets(),
                "font/Roboto-Regular.ttf");
    }
}
