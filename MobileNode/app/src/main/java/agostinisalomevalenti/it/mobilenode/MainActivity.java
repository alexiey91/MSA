package agostinisalomevalenti.it.mobilenode;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.services.sqs.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import agostinisalomevalenti.it.mobilenode.Fragment.ReadFragment;
import agostinisalomevalenti.it.mobilenode.Fragment.SetFilterFragment;
import agostinisalomevalenti.it.mobilenode.Fragment.SettingsFragment;
import agostinisalomevalenti.it.mobilenode.Fragment.WriteFragment;
import agostinisalomevalenti.it.mobilenode.Utils.AWSSimpleQueueServiceUtil;
import agostinisalomevalenti.it.mobilenode.Utils.DBHelper;
import agostinisalomevalenti.it.mobilenode.Utils.Util;


/*
* MainActivity gestisce tutto il flusso di esecuzione dell'appiclazione, generando attraverso un AsyncTask una serie di Fragment
* a seconda delle scelte dell'utente. Genera la coda di ricezione di tutti i messaggi attrevero un id univoco, monitora lo stato
* di carica della batteria impostando i tempi di pull a seconda dello stato di carica
* */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Util util = new Util();
    AWSSimpleQueueServiceUtil test;
    private Button button;
    private ListView spinn;
    private ListView mylist;
    public String akey;
    public String skey;

    private String unique_id;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//Permette la connessione ad Internet con il Main Thread
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
            db = new DBHelper(getApplicationContext());

//Funzioni di tipo Get e Post Http verso il server
            //unique_id identifica la versione di Android sul dispositivo
            unique_id= Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
            test.createQueue(unique_id);
            Log.e("UserID",unique_id);

//AsyncTask per la gestione di tutti i thread
           new AsyncPullTask().execute("0");

        }catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class AsyncPullTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {

            try {


                String queueUrl = test.getQueueUrl(unique_id);
                List<Message> messageList = new ArrayList();

                do {
                    messageList = test.getMessagesFromQueue(queueUrl);
                    JSONObject jsonObject;
                    for (int i = 0; i < messageList.size(); i++) {

                        jsonObject = new JSONObject(messageList.get(i).getBody());
                        Date data = new Date();
                       // db.insertTableFiltered(data, "Server", messageList.get(i).getBody());
                        db.insertTableFiltered(data, jsonObject.getString("topicName"),jsonObject.getString("messageBody") );
                        test.deleteMessageFromQueue(queueUrl, messageList.get(i));


                    }
                } while (messageList.size() != 0);


            }catch(NullPointerException e){
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                //imposto il tempo di attesa in secondi
                Thread.sleep(Integer.valueOf(params[0])*1000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (util.getBatteryLevel(MainActivity.this)>=75.0) {

              return "5";
          }
          else if(util.getBatteryLevel(MainActivity.this)>= 50.0){
              return  "10";
          }
          else {
              return "30";
          }



        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Context context= getApplicationContext();
           new AsyncPullTask().execute(result);

            return;

        }


        @Override
        protected void onPreExecute() {
            Context context= getApplicationContext();
            Toast t= Toast.makeText(context,"Execution start",Toast.LENGTH_LONG);
            t.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
    //Elenco dei Fragment allegati al Menù laterale
        if (id == R.id.nav_camera) {

            fragment= new WriteFragment();

        } else if (id == R.id.nav_gallery) {
            fragment = new ReadFragment();

        } else if (id == R.id.nav_slideshow) {
            fragment = new SetFilterFragment();

        } else if (id == R.id.nav_manage) {
            fragment= new SettingsFragment();

        }
        if(fragment!=null){
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_frame,fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
