package com.github.android.bestpath;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity{
    public static final String TAG="MainActivity";
    public static final String SP_FILE_NAME ="BPSP";
    public static final String SP_KEY_First_Time_READ ="SP_KEY_First_Time_READ";
    public static final String SP_KEY_THEME ="SP_KEY_THEME";
    public static final String SP_KEY_SOUND ="SP_KEY_SOUND";
    public static final String SP_KEY_LANG="SP_KEY_LANG";

    public static final int SP_KEY_THEME_DEFAULT=0;
    public static final boolean SP_KEY_SOUND_DEFAULT=true;
    public static final String[] SP_KEY_LANG_PACKAGE={"en","ch", "jp"};

    private SharedPreferences mSP;
    private SharedPreferences.Editor mSPEditor;

    public static SettingsFragment mSettingsFragment;
    public static GameFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        checkSP();
        //Do not need to add to back stack here, because the fragment being replaced is added to the back stack
        // (so in this case R.id.frag_container will be added to back stack if we call addBackStack)
        getFragmentManager().beginTransaction().add(R.id.frag_container, new GameFragment()).commit();
    }

    /**
     * Assign default value to SP if SP had never been read before
     */
    private void checkSP(){
        mSP = getSharedPreferences(MainActivity. SP_FILE_NAME, MODE_PRIVATE);
        mSPEditor=getSharedPreferences(MainActivity. SP_FILE_NAME, MODE_PRIVATE).edit();
        int readAgain=mSP.getInt(SP_KEY_First_Time_READ,-99);
        if(readAgain==-99){
            Log.d(TAG,"First time access this preference file!");
            mSPEditor.putInt(SP_KEY_First_Time_READ,1);
            mSPEditor.putInt(SP_KEY_THEME,SP_KEY_THEME_DEFAULT);
            mSPEditor.putBoolean(SP_KEY_SOUND,SP_KEY_SOUND_DEFAULT);
            mSPEditor.putString(SP_KEY_LANG,SP_KEY_LANG_PACKAGE[0]);
            mSPEditor.commit();

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Toast.makeText(this,"ActResume!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();

        //Toast.makeText(this,"ActPause!",Toast.LENGTH_SHORT).show();
    }

}