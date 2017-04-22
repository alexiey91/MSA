package agostinisalome.it.mobilenode;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agostinisalome.it.mobilenode.MainActivity;
import agostinisalome.it.mobilenode.R;


public class WriteActivity extends Activity {
    private Button publish;
    private Button clear;
    private Button create;
    private Spinner topics;
    private EditText textMulti;
    private long initial_X_coord=0;

    public String akey;
    public String skey;
    private AWSSimpleQueueServiceUtil test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
        Util util = new Util();
        try {
            akey = util.getProperty("accessKey", this.getApplicationContext());
            skey = util.getProperty("secretKey", this.getApplicationContext());
            test = new AWSSimpleQueueServiceUtil(akey, skey);

        }catch (IOException e) {
            e.printStackTrace();
        }
        /* setting listener */

        topics=(Spinner) findViewById(R.id.topics);
        try {
            String queueUrl = test.getQueueUrl("Server");

            List<String> lista = test.listQueues().getQueueUrls();
            List<String> filtered = new ArrayList<String>();
            for(int i=0;i<lista.size();i++)
                filtered.add(lista.get(i).substring(lista.get(i).lastIndexOf("/")+1,lista.get(i).length()));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    filtered
            );
            topics.setAdapter(adapter);

        }catch(NullPointerException e){
            e.printStackTrace();

        }
        textMulti=(EditText) findViewById(R.id.corpse_text);
        publish=(Button)findViewById(R.id.send_text);
        publish.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                //  AWSSimpleQueueServiceUtil test = new AWSSimpleQueueServiceUtil(akey,skey);
                String queueUrl  = topics.getSelectedItem().toString();
                String text = textMulti.getText().toString();
                test.sendMessageToQueue(queueUrl, text);
            }
        });
        clear=(Button)findViewById(R.id.clear_text);
        clear.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                textMulti.setText("");
            }
        });
        create=(Button)findViewById(R.id.create_topic);
        create.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                builder.setTitle("Add topic");

                // Set up the input
                final EditText input = new EditText(WriteActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        test.createQueue(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

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

                if (positionDelta > 400){
                    Intent i=new Intent(this,MainActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(this, "Slide right for change view ! >>>>", Toast.LENGTH_SHORT).show();
                }

                return true;
        }
        return false;
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