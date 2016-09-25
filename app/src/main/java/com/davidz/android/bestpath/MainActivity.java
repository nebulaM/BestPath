package com.nebulaM.android.bestpath;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import com.nebulaM.android.bestpath.drawing.GameDrawing;

public class MainActivity extends AppCompatActivity {


    private GameDrawing mGameDrawing;
    private ImageButton mResetButton;
    private ImageButton mRestartButton;
    private ImageButton mNextLevelButton;
    private ImageButton mPreviousLevelButton;
    private ImageButton mSettingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameDrawing=(GameDrawing)this.findViewById(R.id.GameDrawing);

        mResetButton=(ImageButton)findViewById(R.id.ResetButton);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.reset();
            }
        });

        mRestartButton=(ImageButton)findViewById(R.id.RestartButton);
        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.restart();
            }
        });

        mNextLevelButton=(ImageButton)findViewById(R.id.NextLevelButton);
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.nextLevel();
            }
        });

        mPreviousLevelButton=(ImageButton)findViewById(R.id.PreviousLevelButton);
        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.previousLevel();
            }
        });

        mSettingsButton=(ImageButton)findViewById(R.id.SettingButton);

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PreferenceFragment newFragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.main_game_view, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }

        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }



    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundResource(R.drawable.gradient_background_cyan);
            getView().setClickable(true);
        }

    }
}