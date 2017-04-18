package com.example.alessandro.testing;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

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
            String queueUrl  = test.getQueueUrl("prova");
             test.sendMessageToQueue(queueUrl,"Stefano Riccone Abbruzzese");

            test.createQueue("CiaoPaolo");
            List<String> lista = new ArrayList<String>();

            for(int i=0; i<test.listQueues().getQueueUrls().size();i++){
                lista.add(test.listQueues().getQueueUrls().get(i));
                Log.e("LISTA CODE",lista.get(i));}



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
               // AsyncTaskRunner runner = new AsyncTaskRunner();
                Context context= getApplicationContext();
            Toast t = Toast.makeText(context,"cliaccato",Toast.LENGTH_SHORT );
                t.show();
            }
        });


    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {

            AWSSimpleQueueServiceUtil test = new AWSSimpleQueueServiceUtil(akey,skey);
            String queueUrl  = test.getQueueUrl("prova");
            test.sendMessageToQueue(queueUrl, "Paolo Ebreo");

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
