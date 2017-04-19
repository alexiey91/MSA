package com.example.alessandro.testing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.services.sqs.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private long initial_X_coord=0;

    private Button button;
   // private ListView listView;
    public String akey;
    public String skey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

      //  AmazonSQSClient sqs = new AmazonSQSClient();
      //  sqs.setEndpoint("https://sqs.eu-central-1.amazonaws.com/579332910827/prova");
        Util util = new Util();
        try {
            akey= util.getProperty("accessKey",this.getApplicationContext());
            skey= util.getProperty("secretKey",this.getApplicationContext());
            Log.e("accessKey", akey);
            AWSSimpleQueueServiceUtil test = new AWSSimpleQueueServiceUtil(akey,skey);
            String queueUrl  = test.getQueueUrl("Server");
            // test.sendMessageToQueue(queueUrl,"Stefano Riccone Abbruzzese");

            List<Message>messageList = new ArrayList();
            messageList=test.getMessagesFromQueue(queueUrl);

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

           // test.createQueue("CiaoPaolo");
            List<String> lista = new ArrayList<String>();

            //JSONArray json = new JSONArray(temp);
           /*for(int i=0; i<temp.size();i++)
               try {
                   JSONObject json = new JSONObject(temp.get(i));
                 lista.add(json.getString("topic"));
               } catch (JSONException e) {
                   e.printStackTrace();
               }
*/
          lista=Util.parsingJsonObject(temp,"topic");
            // recupero la lista dal layout
            final ListView mylist = (ListView) findViewById(R.id.listView);

            // creo e istruisco l'adattatore
            final ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista);

            // inietto i dati
            mylist.setAdapter(adapter);


        } catch (IOException e) {
            e.printStackTrace();
        }
      //  String queueUrl  = test.getQueueUrl("StefanoPaolo");
      //  test.sendMessageToQueue(queueUrl,"Stefano");

      //  Log.e("queueList",test.listQueues().toString());
     //   test.createQueue("StefanoPaolo");


    button =(Button) findViewById(R.id.btnconnect);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                Context context= getApplicationContext();
            Toast t = Toast.makeText(context,"cliaccato",Toast.LENGTH_SHORT );
                t.show();
            }
        });


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
          //  AWSSimpleQueueServiceUtil test = new AWSSimpleQueueServiceUtil(akey,skey);
          //  String queueUrl  = test.getQueueUrl("Server");
         //   test.sendMessageToQueue(queueUrl, "Paolo Ebreo");
         //   for(int i=0; i<test.getMessagesFromQueue(queueUrl).size();i++)
            //    Log.e("Message from Server", test.getMessagesFromQueue(queueUrl).get(i).getMessageId()+"dimensione:"+test.getMessagesFromQueue(queueUrl).size());
          /* String testo= test.listQueues().toString();
            Context context= getApplicationContext();
            Toast t= Toast.makeText(context,testo,Toast.LENGTH_LONG);
            t.show();

            test.createQueue("PaoloEbreo");*/

            return null;}


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Context context= getApplicationContext();
            Toast t= Toast.makeText(context,"Finita esecuzione",Toast.LENGTH_LONG);
            t.show();
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
