package agostinisalome.it.mobilenode.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by alessandro on 27/04/2017.
 */

public class SetFilterFragment extends Fragment {

    private Button publish;
    private Button clear;
    private Button create;
    private ListView topics;
    private EditText textMulti;
    private TextView text;
    private ProgressDialog p;
    public String akey;
    public String skey;
    private AWSSimpleQueueServiceUtil test;
    DBHelper db;

   public SetFilterFragment(){}
/*
* Fragment per impostare il filtro sui messaggi ricercati dal topic
*
* */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_set_filter,container,false);
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
          text=(TextView) view.findViewById(R.id.textSetFilter);

            new AsyncTaskReader().execute();

        }catch (IOException e) {
            e.printStackTrace();
        }
        /* setting listener */

        topics= (ListView) view.findViewById(R.id.listSetFilter);

        topics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {

                Toast.makeText(getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT ).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                input.setLayoutParams(lp);
                alert.setView(input,50,50,50,50);

                final String temp = adapterView.getItemAtPosition(i).toString();
                alert.setTitle("Scrivi il filtro per il topic "+adapterView.getItemAtPosition(i).toString());
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(getContext(),"OK",Toast.LENGTH_SHORT ).show();
                        String queueUrl  = test.getQueueUrl(temp);
                         test.sendMessageToQueue(queueUrl, String.valueOf(input.getText()));
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                             //   Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT ).show();
                            }
                        });
                alert.show();            }
        });

        try {
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
        }
        return view;
    }

    //async della lista dei topic
    private class AsyncTaskReader extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {

            List<String> filtered = new ArrayList<String>();
            JSONObject json = new JSONObject();

            try {


                List<String> lista = test.listQueues().getQueueUrls();

                for(int i=0;i<lista.size();i++) {
                    filtered.add(lista.get(i).substring(lista.get(i).lastIndexOf("/") + 1, lista.get(i).length()));
                    json.put(""+i+"", filtered.get(i));
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
            // Toast t= Toast.makeText(context,"Finita esecuzione",Toast.LENGTH_LONG);
            try {
                text.setText("Lista Topic Registrati");

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
            p.setMessage("Waiting Loading");
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