package agostinisalome.it.mobilenode.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.sqs.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import agostinisalome.it.mobilenode.Utils.AWSSimpleQueueServiceUtil;
import agostinisalome.it.mobilenode.Utils.DBHelper;
import agostinisalome.it.mobilenode.R;
import agostinisalome.it.mobilenode.Utils.Util;

/**
 * Created by Paolo on 26/04/2017.
 */

public class ReadFragment extends Fragment  {

    private ListView topics;
    private TextView text;
    private ProgressDialog p;
    public String akey;
    public String skey;
    private AWSSimpleQueueServiceUtil test;
    DBHelper db;
    public ReadFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_read,container,false);
        db = new DBHelper(getContext());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
        Util util = new Util();
        try {
            akey = util.getProperty("accessKey", this.getContext());
            skey = util.getProperty("secretKey", this.getContext());
            test = new AWSSimpleQueueServiceUtil(akey, skey);
            text = (TextView) view.findViewById(R.id.textViewRead);

            new AsyncTaskReader().execute();

        }catch (IOException e) {
            e.printStackTrace();
        }
        /* setting listener */

        topics= (ListView) view.findViewById(R.id.listRead);

        topics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT ).show();
                    new AsyncTaskReaderMessage().execute(adapterView.getItemAtPosition(i).toString());
            }
        });

        /*try {
            List<String> prov= new ArrayList<>();
            prov= db.getAllTopic();
            for(int k=0;k<prov.size();k++)
                Log.e("LISTA","Lista["+k+"]:"+prov.get(k));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            List<String> prov= new ArrayList<>();
            prov= db.getAllFilteredMessage();
            for(int k=0;k<prov.size();k++)
                Log.e("MSG","MSG["+k+"]:"+prov.get(k));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return view;
    }

//async della lista dei topic
    private class AsyncTaskReader extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {

//            List<String> filtered = new ArrayList<String>();
            JSONObject json = new JSONObject();
            JSONObject temp;
            try {


                //List<String> lista = test.listQueues().getQueueUrls();
                List<String> lista = db.getAllTopicFilter();

                for(int i=0;i<lista.size();i++) {
                    temp = new JSONObject(lista.get(i));
                  //  filtered.add(lista.get(i).substring(lista.get(i).lastIndexOf("/") + 1, lista.get(i).length()));
                    json.put(""+i+"", temp.getString("topic"));
                }

            }catch(NullPointerException e){
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Context context= getContext();
            List<String> temp= new ArrayList<>();
            try {
                JSONObject json = new JSONObject(result);
                List<String> t = new ArrayList<>();

                for(int j=0;j<json.length();j++) {
                    temp.add(json.getString("" + j + ""));
                    db.insertNotExistTableTopic(temp.get(j), new Date());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                text.setText("List of Subscribes Topics");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        temp
                );
                topics.setAdapter(adapter);
                p.dismiss();
            }catch(NullPointerException e){
                e.printStackTrace();

            }
        }


        @Override
        protected void onPreExecute() {
            Context context= getContext();
            p = new ProgressDialog(context);
            p.setMessage(Html.fromHtml("<b>Waiting Loading....</b>"));
            p.setIndeterminate(false);
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setCancelable(false);
            p.show();

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }
//in teoria è la parte della pull
    private class AsyncTaskReaderMessage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            JSONObject json = new JSONObject();
            try {


                //List<String> lista = test.listQueues().getQueueUrls();
                List<String> lista = db.getFilteredMessageByTopicName(params[0]);
                Log.e("LISTA back",lista.toString());
                for(int i=0;i<lista.size();i++) {
                    JSONObject temp = new JSONObject(lista.get(i));
                    Log.e("temp","temp"+temp.getString("CONTENT"));
                    //  filtered.add(lista.get(i).substring(lista.get(i).lastIndexOf("/") + 1, lista.get(i).length()));
                    json.put(""+i+"", temp.getString("CONTENT"));
                }

            }catch(NullPointerException e){
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();

        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Context context= getContext();
            List<String> temp= new ArrayList<>();
            try {
                JSONObject json = new JSONObject(result);
                Log.e("RESULT",result);
                for(int j=0;j<json.length();j++) {
                    temp.add(json.getString(""+j+""));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        temp
                );
                topics.setAdapter(adapter);
                text.setText("List of selected Topic messages");

                topics.setOnItemClickListener(null);
                p.dismiss();
            }catch(NullPointerException e){
                e.printStackTrace();

            }

        }


        @Override
        protected void onPreExecute() {
            Context context= getContext();
            p = new ProgressDialog(context);
            p.setMessage(Html.fromHtml("<b>Waiting Loading....</b>"));
            p.setIndeterminate(false);
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setCancelable(false);
            p.show();

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }
}

