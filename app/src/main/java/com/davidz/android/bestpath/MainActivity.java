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


    private GameDrawing mGameDrawing;
    private ImageButton mResetButton;
    private ImageButton mRestartButton;
    private ImageButton mNextLevelButton;
    private ImageButton mPreviousLevelButton;
    private ImageButton mSettingsButton;
    static String mBackgroundColorPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mBackgroundColorPref = sharedPref.getString(SettingsFragment.KEY_PREF_BG_COLOR, "");
        if(mBackgroundColorPref.equals("gradient_background_cyan")) {
            this.findViewById(R.id.main_game_view).setBackgroundResource(R.drawable.gradient_background_cyan);
        }
        else if(mBackgroundColorPref.equals("gradient_background_pink")) {
            this.findViewById(R.id.main_game_view).setBackgroundResource(R.drawable.gradient_background_pink);
        }

        mGameDrawing=(GameDrawing)this.findViewById(R.id.GameDrawing);

        mResetButton=(ImageButton)findViewById(R.id.ResetButton);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.reset();
            }
        });

        mRestartButton=(ImageButton)findViewById(R.id.RestartButton);
        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.restart();
            }
        });

        mNextLevelButton=(ImageButton)findViewById(R.id.NextLevelButton);
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.nextLevel();
            }
        });

        mPreviousLevelButton=(ImageButton)findViewById(R.id.PreviousLevelButton);
        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.previousLevel();
            }
        });

        mSettingsButton=(ImageButton)findViewById(R.id.SettingButton);

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PreferenceFragment newFragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.main_game_view, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }

        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }



    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static final String KEY_PREF_BG_COLOR="pref_key_background_color";
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setBackground();

            getView().setClickable(true);
        }
        //TODO:!!!!!shared preference is not correct
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(KEY_PREF_BG_COLOR)){
                Preference mPref = findPreference(key);
                mPref.setDefaultValue(sharedPreferences.getString(key, ""));
                mBackgroundColorPref=sharedPreferences.getString(key, "");
                setBackground();
            }
        }
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void setBackground(){
            if(mBackgroundColorPref.equals("gradient_background_cyan")) {
                getView().setBackgroundResource(R.drawable.gradient_background_cyan);
            }
            else if(mBackgroundColorPref.equals("gradient_background_pink")) {
                getView().setBackgroundResource(R.drawable.gradient_background_pink);
            }
        }

    }
}