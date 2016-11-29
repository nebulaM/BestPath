package com.nebulaM.android.bestpath;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    public static final String KEY_PREF_BG_COLOR="pref_key_background_color";
    private SharedPreferences sharedPref;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener;

    private String mBackgroundColorPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setBackground();
        if (savedInstanceState == null) {
            GameFragment newFragment = new GameFragment();
            //TODO:addToBackStack NOT working
            getFragmentManager().beginTransaction().add(R.id.frag_container, newFragment).commit();
            mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    //TODO:customize color for path in game
                    // Implementation
                    if (key.equals(MainActivity.KEY_PREF_BG_COLOR)){

                        setBackground();
                    }

                }
            };

            sharedPref.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        }

    }
    @Override
    public void onResume() {
        super.onResume();
        sharedPref.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        Toast.makeText(this,"ActResume!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPref.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        Toast.makeText(this,"ActPause!",Toast.LENGTH_SHORT).show();
    }

    public void setBackground(){
        mBackgroundColorPref = sharedPref.getString(KEY_PREF_BG_COLOR, "");
        if(mBackgroundColorPref.equals("gradient_background_cyan")) {
            this.findViewById(R.id.frag_container).setBackgroundColor(0xff303030);
        }
        else if(mBackgroundColorPref.equals("gradient_background_pink")) {
            this.findViewById(R.id.frag_container).setBackgroundResource(R.drawable.gradient_background_pink);
        }
    }
}