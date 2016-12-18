package com.github.android.bestpath;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.android.bestpath.backend.Game;
import com.github.android.bestpath.mediaPlayer.MediaPlayerSingleton;



public class GameFragment extends Fragment implements GameDrawing.onPlayerMovingListener{
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
    private int mGameMode;

    private MediaPlayer mMP = MediaPlayerSingleton.getInstance();

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
        Log.d(TAG,"@onCreate: obtain media player from parent activity");
        mMP =MainActivity.mMPClick;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_game, container, false);

        mTheme = mSP.getInt(MainActivity.SP_KEY_THEME, MainActivity.SP_KEY_THEME_DEFAULT);
        mSound = mSP.getBoolean(MainActivity.SP_KEY_SOUND, MainActivity.SP_KEY_SOUND_DEFAULT);
        mLanguage = mSP.getString(MainActivity.SP_KEY_LANG, MainActivity.SP_KEY_LANG_PACKAGE[0]);
        mGameMode=mSP.getInt(MainActivity.SP_KEY_GAME_MODE,MainActivity.SP_KEY_GAME_MODE_DEFAULT);
        Log.d(TAG, "@onCreateView: args Theme "+mTheme+" Sound "+mSound+" Game Mode "+mGameMode);

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
                mGameDrawing.restart(mGameMode);
            }
        });
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.nextLevel(mGameMode);
            }
        });

        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.previousLevel(mGameMode);

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

        mGameDrawing.setOnPlayerMovingListener(this);

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
        Log.d(TAG,"@onResume: obtain media player from parent activity");
        mMP=MainActivity.mMPClick;

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"@onPause: release pointer to media player");
        mMP=null;
    }

    @Override
    public void onPlayerMoving(Game.GameState state){
        switch (state){
            case GAME_NOT_END:
                playSound(mSound);
                break;
            case PLAYER_WIN:
                break;
            case PLAYER_LOSE:
                break;
            default:
                break;
        }
    }
}
