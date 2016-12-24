package com.github.android.bestpath;


import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.android.bestpath.backend.Game;
import com.github.android.bestpath.mediaPlayer.MediaPlayerSingleton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    public static final String TAG="MainActivity";
    public static final String SP_FILE_NAME ="BPSP";
    public static final String SP_KEY_First_Time_READ ="SP_KEY_First_Time_READ";
    public static final String SP_KEY_THEME ="SP_KEY_THEME";
    public static final String SP_KEY_SOUND ="SP_KEY_SOUND";
    public static final String SP_KEY_LANG="SP_KEY_LANG";
    public static final String SP_KEY_GAME_RECORD="SP_KEY_GAME_RECORD";

    public static final String SP_KEY_GAME_LEVEL="SP_KEY_GAME_LEVEL";
    public static final String SP_KEY_GAME_MODE="SP_KEY_GAME_MODE";
    public static final int SP_KEY_THEME_DEFAULT=0;
    public static final boolean SP_KEY_SOUND_DEFAULT=true;
    public static final String[] SP_KEY_LANG_PACKAGE={"en","ch", "jp"};
    public static final String SP_KEY_GAME_RECORD_DEFAULT ="LV1{0,0};LV2{0,0};LV3{0,0};LV4{0,0};LV5{0,0};LV6{0,0}";

    public static final int SP_KEY_GAME_LEVEL_DEFAULT=3;
    public static final int SP_KEY_GAME_MODE_DEFAULT=0;


    private SharedPreferences mSP;
    private SharedPreferences.Editor mSPEditor;

    protected static Game GAME;
    private int mGameLevel;

    protected final static float GAME_LEVEL_MAX=8.0f;

    //sound from http://www.freesfx.co.uk
    protected static MediaPlayer mMPClick = MediaPlayerSingleton.getInstance();
    public static MediaPlayer mMPSwitch;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMPClick = MediaPlayer.create(getApplicationContext(),R.raw.click_1);
        mMPSwitch= MediaPlayer.create(getApplicationContext(),R.raw.switch_sound);
        Log.d(TAG,"@onCreate: Create media player");
        //use hardware volume key to control audio volume for all fragments under this activity
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //read from shared preference
        checkSP(false);

        GAME =new Game();
        GAME.init( mGameLevel,mSP.getInt(SP_KEY_GAME_MODE,SP_KEY_GAME_MODE_DEFAULT),
                parseGameRecordString(TAG,mSP.getString(SP_KEY_GAME_RECORD,SP_KEY_GAME_RECORD_DEFAULT)));

        //Do not need to add to back stack here, because the fragment being replaced is added to the back stack
        // (so in this case R.id.frag_container will be added to back stack if we call addBackStack)
        getFragmentManager().beginTransaction().add(R.id.frag_container, new GameFragment()).commit();
        //rate the app
        AppRater.appLaunched(this);
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
            mSPEditor.putString(SP_KEY_GAME_RECORD, SP_KEY_GAME_RECORD_DEFAULT);
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
        if(mMPClick ==null){
            Log.d(TAG,"@onResume: Create media player");
            mMPClick =MediaPlayer.create(getApplicationContext(), R.raw.click_1);
        }
        if(mMPSwitch==null){
            mMPSwitch= MediaPlayer.create(getApplicationContext(),R.raw.switch_sound);
        }
    }

    @Override
    public void onPause() {
        //write user preferred game info to SP
        if (mGameLevel != GAME.getGameLevel()) {
            Log.d(TAG, "@onPause: Save new preferred Game level");
            mGameLevel = GAME.getGameLevel();
            mSPEditor.putInt(SP_KEY_GAME_LEVEL, GAME.getGameLevel()).apply();
        }
        super.onPause();
        //release media player
        if (mMPClick != null) {
            Log.d(TAG, "@onPause: Release media player");
            mMPClick.release();
            mMPClick = null;
        }
        if (mMPSwitch != null) {
            mMPSwitch.release();
            mMPSwitch = null;
        }

        if (MainActivity.GAME.getGameRecord(true)!=null){
            Log.d(TAG, "@onPause: write game record "+MainActivity.GAME.getGameRecord(true));
            mSP.edit().putString(MainActivity.SP_KEY_GAME_RECORD, MainActivity.parseGameRecordList(TAG, MainActivity.GAME.getGameRecord(true))).apply();
        }
    }

    /**
     *
     * @param tag   indicates which activity is using this method
     * @param rawData string read from SP file, default is defined by MainActivity.SP_KEY_GAME_RECORD_DEFAULT
     * @return list list of clear time:
     *              Location 0-5 contains clear times in normal mode, from lv1 to lv6
     *              Location 6-11 contains clear times in hard mode, from lv1 to lv6
     */
    public static List<Integer> parseGameRecordString(String tag, String rawData){
        String[] eachLevel=rawData.split(";");
        List<Integer> normal=new ArrayList<>((eachLevel.length)/2);
        List<Integer> hard=new ArrayList<>((eachLevel.length)/2);
        List<Integer> list=new ArrayList<>(eachLevel.length);
        for(String record : eachLevel){
            Log.d(tag,"@parseGameRecordString record is: "+record);
            String normal_hard=record.substring(record.indexOf("{")+1,record.indexOf("}"));
            //Log.d(tag,"@parseGameRecordString normal_hard is: "+normal_hard);
            int furtherSplit=normal_hard.indexOf(",");
            normal.add(Integer.valueOf(normal_hard.substring(0,furtherSplit)));
            hard.add(Integer.valueOf(normal_hard.substring(furtherSplit+1)));
        }
        list.addAll(normal);
        list.addAll(hard);
        Log.d(tag,"@parseGameRecordString return list of cleared time: "+list);
        return list;
    }


    public static String parseGameRecordList(String tag,List<Integer> list){
        String newRawGameRecord="";
        int offset=list.size()/2;
        for(int i=0;i<offset;++i){
            newRawGameRecord=newRawGameRecord.concat("LV"+(i+1)+"{"+list.get(i)+","+list.get(i+offset)+"}");
            if(i==offset-1){
                break;
            }
            newRawGameRecord=newRawGameRecord.concat(";");
        }
        Log.d(tag,"@parseGameRecordList: new Game Record is "+newRawGameRecord);
        return newRawGameRecord;
    }

}