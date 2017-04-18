package com.example.alessandro.testing;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by alessandro on 13/04/2017.
 */
public class Util {

    public static String getProperty(String key,Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("awscredential.properties");
        properties.load(inputStream);
        return properties.getProperty(key);

    }
}
