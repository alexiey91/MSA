package agostinisalomevalenti.it.mobilenode.Fragment;

import android.app.Activity;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;


import agostinisalomevalenti.it.mobilenode.R;
import agostinisalomevalenti.it.mobilenode.Utils.Util;


/**
 * Created by alessandro on 30/04/2017.
 *
 * Definisce il Fragment relativo alle impostazioni dell'applicazione permettendo all'utente di
 * abilitare un risparmio energetico statico abbasando la luminosità del display attraverso uno switch.
 * */

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

                if(isChecked){


                   //Accede alle impostazioni di sistema e cambia la luminosità
                    Settings.System.putInt(getContext().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 20);

                    WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
                    lp.screenBrightness =0.1f;
                    ((Activity) getContext()).getWindow().setAttributes(lp);

                }else{
                    //Accede alle impostazioni di sistema e ripristina la luminosità iniziale
                    Settings.System.putInt(getContext().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 20);

                    WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
                    lp.screenBrightness =1.0f;
                    ((Activity) getContext()).getWindow().setAttributes(lp);


                }

            }
        });

        return view;
    }
}
