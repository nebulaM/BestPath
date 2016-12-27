package com.github.android.bestpath;


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

    private ImageView mResetPlayerButton;
    private ImageView mRestartButton;
    private ImageView mNextLevelButton;
    private ImageView mPreviousLevelButton;
    private ImageView mSettingsButton;

    private ImageView mDisableResetPlayerButton;

    private int mTheme;
    private Boolean mSound;
    private int mGameMode;

    private boolean mCheckDisableMask=false;

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
        mGameMode=mSP.getInt(MainActivity.SP_KEY_GAME_MODE,MainActivity.SP_KEY_GAME_MODE_DEFAULT);
        Log.d(TAG, "@onCreateView: args Theme "+mTheme+" Sound "+mSound+" Game Mode "+mGameMode);

        mGameDrawing=(GameDrawing)view.findViewById(R.id.GameDrawing);

        mGameFragmentContainer=(LinearLayout) view.findViewById(R.id.GameFragmentContainer);
        mResetPlayerButton =(ImageView)view.findViewById(R.id.ResetButton);
        mRestartButton=(ImageView)view.findViewById(R.id.RestartButton);
        mNextLevelButton=(ImageView)view.findViewById(R.id.NextLevelButton);
        mPreviousLevelButton=(ImageView)view.findViewById(R.id.PreviousLevelButton);
        mSettingsButton=(ImageView)view.findViewById(R.id.SettingButton);

        mDisableResetPlayerButton=(ImageView)view.findViewById(R.id.DisableResetButton);

        if(MainActivity.GAME.getGameMode()!=0){
            mDisableResetPlayerButton.setVisibility(View.VISIBLE);
        }else{
            mDisableResetPlayerButton.setVisibility(View.INVISIBLE);
        }

        //enable the flag to check if we should disable "reset player" after the user press one of the buttons:
        // new map, prev level, next level
        //this check should be performed once only, so disable this flag at the end of checkDisableButton();
        mCheckDisableMask=true;

        setGameTheme(mTheme);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mResetPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.GAME.getGameMode()!=0) {
                    mGameDrawing.resetPlayer(false);
                }else{
                    playSound(mSound);
                    mGameDrawing.resetPlayer(true);
                }
            }
        });

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.restart(mGameMode);
                checkDisableButton();
            }
        });
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.nextLevel(mGameMode);
                checkDisableButton();
            }
        });

        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(mSound);
                mGameDrawing.previousLevel(mGameMode);
                checkDisableButton();

            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playSound(mSound);
                while (mMP.isPlaying()) ;
                if (getView().getHeight() > getView().getWidth()) {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right).replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();

                }else {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_top, R.animator.slide_out_bottom, R.animator.slide_in_bottom, R.animator.slide_out_top).replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();

                }
            }

        });

        mGameDrawing.setOnPlayerMovingListener(this);

    }

    void checkDisableButton(){
        if(mCheckDisableMask){
            if(mGameMode==0) {
                mDisableResetPlayerButton.setVisibility(View.INVISIBLE);
            }else{
                mDisableResetPlayerButton.setVisibility(View.VISIBLE);
            }
            mCheckDisableMask=false;
        }
    }

    public void setGameTheme(int theme){
        switch (theme){
            case 0:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_dark);
                mGameDrawing.setThemeColor(R.color.theme_red,R.color.path_white);
                break;
            case 1:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_red);
                mGameDrawing.setThemeColor(R.color.theme_dark,R.color.path_green);
                break;
            case 2:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_grey);
                mGameDrawing.setThemeColor(R.color.theme_blue,R.color.path_white);
                break;
            case 3:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_blue);
                mGameDrawing.setThemeColor(R.color.theme_grey,R.color.path_orange);
                break;
            default:
                break;
        }
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
