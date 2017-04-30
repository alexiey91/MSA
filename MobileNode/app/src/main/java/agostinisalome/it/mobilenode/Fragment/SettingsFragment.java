package agostinisalome.it.mobilenode.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;


import agostinisalome.it.mobilenode.MainActivity;
import agostinisalome.it.mobilenode.R;
import agostinisalome.it.mobilenode.Utils.Util;


/**
 * Created by alessandro on 30/04/2017.
 */

public class SettingsFragment extends Fragment{

    public  SettingsFragment(){}
private Switch energySavage;
    private Util util;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View view = inflater.inflate(R.layout.activity_settings,container,false);


        //Inizializzazione dello Switch sulla view
        energySavage = (Switch)view.findViewById(R.id.energy_switch);

        energySavage.setChecked(false);

        energySavage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
//Check the Switch selection
                if(isChecked){
                 //   Toast.makeText(getContext(),"Accesso",Toast.LENGTH_SHORT).show();

                   //Access to System setting and change the brightness
                    Settings.System.putInt(getContext().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 20);

                    WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
                    lp.screenBrightness =0.1f;// 100 / 100.0f;
                    ((Activity) getContext()).getWindow().setAttributes(lp);

                }else{
                    //Access to System and restore the brightness
                    Settings.System.putInt(getContext().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 20);

                    WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
                    lp.screenBrightness =1.0f;// 100 / 100.0f;
                    ((Activity) getContext()).getWindow().setAttributes(lp);

                    //Toast.makeText(getContext(),"Spento",Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }
}
