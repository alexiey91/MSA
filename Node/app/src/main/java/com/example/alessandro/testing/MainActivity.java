package com.example.alessandro.testing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.services.sqs.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.id.message;


public class MainActivity extends Activity {
    private long initial_X_coord=0;
    Util util = new Util();
    AWSSimpleQueueServiceUtil test;
    private Button button;
    private ListView spinn;
    private ListView mylist;
   // private ListView listView;
    public String akey;
    public String skey;
    public  TelephonyManager tManager = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
    public String UserId;
   /* ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
        /* connessione aws */
        try {
            akey = util.getProperty("accessKey", this.getApplicationContext());
            skey = util.getProperty("secretKey", this.getApplicationContext());
            test = new AWSSimpleQueueServiceUtil(akey, skey);




            UserId = tManager.getDeviceId();
            Log.e("UserID",UserId);
        }catch (IOException e) {
            e.printStackTrace();
        }
        //new PostTask().execute();


        new AsyncTaskRunner().execute();

        /*expListView = (ExpandableListView) findViewById(R.id.topicListView);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);*/



        spinn=(ListView) findViewById(R.id.topicListView);
       /* try {
            String queueUrl = test.getQueueUrl("Server");

            List<String> lista = test.listQueues().getQueueUrls();
            List<String> filtered = new ArrayList<String>();
            for(int i=0;i<lista.size();i++)
                filtered.add(lista.get(i).substring(lista.get(i).lastIndexOf("/")+1,lista.get(i).length()));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    filtered
            );
            spinn.setAdapter(adapter);

        }catch(NullPointerException e){
            e.printStackTrace();

        }*/

        /*button =(Button) findViewById(R.id.btnconnect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test.createQueue("Server");
             /*   String queueUrl  = test.getQueueUrl("Server");
                // test.sendMessageToQueue(queueUrl,"Stefano Riccone Abbruzzese");
                List<Message>messageList = new ArrayList();
                //messageList=test.getMessagesFromQueue(queueUrl);
                List<String> temp = new ArrayList<>();
                do {
                    messageList = test.getMessagesFromQueue(queueUrl);
                    for (int i = 0; i < messageList.size(); i++) {
                        temp.add(messageList.get(i).getBody());
                        test.deleteMessageFromQueue(queueUrl,messageList.get(i));
                        Log.e("Message from Server", messageList.get(i).getMessageId() + "dimensione:" + i);
                    }
                }     while(messageList.size()!=0);

                if(temp.isEmpty()) Log.e("TEMP ","VUoto");
                for(int i =0 ; i< temp.size();i++){
                    Log.v("TEMP:",temp.get(i));}
                List<String> lista = new ArrayList<String>();
                lista=Util.parsingJsonObject(temp,"topic");
                // recupero la lista dal layout
                mylist = (ListView) findViewById(R.id.listView);
                // creo e istruisco l'adattatore
                final ArrayAdapter <String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, lista);
                // inietto i dati
                mylist.setAdapter(adapter);


            }

        });*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                initial_X_coord = Math.round(eventX);
                break;
            case MotionEvent.ACTION_UP:
                long positionDelta = Math.round(eventX) - initial_X_coord;

                if (positionDelta < -400){
                    Intent i=new Intent(this,WriteActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(this, "<<<<  Slide left for change view !", Toast.LENGTH_SHORT).show();
                }

                return true;
        }
        return false;
    }




    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {
            Log.e("Stampa","ciao");
            List<String> filtered = new ArrayList<String>();
            JSONObject json = new JSONObject();

            try {
                String queueUrl = test.getQueueUrl("Server");

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
            Context context= getApplicationContext();
            List<String> temp= new ArrayList<>();
            try {
                JSONObject json = new JSONObject(result);
                List<String> t = new ArrayList<>();
                for(int j=0;j<json.length();j++)
                    temp.add(json.getString(""+j+""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast t= Toast.makeText(context,"Finita esecuzione",Toast.LENGTH_LONG);
            t.show();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    temp
            );
            spinn.setAdapter(adapter);
        }


        @Override
        protected void onPreExecute() {
            Context context= getApplicationContext();
            Toast t= Toast.makeText(context,"Inizio esecuzione",Toast.LENGTH_LONG);
            t.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

