/*
 * Copyright (C) 2017 by nebulaM <nebulam12@gmail.com>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.android.bestpath;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.android.bestpath.dialog.ModeDialog;
import com.github.android.bestpath.dialog.MyDialog;
import com.github.android.bestpath.dialog.RecordDialog;
import com.github.android.bestpath.dialog.ThemeDialog;

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
    private TextView mThankText;
    private TextView mRemoveAddsText;

    private SharedPreferences mSP;
    private int mTheme;
    private Boolean mSound;
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
        //Log.d(TAG,"@onCreate: obtain media player from parent activity");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mTheme = mSP.getInt(MainActivity.SP_KEY_THEME, MainActivity.SP_KEY_THEME_DEFAULT);
        mSound = mSP.getBoolean(MainActivity.SP_KEY_SOUND, MainActivity.SP_KEY_SOUND_DEFAULT);
        mGameMode =mSP.getInt(MainActivity.SP_KEY_GAME_MODE,MainActivity.SP_KEY_GAME_MODE_DEFAULT);

        //Log.d(TAG, "@onCreateView: args theme "+mTheme+" sound "+mSound+ "gameMode "+mGameMode);
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
        mThankText=(TextView)view.findViewById(R.id.ThankText);
        mRemoveAddsText=(TextView)view.findViewById(R.id.RemoveAddsText);

        //set color for each sector in this setting fragment
        mSettingsSector1=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L1);
        mSettingsSector2=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L2);
        mSettingsSector3=(LinearLayout)view.findViewById(R.id.SettingsSectorContainer_L3);

        mModeText.setOnClickListener(this);
        mThemeText.setOnClickListener(this);
        mSoundText.setOnClickListener(this);
        mRecordText.setOnClickListener(this);
        mHelpText.setOnClickListener(this);
        mThankText.setOnClickListener(this);
        mRemoveAddsText.setOnClickListener(this);
        //share image, not text
        mShareImage.setOnClickListener(this);

        //blink "help" if user never opened "help" before
        if( mSP.getBoolean(MainActivity.SP_KEY_NEVER_OPENED_HELP,false)) {
            //http://stackoverflow.com/questions/4852281/android-how-can-i-make-a-button-flash/4852468#4852468
            final Animation animation = new AlphaAnimation(1.0f, 0f); // Change alpha from fully visible to invisible
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            mHelpText.setAnimation(animation);
        }

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
                //Log.d(TAG,"MainActivity.GAME.getGameRecord(): "+MainActivity.GAME.getGameRecord(false));
                RecordDialog recordDialog=RecordDialog.newInstance(MainActivity.GAME.getGameRecord(false));
                recordDialog.setOnCloseListener(this);
                recordDialog.show(getFragmentManager(), TAG_DIALOG_ON_BACK_STACK);
                break;

            case R.id.HelpText:
                mHelpImage.setVisibility(View.VISIBLE);
                if(getView().getHeight()>getView().getWidth()) {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left).replace(R.id.frag_container, new HelpFragment()).addToBackStack(TAG).commit();
                }else {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_top, R.animator.slide_out_bottom).replace(R.id.frag_container, new HelpFragment()).addToBackStack(TAG).commit();
                }
                break;
            case R.id.ThankText:
                mThankImage.setVisibility(View.VISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.thank_title)
                        .setMessage(R.string.thank_text)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.RemoveAddsText:
                mRemoveAddsImage.setVisibility(View.VISIBLE);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setTitle(R.string.more_features_title)
                        .setMessage(R.string.more_features_text)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            default:
                break;
        }
        MainActivity.playSound(TAG,mSound,"click");
        //share is special because it fires another activity, do not play music
        if(v.getId()==R.id.ShareImage){
            //TODO:custom share windows
            Uri uriToImage=Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.share);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            sendIntent.setType("image/png");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_app)));
        }
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
            MainActivity.playSound(TAG,mSound,"click");
        }
        switch (tag) {
            case ThemeDialog.TAG:
                if(parameter!=-1) {
                    //Log.d(TAG, "@onDialogClose new theme is " + parameter);
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
                        //Log.d(TAG, "@onDialogClose new game mode is " + parameter);
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
                mModeImage.setImageResource(R.drawable.ic_select_1_cyan);
                mThemeImage.setImageResource(R.drawable.ic_select_1_cyan);
                mSoundImage.setImageResource(R.drawable.ic_select_1_cyan);
                mRecordImage.setImageResource(R.drawable.ic_select_3_yellow);
                mThankImage.setImageResource(R.drawable.ic_select_3_yellow);
                mHelpImage.setImageResource(R.drawable.ic_select_3_yellow);
                mRemoveAddsImage.setImageResource(R.drawable.ic_select_1_cyan);
                break;
            case 1:
                mSettingsSector1.setBackgroundResource(R.color.theme_red);
                mSettingsSector2.setBackgroundResource(R.color.theme_dark);
                mSettingsSector3.setBackgroundResource(R.color.theme_red);
                mModeImage.setImageResource(R.drawable.ic_select_1_cyan);
                mThemeImage.setImageResource(R.drawable.ic_select_1_cyan);
                mSoundImage.setImageResource(R.drawable.ic_select_1_cyan);
                mRecordImage.setImageResource(R.drawable.ic_select_3_yellow);
                mThankImage.setImageResource(R.drawable.ic_select_3_yellow);
                mHelpImage.setImageResource(R.drawable.ic_select_3_yellow);
                mRemoveAddsImage.setImageResource(R.drawable.ic_select_1_cyan);
                break;
            case 2:
                mSettingsSector1.setBackgroundResource(R.color.theme_grey);
                mSettingsSector2.setBackgroundResource(R.color.theme_blue);
                mSettingsSector3.setBackgroundResource(R.color.theme_grey);
                mModeImage.setImageResource(R.drawable.ic_select_1_black);
                mThemeImage.setImageResource(R.drawable.ic_select_1_black);
                mSoundImage.setImageResource(R.drawable.ic_select_1_black);
                mRecordImage.setImageResource(R.drawable.ic_select_3_green);
                mThankImage.setImageResource(R.drawable.ic_select_3_green);
                mHelpImage.setImageResource(R.drawable.ic_select_3_green);
                mRemoveAddsImage.setImageResource(R.drawable.ic_select_1_black);
                break;
            case 3:
                mSettingsSector1.setBackgroundResource(R.color.theme_blue);
                mSettingsSector2.setBackgroundResource(R.color.theme_grey);
                mSettingsSector3.setBackgroundResource(R.color.theme_blue);
                mModeImage.setImageResource(R.drawable.ic_select_1_black);
                mThemeImage.setImageResource(R.drawable.ic_select_1_black);
                mSoundImage.setImageResource(R.drawable.ic_select_1_black);
                mRecordImage.setImageResource(R.drawable.ic_select_3_green);
                mThankImage.setImageResource(R.drawable.ic_select_3_green);
                mHelpImage.setImageResource(R.drawable.ic_select_3_green);
                mRemoveAddsImage.setImageResource(R.drawable.ic_select_1_black);
                break;
            default:
                mSettingsSector1.setBackgroundResource(R.color.theme_dark);
                mSettingsSector2.setBackgroundResource(R.color.theme_red);
                mSettingsSector3.setBackgroundResource(R.color.theme_dark);
                mModeImage.setImageResource(R.drawable.ic_select_1_cyan);
                mThemeImage.setImageResource(R.drawable.ic_select_1_cyan);
                mSoundImage.setImageResource(R.drawable.ic_select_1_cyan);
                mRecordImage.setImageResource(R.drawable.ic_select_3_yellow);
                mThankImage.setImageResource(R.drawable.ic_select_3_yellow);
                mHelpImage.setImageResource(R.drawable.ic_select_3_yellow);
                mRemoveAddsImage.setImageResource(R.drawable.ic_select_1_cyan);
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
     * init this fragment's view w/ user preferred settings
     */
    private void init(){
        setSettingsTheme(mTheme);
        setSound(mSound,true);
    }
}


