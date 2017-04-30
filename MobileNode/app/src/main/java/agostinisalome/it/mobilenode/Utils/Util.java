package agostinisalome.it.mobilenode.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static android.content.Context.CONNECTIVITY_SERVICE;

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
    /* userId: androidid */
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


    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String POST(String url, JSONObject json){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String data = "";


            // 3. convert JSONObject to JSON to String
            data = json.toString();



            // 5. set json to StringEntity
            StringEntity se = new StringEntity(data);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    // check network connection
    public boolean isConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }



}
