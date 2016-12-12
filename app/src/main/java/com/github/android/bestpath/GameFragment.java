package com.github.android.bestpath;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.android.bestpath.drawing.GameDrawing;

public class GameFragment extends Fragment{
    public static final String TAG="GameFragment";
    private SharedPreferences mSP;
    private GameDrawing mGameDrawing;

    private LinearLayout mGameFragmentContainer;

    private ImageButton mResetButton;
    private ImageButton mRestartButton;
    private ImageButton mNextLevelButton;
    private ImageButton mPreviousLevelButton;
    private ImageButton mSettingsButton;

    private int mTheme;
    private Boolean mSound;
    private String mLanguage;

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
        mResetButton=(ImageButton)view.findViewById(R.id.ResetButton);
        mRestartButton=(ImageButton)view.findViewById(R.id.RestartButton);
        mNextLevelButton=(ImageButton)view.findViewById(R.id.NextLevelButton);
        mPreviousLevelButton=(ImageButton)view.findViewById(R.id.PreviousLevelButton);
        mSettingsButton=(ImageButton)view.findViewById(R.id.SettingButton);
        init();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.reset();
            }
        });

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.restart();
            }
        });
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.nextLevel();
            }
        });

        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.previousLevel();
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getFragmentManager().beginTransaction().replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();
            }

        });

    }

    public void setGameTheme(int theme){
        switch (theme){
            case 0:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_dark);
                break;
            case 1:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_red);
                break;
            case 2:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_grey);
                break;
            case 3:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_blue);
                break;
            default:
                break;
        }
    }


    private void init() {
        setGameTheme(mTheme);
    }
}
