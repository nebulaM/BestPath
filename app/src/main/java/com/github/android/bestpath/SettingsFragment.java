package com.github.android.bestpath;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.android.bestpath.dialog.MyDialog;
import com.github.android.bestpath.dialog.ThemeDialog;
import com.github.android.bestpath.mediaPlayer.MediaPlayerSingleton;

import java.io.IOException;

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
    private ImageView mEffectImage;
    private ImageView mLanguageImage;
    private ImageView mHelpImage;
    private ImageView mShareImage;
    private ImageView mRemoveAddsImage;
    //texts
    private TextView mThemeText;
    private TextView mSoundText;

    private SharedPreferences mSP;
    private int mTheme;
    private Boolean mSound;
    private String mLanguage;
    //sound from http://www.freesfx.co.uk
    private MediaPlayer mMP = MediaPlayerSingleton.getInstance();
    private Toast mToastSound;

    public interface onPreferenceChangeListener {
        void onPreferenceChange(String tag, int parameter);
    }

    public static SettingsFragment newInstance(int theme, boolean sound, String language) {
        SettingsFragment myFragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt("theme", theme);
        args.putBoolean("sound", sound);
        args.putString("language", language);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSP = getActivity().getSharedPreferences(MainActivity. SP_FILE_NAME, Context.MODE_PRIVATE);

        mMP =MediaPlayer.create(getActivity().getApplicationContext(),R.raw.click_1);
        Log.d(TAG,"Create media player on Create");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mTheme = mSP.getInt(MainActivity.SP_KEY_THEME, MainActivity.SP_KEY_THEME_DEFAULT);
        mSound = mSP.getBoolean(MainActivity.SP_KEY_SOUND, MainActivity.SP_KEY_SOUND_DEFAULT);
        mLanguage = mSP.getString(MainActivity.SP_KEY_LANG, MainActivity.SP_KEY_LANG_PACKAGE[0]);


        mModeImage=(ImageView)view.findViewById(R.id.ModeImage);
        mThemeImage=(ImageView)view.findViewById(R.id.ThemeColorImage);
        mSoundImage=(ImageView)view.findViewById(R.id.SoundImage);
        mEffectImage=(ImageView)view.findViewById(R.id.EffectImage);
        mLanguageImage=(ImageView)view.findViewById(R.id.LanguageImage);
        mHelpImage=(ImageView)view.findViewById(R.id.HelpImage);
        mShareImage=(ImageView)view.findViewById(R.id.ShareImage);
        mRemoveAddsImage=(ImageView)view.findViewById(R.id.RemoveAddsImage);

        mThemeText=(TextView)view.findViewById(R.id.ThemeColorText);
        mSoundText=(TextView)view.findViewById(R.id.SoundText);

        //set color for each sector in this setting fragment
        mSettingsSector1=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L1);
        mSettingsSector2=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L2);
        mSettingsSector3=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L3);

        mThemeText.setOnClickListener(this);
        mSoundText.setOnClickListener(this);
        mToastSound=Toast.makeText(getActivity().getApplicationContext(),"",Toast.LENGTH_SHORT);
        //IMPORTANT, initialize relevant things in this fragment
        init();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.settings);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    /**
     * listen to click and deal w/ different settings
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ThemeColorText:
                Log.d(TAG,"click theme");
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
                    Log.d(TAG, "theme dialog closed and user selected a theme");
                    Log.d(TAG, "theme is " + parameter);
                    if (parameter != mTheme) {
                        mTheme = parameter;
                        setSettingsTheme(parameter);

                    }
                }
                mThemeImage.setVisibility(View.INVISIBLE);
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
                mEffectImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mLanguageImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mShareImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            case 1:
                mSettingsSector1.setBackgroundResource(R.color.theme_red);
                mSettingsSector2.setBackgroundResource(R.color.theme_dark);
                mSettingsSector3.setBackgroundResource(R.color.theme_red);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mEffectImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mLanguageImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mShareImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            case 2:
                mSettingsSector1.setBackgroundResource(R.color.theme_grey);
                mSettingsSector2.setBackgroundResource(R.color.theme_blue);
                mSettingsSector3.setBackgroundResource(R.color.theme_grey);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mEffectImage.setImageResource(R.mipmap.ic_select_3_green);
                mLanguageImage.setImageResource(R.mipmap.ic_select_3_green);
                mHelpImage.setImageResource(R.mipmap.ic_select_3_green);
                mShareImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            case 3:
                mSettingsSector1.setBackgroundResource(R.color.theme_blue);
                mSettingsSector2.setBackgroundResource(R.color.theme_grey);
                mSettingsSector3.setBackgroundResource(R.color.theme_blue);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mEffectImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mLanguageImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mShareImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
            default:
                mSettingsSector1.setBackgroundResource(R.color.theme_dark);
                mSettingsSector2.setBackgroundResource(R.color.theme_red);
                mSettingsSector3.setBackgroundResource(R.color.theme_dark);
                mModeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mThemeImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mSoundImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mEffectImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mLanguageImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mHelpImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mShareImage.setImageResource(R.mipmap.ic_select_1_cyan);
                mRemoveAddsImage.setImageResource(R.mipmap.ic_select_1_cyan);
                break;
        }
    }

    private void setLanguage(String language){

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
        setLanguage(mLanguage);
    }

    /**
     * sound effect method
     * @param enable play sound if enable == true
     */
    private void playSound(boolean enable){
        if(enable) {
            Log.d(TAG,"play sound");
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
        if(mMP ==null){
            Log.d(TAG,"Create media player on Resume");
            mMP =MediaPlayer.create(getActivity().getApplicationContext(), R.raw.click_1);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //clear dialog, (without cleaning back stack, sometimes need to click dialog multiply times to close it after resumed from pause)
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_DIALOG_ON_BACK_STACK);
        if (prev != null) {
            Log.d(TAG,"close opened dialog on Pause");
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
        //release media player
        if(mMP !=null) {
            Log.d(TAG,"Release media player on Pause");
            mMP.release();
            mMP = null;
        }
    }

}


