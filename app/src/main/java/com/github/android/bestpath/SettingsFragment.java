package com.github.android.bestpath;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.android.bestpath.dialog.ModeDialog;
import com.github.android.bestpath.dialog.MyDialog;
import com.github.android.bestpath.dialog.RecordDialog;
import com.github.android.bestpath.dialog.ThemeDialog;
import com.github.android.bestpath.mediaPlayer.MediaPlayerSingleton;

public class SettingsFragment extends PreferenceFragment implements View.OnClickListener,MyDialog.onCloseListener{
    public static final String TAG="SettingsFragment";
    public static final String TAG_DIALOG_ON_BACK_STACK="dialog";
    //background
    private LinearLayout mSettingsSector1;
    private LinearLayout mSettingsSector2;
    private LinearLayout mSettingsSector3;
    //image modified from http://www.freepik.com, Designed by Milano83 / Freepik
    //images are used as button
    private ImageView mThemeImage;
    private ImageView mModeImage;
    private ImageView mSoundImage;
    private ImageView mRecordImage;
    private ImageView mThankImage;
    private ImageView mHelpImage;
    private ImageView mShareImage;
    private ImageView mRemoveAddsImage;
    //texts
    private TextView mModeText;
    private TextView mThemeText;
    private TextView mSoundText;
    private TextView mRecordText;
    private TextView mHelpText;

    private SharedPreferences mSP;
    private int mTheme;
    private Boolean mSound;
    private MediaPlayer mMP = MediaPlayerSingleton.getInstance();
    private Toast mToastSound;

    private int mGameMode;

    public interface onPreferenceChangeListener {
        void onPreferenceChange(String tag, int parameter);
    }

