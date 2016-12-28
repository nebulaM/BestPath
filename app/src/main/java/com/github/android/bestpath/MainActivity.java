package com.github.android.bestpath;


import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.android.bestpath.backend.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    public static final String TAG="MainActivity";
    public static final String SP_FILE_NAME ="BPSP";
    public static final String SP_KEY_First_Time_READ ="SP_KEY_First_Time_READ";
    public static final String SP_KEY_THEME ="SP_KEY_THEME";
    public static final String SP_KEY_SOUND ="SP_KEY_SOUND";
    public static final String SP_KEY_GAME_RECORD="SP_KEY_GAME_RECORD";

    public static final String SP_KEY_GAME_LEVEL="SP_KEY_GAME_LEVEL";
    public static final String SP_KEY_GAME_MODE="SP_KEY_GAME_MODE";
    public static final int SP_KEY_THEME_DEFAULT=0;
    public static final boolean SP_KEY_SOUND_DEFAULT=true;
    //public static final String SP_KEY_GAME_RECORD_DEFAULT ="LV1{9999,9990};LV2{9990,9990};LV3{9990,9990};LV4{9990,9990};LV5{9990,9990};LV6{9990,9990}";
    //public static final String SP_KEY_GAME_RECORD_DEFAULT ="LV1{999,1};LV2{990,90};LV3{990,90};LV4{999,90};LV5{990,0};LV6{990,90}";
    public static final String SP_KEY_GAME_RECORD_DEFAULT ="LV1{9,0};LV2{0,0};LV3{9,9};LV4{9,9};LV5{9,9};LV6{9,9}";
    public static final int SP_KEY_GAME_LEVEL_DEFAULT=3;
    public static final int SP_KEY_GAME_MODE_DEFAULT=0;


    private SharedPreferences mSP;
    private SharedPreferences.Editor mSPEditor;

    protected static Game GAME;
    private int mGameLevel;

    protected final static float GAME_LEVEL_MAX=8.0f;

    public static MediaPlayer mMPClick;
    public static MediaPlayer mMPStep;
    public static MediaPlayer mMPLose;
    public static MediaPlayer mMPWin;


    public static int DISPLAY_LANGUAGE=0;
    public static final int LANGUAGE_ZH_PRC=30;
    public static final int LANGUAGE_ZH_TW=31;
    public static final int LANGUAGE_JA=32;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMediaPlayers();
        Log.d(TAG,"@onCreate: Create media player");
        //use hardware volume key to control audio volume for all fragments under this activity
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //read from shared preference
        checkSP(true);
        if(GAME==null) {
            GAME = new Game('M');
            GAME.init(mGameLevel, mSP.getInt(SP_KEY_GAME_MODE, SP_KEY_GAME_MODE_DEFAULT),
                    parseGameRecordString(TAG, mSP.getString(SP_KEY_GAME_RECORD, SP_KEY_GAME_RECORD_DEFAULT)));
        }
        //get display language
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
            if (Locale.getDefault().getDisplayLanguage().equals(Locale.JAPANESE.getDisplayName())) {
                DISPLAY_LANGUAGE=LANGUAGE_JA;
            }else if(Locale.getDefault().getDisplayLanguage().equals(Locale.CHINESE.getDisplayName())) {
                if(Locale.getDefault().getDisplayCountry().equals(Locale.PRC.getDisplayCountry())) {
                    DISPLAY_LANGUAGE=LANGUAGE_ZH_PRC;
                }else {
                    DISPLAY_LANGUAGE=LANGUAGE_ZH_TW;
                }
            } }
        });
        t.start();

        int count=getFragmentManager().getBackStackEntryCount();
        for(int i=0;i<count;++i) {
            int backStackId = getFragmentManager().getBackStackEntryAt(i).getId();
            getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.d(TAG,"@onCreate clean "+backStackId+" on back stack");
        }
        //Do not need to add to back stack here, because the fragment being replaced is added to the back stack
        // (so in this case R.id.frag_container will be added to back stack if we call addBackStack)
        getFragmentManager().beginTransaction().add(R.id.frag_container, new GameFragment()).commit();
        //rate the app
        AppRater.appLaunched(this);
        try{t.join();}catch (InterruptedException e){e.printStackTrace();}
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
        getMediaPlayers();
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
        releaseMediaPlayers();

        if (MainActivity.GAME.getGameRecord(true)!=null){
            Log.d(TAG, "@onPause: write game record "+MainActivity.GAME.getGameRecord(true));
            mSP.edit().putString(MainActivity.SP_KEY_GAME_RECORD, MainActivity.parseGameRecordList(TAG, MainActivity.GAME.getGameRecord(true))).apply();
        }
    }


    private void getMediaPlayers(){
        Log.d(TAG,"Create media players");
        if(mMPClick ==null){

            mMPClick =MediaPlayer.create(getApplicationContext(), R.raw.click);
        }
        if(mMPStep==null){
            mMPStep= MediaPlayer.create(getApplicationContext(),R.raw.step);
        }
        if(mMPLose ==null){
            mMPLose =MediaPlayer.create(getApplicationContext(),R.raw.lose);
        }
        if(mMPWin==null){
            mMPWin=MediaPlayer.create(getApplicationContext(),R.raw.win);
        }
    }

    private void releaseMediaPlayers(){
        Log.d(TAG, "Release media player");
        //release media player
        if (mMPClick != null) {
            mMPClick.release();
            mMPClick = null;
        }
        if (mMPStep != null) {
            mMPStep.release();
            mMPStep = null;
        }
        if(mMPLose !=null){
            mMPLose.release();
            mMPLose =null;
        }
        if(mMPWin!=null) {
            mMPWin.release();
            mMPWin=null;
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


    /**
     *
     * @param tag of the activity request this method
     * @param enable play sound if enable is true
     * @param soundName sound name to play
     */
    public static void playSound(final String tag, final boolean enable,final String soundName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    Log.d(tag,"play sound");
                    MediaPlayer mMP;
                    switch (soundName) {
                        case "click":
                            mMP = mMPClick;
                            break;
                        case "step":
                            mMP = mMPStep;
                            break;
                        case "win":
                            mMP = mMPWin;
                            break;
                        case "lose":
                            mMP = mMPLose;
                            break;
                        default:
                            mMP = null;
                    }
                    //prevent from unexpected null pointer
                    if (mMP != null) {
                        if (mMP.isPlaying()) {
                            mMP.stop();
                        }
                        mMP.start();
                    }
                }
            }
        }
        ).start();
    }

}