package com.github.android.bestpath;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.android.bestpath.R;

public class MainActivity extends AppCompatActivity{
    public static final String SPColorBG ="ColorBG";
    private SharedPreferences mSP;
    private SharedPreferences.OnSharedPreferenceChangeListener mSPListener;

    private String mColorBG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mSP = PreferenceManager.getDefaultSharedPreferences(this);

        setBackground();
        if (savedInstanceState == null) {
            //Do not need to add to back stack here, because the fragment being replaced is added to the back stack
            // (so in this case R.id.frag_container will be added to back stack if we call addBackStack)
            getFragmentManager().beginTransaction().add(R.id.frag_container, new GameFragment()).commit();
            mSPListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    //TODO:customize color for path in game
                    // Implementation
                    if (key.equals(MainActivity.SPColorBG)){

                        setBackground();
                    }

                }
            };

            mSP.registerOnSharedPreferenceChangeListener(mSPListener);

        }

    }
    @Override
    public void onResume() {
        super.onResume();
        mSP.registerOnSharedPreferenceChangeListener(mSPListener);
        //Toast.makeText(this,"ActResume!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        mSP.unregisterOnSharedPreferenceChangeListener(mSPListener);
        //Toast.makeText(this,"ActPause!",Toast.LENGTH_SHORT).show();
    }

    public void setBackground(){
        mColorBG = mSP.getString(SPColorBG, "");
        if(mColorBG.equals("gradient_background_cyan")) {
            this.findViewById(R.id.frag_container).setBackgroundColor(0xff303030);
        }
        else if(mColorBG.equals("gradient_background_pink")) {
            this.findViewById(R.id.frag_container).setBackgroundColor(0xffececec);
        }
    }

}