package agostinisalome.it.mobilenode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 26/04/2017.
 */

public class WriteFragment extends Fragment implements View.OnClickListener {
    private Button publish;
    private Button clear;
    private Button create;
    private Spinner topics;
    private EditText textMulti;


    public String akey;
    public String skey;
    private AWSSimpleQueueServiceUtil test;
    public WriteFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_write,container,false);

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
            new AsyncTaskReader().execute();
        }catch (IOException e) {
            e.printStackTrace();
        }
        /* setting listener */

        topics=(Spinner) view.findViewById(R.id.topics);

        textMulti=(EditText) view.findViewById(R.id.corpse_text);
        publish=(Button) view.findViewById(R.id.send_text);
        publish.setOnClickListener(this);


        clear=(Button)view.findViewById(R.id.clear_text);
        clear.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                textMulti.setText("");
            }
        });
        create=(Button)view.findViewById(R.id.create_topic);
        create.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add topic");

                // Set up the input
                final EditText input = new EditText(getContext());
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
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_text:
                String queueUrl  = topics.getSelectedItem().toString();
                String text = textMulti.getText().toString();
                if(!text.isEmpty())
                    test.sendMessageToQueue(queueUrl, text);
                else{
                    Toast t = Toast.makeText(getContext(),"Inserire Messaggio",Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
            case R.id.clear_text:
                textMulti.setText("");
                break;
            default:

                break;
        }

    }


    private class AsyncTaskReader extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {

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
            Context context= getContext();
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
            try {


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        temp
                );
                topics.setAdapter(adapter);

            }catch(NullPointerException e){
                e.printStackTrace();

            }
        }


        @Override
        protected void onPreExecute() {
            Context context= getContext();
            Toast t= Toast.makeText(context,"Inizio esecuzione",Toast.LENGTH_LONG);
            t.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }
}

