package com.github.android.bestpath;


import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.android.bestpath.backend.Game;
import com.github.android.bestpath.mediaPlayer.MediaPlayerSingleton;

public class MainActivity extends AppCompatActivity{
    public static final String TAG="MainActivity";
    public static final String SP_FILE_NAME ="BPSP";
    public static final String SP_KEY_First_Time_READ ="SP_KEY_First_Time_READ";
    public static final String SP_KEY_THEME ="SP_KEY_THEME";
    public static final String SP_KEY_SOUND ="SP_KEY_SOUND";
    public static final String SP_KEY_LANG="SP_KEY_LANG";

    public static final String SP_KEY_GAME_LEVEL="SP_KEY_GAME_LEVEL";
    public static final String SP_KEY_GAME_MODE="SP_KEY_GAME_MODE";
    public static final int SP_KEY_THEME_DEFAULT=0;
    public static final boolean SP_KEY_SOUND_DEFAULT=true;
    public static final String[] SP_KEY_LANG_PACKAGE={"en","ch", "jp"};

    public static final int SP_KEY_GAME_LEVEL_DEFAULT=3;
    public static final int SP_KEY_GAME_MODE_DEFAULT=0;
    private SharedPreferences mSP;
    private SharedPreferences.Editor mSPEditor;

    protected static Game GAME;
    private int mGameLevel;
    protected static int GAME_EDGE_PROBABILITY;

    //sound from http://www.freesfx.co.uk
    protected static MediaPlayer mMP = MediaPlayerSingleton.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMP = MediaPlayer.create(getApplicationContext(),R.raw.click_1);
        Log.d(TAG,"@onCreate: Create media player");
        //use hardware volume key to control audio volume for all fragments under this activity
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //read from shared preference
        checkSP(false);

        GAME_EDGE_PROBABILITY=50;
        GAME =new Game('M');
        GAME.init( mGameLevel,GAME_EDGE_PROBABILITY);

        //Do not need to add to back stack here, because the fragment being replaced is added to the back stack
        // (so in this case R.id.frag_container will be added to back stack if we call addBackStack)
        getFragmentManager().beginTransaction().add(R.id.frag_container, new GameFragment()).commit();
    }

    /**
     * Assign default value to SharedPreference if SP had never been read before
     * @param Overwrite overwrite the SP file w/ default parameters
     */
    private void checkSP(boolean Overwrite){
        mSP = getSharedPreferences(MainActivity. SP_FILE_NAME, MODE_PRIVATE);
        mSPEditor=getSharedPreferences(MainActivity. SP_FILE_NAME, MODE_PRIVATE).edit();
        boolean firstTimeAccess=mSP.getBoolean(SP_KEY_First_Time_READ,true);
        if(Overwrite||firstTimeAccess){
            Log.d(TAG,"@checkSP: Rewrite this preference file with default values!");
            mSPEditor.putBoolean(SP_KEY_First_Time_READ,false);
            mSPEditor.putInt(SP_KEY_THEME,SP_KEY_THEME_DEFAULT);
            mSPEditor.putBoolean(SP_KEY_SOUND,SP_KEY_SOUND_DEFAULT);
            mSPEditor.putString(SP_KEY_LANG,SP_KEY_LANG_PACKAGE[0]);
            mSPEditor.putInt(SP_KEY_GAME_LEVEL,SP_KEY_GAME_LEVEL_DEFAULT);
            mSPEditor.putInt(SP_KEY_GAME_MODE,SP_KEY_GAME_MODE_DEFAULT);
            mSPEditor.commit();

            mGameLevel=SP_KEY_GAME_LEVEL_DEFAULT;
        }else{
            Log.d(TAG,"@checkSP: get saved values from preference file");
            mGameLevel= mSP.getInt(SP_KEY_GAME_LEVEL,SP_KEY_GAME_LEVEL_DEFAULT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMP ==null){
            Log.d(TAG,"@onResume: Create media player");
            mMP =MediaPlayer.create(getApplicationContext(), R.raw.click_1);
        }
    }

    @Override
    public void onPause() {
        //write user preferred game info to SP
        if (mGameLevel != GAME.getGameLevel()) {
            Log.d(TAG,"@onPause: Save new preferred Game level");
            mGameLevel = GAME.getGameLevel();
            mSPEditor.putInt(SP_KEY_GAME_LEVEL, GAME.getGameLevel()).apply();
            }
        super.onPause();
        //release media player
        if(mMP !=null) {
            Log.d(TAG,"@onPause: Release media player");
            mMP.release();
            mMP = null;
        }
    }
}