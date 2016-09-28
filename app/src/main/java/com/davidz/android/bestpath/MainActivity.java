package com.nebulaM.android.bestpath;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import com.nebulaM.android.bestpath.drawing.GameDrawing;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_PREF_BG_COLOR="pref_key_background_color";
    private SharedPreferences sharedPref;

    static String mBackgroundColorPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setBackground();
        if (savedInstanceState == null) {
            GameFragment newFragment = new GameFragment();
            getFragmentManager().beginTransaction().add(R.id.activty_main_empty, newFragment).commit();
        }

    }


    public void setBackground(){

        mBackgroundColorPref = sharedPref.getString(KEY_PREF_BG_COLOR, "");
        if(mBackgroundColorPref.equals("gradient_background_cyan")) {
            this.findViewById(R.id.activty_main_empty).setBackgroundResource(R.drawable.gradient_background_cyan);
        }
        else if(mBackgroundColorPref.equals("gradient_background_pink")) {
            this.findViewById(R.id.activty_main_empty).setBackgroundResource(R.drawable.gradient_background_pink);
        }
    }



}