package agostinisalome.it.mobilenode.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.BatteryManager;

import org.json.JSONArray;
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
	
    /* type:CREATE/DELETE */
    public static JSONObject createDelete(String type,String userID,String topicName){
        JSONObject message = new JSONObject();
        try {
            message.put("type",type);
            message.put("userID",userID);
            message.put("topicName",topicName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
    /* type : SUB/UNSUB/UNSUBALL
    *  userId: androidid */
    public static JSONObject subUnsub(String type,String userID,String topicName,String filter){
        JSONObject message = new JSONObject();
        try {
            message.put("type",type);
            message.put("userID",userID);
            message.put("topicName",topicName);
            message.put("filter",filter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
    /* userId: andoidid */
    public static JSONObject publish(String userID,String topicName,String messageBody){
        JSONObject message = new JSONObject();
        try {
            message.put("userID",userID);
            message.put("topicName",topicName);
            message.put("messageBody",messageBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String getTopicOccurrency(String topic,ArrayList<String> filter) throws JSONException {


        JSONObject jsonArray;
        for(int i =0; i<filter.size();i++){
            jsonArray =  new JSONObject(filter.get(i));
            if(jsonArray.getString("topic").equals(topic))
                return jsonArray.getString("filter");
        }
        return "";

    }

}
