package com.github.android.bestpath;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.android.bestpath.mediaPlayer.MediaPlayerSingleton;


public class GameFragment extends Fragment{
    public static final String TAG="GameFragment";
    private SharedPreferences mSP;
    private GameDrawing mGameDrawing;

    private LinearLayout mGameFragmentContainer;

    private ImageView mResetButton;
    private ImageView mRestartButton;
    private ImageView mNextLevelButton;
    private ImageView mPreviousLevelButton;
    private ImageView mSettingsButton;

    private int mTheme;
    private Boolean mSound;
    private String mLanguage;

    private MediaPlayer mMP = MediaPlayerSingleton.getInstance();
    private Uri  path_click_settings;

    /*public static GameFragment newInstance(int theme, boolean sound, String language) {
        GameFragment myFragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt("theme", theme);
        args.putBoolean("sound", sound);
        args.putString("language", language);
        myFragment.setArguments(args);
        return myFragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSP = getActivity().getSharedPreferences(MainActivity. SP_FILE_NAME, Context.MODE_PRIVATE);

        mMP =MediaPlayer.create(getActivity().getApplicationContext(), R.raw.click_1);
        path_click_settings = Uri.parse("android.resource://" + getActivity().getPackageName()+ "/" + R.raw.click_1);
        Log.d(TAG,"Create media player on Create, string"+path_click_settings);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_game, container, false);

        mTheme = mSP.getInt(MainActivity.SP_KEY_THEME, MainActivity.SP_KEY_THEME_DEFAULT);
        mSound = mSP.getBoolean(MainActivity.SP_KEY_SOUND, MainActivity.SP_KEY_SOUND_DEFAULT);
        mLanguage = mSP.getString(MainActivity.SP_KEY_LANG, MainActivity.SP_KEY_LANG_PACKAGE[0]);
        Log.d(TAG, "args theme "+mTheme+" sound "+mSound+" language "+mLanguage);

        mGameDrawing=(GameDrawing)view.findViewById(R.id.GameDrawing);

        mGameFragmentContainer=(LinearLayout) view.findViewById(R.id.GameFragmentContainer);
        mResetButton=(ImageView)view.findViewById(R.id.ResetButton);
        mRestartButton=(ImageView)view.findViewById(R.id.RestartButton);
        mNextLevelButton=(ImageView)view.findViewById(R.id.NextLevelButton);
        mPreviousLevelButton=(ImageView)view.findViewById(R.id.PreviousLevelButton);
        mSettingsButton=(ImageView)view.findViewById(R.id.SettingButton);
        init();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.reset();
            }
        });

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.restart();
            }
        });
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.nextLevel();
            }
        });

        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.previousLevel();

            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playSound(mSound);
                while(mMP.isPlaying());
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right,R.animator.slide_out_left,R.animator.slide_in_left,R.animator.slide_out_right).replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();
            }

        });

    }

    public void setGameTheme(int theme){
        switch (theme){
            case 0:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_dark);
                mGameDrawing.setNodeColor(R.color.theme_red);
                break;
            case 1:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_red);
                mGameDrawing.setNodeColor(R.color.theme_dark);
                break;
            case 2:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_grey);
                mGameDrawing.setNodeColor(R.color.theme_blue);
                break;
            case 3:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_blue);
                mGameDrawing.setNodeColor(R.color.theme_grey);
                break;
            default:
                break;
        }
    }


    private void init() {
        setGameTheme(mTheme);
    }


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
        //release media player
        if(mMP !=null) {
            Log.d(TAG,"Release media player on Pause");
            mMP.release();
            mMP = null;
        }
    }
}