    public static SettingsFragment newInstance(int theme, boolean sound) {
        SettingsFragment myFragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt("theme", theme);
        args.putBoolean("sound", sound);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSP = getActivity().getSharedPreferences(MainActivity. SP_FILE_NAME, Context.MODE_PRIVATE);
        Log.d(TAG,"@onCreate: obtain media player from parent activity");
        mMP=MainActivity.mMPClick;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mTheme = mSP.getInt(MainActivity.SP_KEY_THEME, MainActivity.SP_KEY_THEME_DEFAULT);
        mSound = mSP.getBoolean(MainActivity.SP_KEY_SOUND, MainActivity.SP_KEY_SOUND_DEFAULT);
        mGameMode =mSP.getInt(MainActivity.SP_KEY_GAME_MODE,MainActivity.SP_KEY_GAME_MODE_DEFAULT);

        Log.d(TAG, "@onCreateView: args theme "+mTheme+" sound "+mSound+ "gameMode "+mGameMode);
        mModeImage=(ImageView)view.findViewById(R.id.ModeImage);
        mThemeImage=(ImageView)view.findViewById(R.id.ThemeColorImage);
        mSoundImage=(ImageView)view.findViewById(R.id.SoundImage);
        mRecordImage =(ImageView)view.findViewById(R.id.RecordImage);
        mThankImage =(ImageView)view.findViewById(R.id.ThankImage);
        mHelpImage=(ImageView)view.findViewById(R.id.HelpImage);
        mShareImage=(ImageView)view.findViewById(R.id.ShareImage);
        mRemoveAddsImage=(ImageView)view.findViewById(R.id.RemoveAddsImage);

        mModeText=(TextView)view.findViewById(R.id.ModeText);
        mThemeText=(TextView)view.findViewById(R.id.ThemeColorText);
        mSoundText=(TextView)view.findViewById(R.id.SoundText);
        mRecordText=(TextView)view.findViewById(R.id.RecordText);
        mHelpText=(TextView)view.findViewById(R.id.HelpText);

        //set color for each sector in this setting fragment
        mSettingsSector1=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L1);
        mSettingsSector2=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L2);
        mSettingsSector3=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L3);

        mModeText.setOnClickListener(this);
        mThemeText.setOnClickListener(this);
        mSoundText.setOnClickListener(this);
        mRecordText.setOnClickListener(this);
        mHelpText.setOnClickListener(this);
        //share image, not text
        mShareImage.setOnClickListener(this);


        mToastSound=Toast.makeText(getActivity().getApplicationContext(),"",Toast.LENGTH_SHORT);
        //IMPORTANT, initialize relevant things in this fragment
        init();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.settings);
        //((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    /**
     * listen to click and deal w/ different settings
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ThemeColorText:
                mThemeImage.setVisibility(View.VISIBLE);
                //getFragmentManager().popBackStack("GameFragment",POP_BACK_STACK_INCLUSIVE);<--this is a test on how back stack works
                //for some reason previously "new ThemeDialog()" was not working, had to use "ThemeDialog.newInstance()" instead
                ThemeDialog  dialog=new ThemeDialog();
                dialog.setOnCloseListener (this);
                dialog.show(getFragmentManager(), TAG_DIALOG_ON_BACK_STACK);
                break;
            case R.id.SoundText:
                mSound=!mSound;
                mSP.edit().putBoolean(MainActivity.SP_KEY_SOUND,mSound).apply();
                setSound(mSound,false);
                break;
            case R.id.ModeText:
                mModeImage.setVisibility(View.VISIBLE);
                ModeDialog modeDialog=ModeDialog.newInstance(mGameMode,mSound);
                modeDialog.setOnCloseListener (this);
                modeDialog.show(getFragmentManager(), TAG_DIALOG_ON_BACK_STACK);
                break;
            case R.id.RecordText:
                mRecordImage.setVisibility(View.VISIBLE);
                Log.d(TAG,"MainActivity.GAME.getGameRecord(): "+MainActivity.GAME.getGameRecord(false));
                RecordDialog recordDialog=RecordDialog.newInstance(MainActivity.GAME.getGameRecord(false));
                recordDialog.setOnCloseListener(this);
                recordDialog.show(getFragmentManager(), TAG_DIALOG_ON_BACK_STACK);
                break;

            case R.id.HelpText:
                mHelpImage.setVisibility(View.VISIBLE);
                if(getView().getHeight()>getView().getWidth()) {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right).replace(R.id.frag_container, new HelpFragment()).addToBackStack(TAG).commit();
                }else {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_top, R.animator.slide_out_bottom, R.animator.slide_in_bottom, R.animator.slide_out_top).replace(R.id.frag_container, new HelpFragment()).addToBackStack(TAG).commit();
                }
                break;

            case R.id.ShareImage:
                //TODO:custom share windows
                Uri uriToImage=Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.share);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                sendIntent.setType("image/png");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_app)));
                break;
            default:
                break;
        }
        playSound(mSound);
    }

    /**
     * Listen to dialog close event and get the parameter passed from dialog(which is selected by user)
     * @param tag name of the dialog being closed
     * @param parameter user selected option
     */
    @Override
    public void onDialogClose(String tag, int parameter){
        //parameter == -1 means close the dialog without clicking on any option
        if(parameter!=-1) {
            playSound(mSound);
        }
        switch (tag) {
            case ThemeDialog.TAG:
                if(parameter!=-1) {
                    Log.d(TAG, "@onDialogClose new theme is " + parameter);
                    if (parameter != mTheme) {
                        mTheme = parameter;
                        mSP.edit().putInt(MainActivity.SP_KEY_THEME,parameter).apply();
                        setSettingsTheme(parameter);
                    }
                }
                mThemeImage.setVisibility(View.INVISIBLE);
                break;
            case ModeDialog.TAG:
                if(parameter!=-1) {
                    if (parameter != mGameMode) {
                        mGameMode = parameter;
                        Log.d(TAG, "@onDialogClose new game mode is " + parameter);
                        mSP.edit().putInt(MainActivity.SP_KEY_GAME_MODE,parameter).apply();
                    }
                }
                mModeImage.setVisibility(View.INVISIBLE);
                break;
            case RecordDialog.TAG:
                mRecordImage.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


    /**
     * set theme(color) for this fragment
     * @param theme input
     */
    private void setSettingsTheme(int theme){
        switch (theme){
            case 0:
                //background color
                mSettingsSector1.setBackgroundResource(R.color.theme_dark);
                mSettingsSector2.setBackgroundResource(R.color.theme_red);
                mSettingsSector3.setBackgroundResource(R.color.theme_dark);
                //button color
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRecordImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThankImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            case 1:
                mSettingsSector1.setBackgroundResource(R.color.theme_red);
                mSettingsSector2.setBackgroundResource(R.color.theme_dark);
                mSettingsSector3.setBackgroundResource(R.color.theme_red);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRecordImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThankImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            case 2:
                mSettingsSector1.setBackgroundResource(R.color.theme_grey);
                mSettingsSector2.setBackgroundResource(R.color.theme_blue);
                mSettingsSector3.setBackgroundResource(R.color.theme_grey);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRecordImage.setImageResource(R.mipmap.ic_select_3_green);
                mThankImage.setImageResource(R.mipmap.ic_select_3_green);
                mHelpImage.setImageResource(R.mipmap.ic_select_3_green);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            case 3:
                mSettingsSector1.setBackgroundResource(R.color.theme_blue);
                mSettingsSector2.setBackgroundResource(R.color.theme_grey);
                mSettingsSector3.setBackgroundResource(R.color.theme_blue);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRecordImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThankImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            default:
                mSettingsSector1.setBackgroundResource(R.color.theme_dark);
                mSettingsSector2.setBackgroundResource(R.color.theme_red);
                mSettingsSector3.setBackgroundResource(R.color.theme_dark);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRecordImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThankImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
        }
    }


    /**
     *
     * @param sound sound on or off
     * @param init whether this is the first access this method in SettingsFragment
     *             (first time is when initialize SettingsFragment's view, so do not toast MSG)
     */
    private void setSound(boolean sound, boolean init){
        if(sound){
            if(!init) {
                mToastSound.setText(R.string.soundOn);
                mToastSound.show();
            }
            mSoundImage.setVisibility(View.VISIBLE);
        }else{
            if(!init) {
                mToastSound.setText(R.string.soundOff);
                mToastSound.show();
            }
            mSoundImage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * init this fragment's view w/ user preferenced settings
     */
    private void init(){
        setSettingsTheme(mTheme);
        setSound(mSound,true);
    }

    /**
     * sound effect method
     * @param enable play sound if enable == true
     */
    private void playSound(boolean enable){
        if(enable) {
            //Log.d(TAG,"play sound");
            //prevent from unexpected null pointer
            if(mMP !=null) {
                if (mMP.isPlaying()) {
                    mMP.stop();
                }
                mMP.start();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"@onResume: obtain pointer to media player from parent activity");
        mMP=MainActivity.mMPClick;

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"@onPause: release pointer to media player");
        mMP=null;
    }

}


