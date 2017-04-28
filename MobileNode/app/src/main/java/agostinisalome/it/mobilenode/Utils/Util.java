package agostinisalome.it.mobilenode.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.BatteryManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

    public static List<String> parsingJsonObject(List<String>message,String filter){
        List<String> result = new ArrayList<>();

        for(int i=0; i<message.size();i++)
            try {
                JSONObject json = new JSONObject(message.get(i));
                result.add(json.getString(filter));
            } catch (JSONException e) {
                e.printStackTrace();
            }

    return result;
    }

    public float getBatteryLevel(Context context) {

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }
}
