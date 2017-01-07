package com.github.android.bestpath;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.android.bestpath.backend.Game;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;;


public class tsb extends Fragment implements cgs.onPlayerMovingListener {
    public static final String TAG="tsb";
    private SharedPreferences mSP;
    private cgs mCgs;

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

    /*public static tsb newInstance(int theme, boolean sound, String language) {
        tsb myFragment = new tsb();
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
        mGameMode=mSP.getInt(MainActivity.SP_KEY_GAME_MODE,MainActivity.SP_KEY_GAME_MODE_DEFAULT);
        //Log.d(TAG, "@onCreateView: args Theme "+mTheme+" Sound "+mSound+" Game Mode "+mGameMode);

        mCgs =(cgs)view.findViewById(R.id.GameDrawing);

        mGameFragmentContainer=(LinearLayout) view.findViewById(R.id.GameFragmentContainer);
        mResetPlayerButton =(ImageView)view.findViewById(R.id.ResetButton);
        mRestartButton=(ImageView)view.findViewById(R.id.RestartButton);
        mNextLevelButton=(ImageView)view.findViewById(R.id.NextLevelButton);
        mPreviousLevelButton=(ImageView)view.findViewById(R.id.PreviousLevelButton);
        mSettingsButton=(ImageView)view.findViewById(R.id.SettingButton);

        mDisableResetPlayerButton=(ImageView)view.findViewById(R.id.DisableResetButton);
        //blink settings button if user never opened "help" before
        if( mSP.getBoolean(MainActivity.SP_KEY_NEVER_OPENED_HELP,false)){
            //http://stackoverflow.com/questions/4852281/android-how-can-i-make-a-button-flash/4852468#4852468
            final Animation animation = new AlphaAnimation(1.0f, 0f); // Change alpha from fully visible to invisible
            animation.setDuration(400);
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            mSettingsButton.setAnimation(animation);
        }


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
                    mCgs.resetPlayer(false);
                }else{
                    MainActivity.playSound(TAG,mSound,"click");
                    mCgs.resetPlayer(true);
                }
            }
        });

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound(TAG,mSound,"click");
                mCgs.restart(mGameMode);
                checkDisableButton();
            }
        });
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound(TAG,mSound,"click");
                mCgs.nextLevel(mGameMode);
                checkDisableButton();
            }
        });

        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound(TAG,mSound,"click");
                mCgs.previousLevel(mGameMode);
                checkDisableButton();

            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.playSound(TAG,mSound,"click");
                if (getView().getHeight() > getView().getWidth()) {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right).replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();

                }else {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_top, R.animator.slide_out_bottom, R.animator.slide_in_bottom, R.animator.slide_out_top).replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();

                }
            }

        });

        mCgs.setOnPlayerMovingListener(this);

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
                mCgs.setThemeColor(R.color.theme_red,R.color.path_white);
                break;
            case 1:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_red);
                mCgs.setThemeColor(R.color.theme_dark,R.color.path_green);
                break;
            case 2:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_grey);
                mCgs.setThemeColor(R.color.theme_blue,R.color.path_white);
                break;
            case 3:
                mGameFragmentContainer.setBackgroundResource(R.color.theme_blue);
                mCgs.setThemeColor(R.color.theme_grey,R.color.path_orange);
                break;
            default:
                break;
        }
    }



    @Override
    public void onPlayerMoving(Game.GameState state){
        switch (state){
            case GAME_NOT_END:
                MainActivity.playSound(TAG,mSound,"step");
                break;
            case PLAYER_WIN:
                MainActivity.playSound(TAG,mSound,"win");
                break;
            case PLAYER_LOSE:
                MainActivity.playSound(TAG,mSound,"lose");
                break;
            default:
                break;
        }
    }

}
