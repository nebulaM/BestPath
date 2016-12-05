package com.github.android.bestpath;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.android.bestpath.R;
import com.github.android.bestpath.drawing.GameDrawing;

public class GameFragment extends Fragment {
    private GameDrawing mGameDrawing;
    private ImageButton mResetButton;
    private ImageButton mRestartButton;
    private ImageButton mNextLevelButton;
    private ImageButton mPreviousLevelButton;
    private ImageButton mSettingsButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_game, container, false);
        mGameDrawing=(GameDrawing)view.findViewById(R.id.GameDrawing);

        mResetButton=(ImageButton)view.findViewById(R.id.ResetButton);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.reset();
            }
        });

        mRestartButton=(ImageButton)view.findViewById(R.id.RestartButton);
        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.restart();
            }
        });

        mNextLevelButton=(ImageButton)view.findViewById(R.id.NextLevelButton);
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.nextLevel();
            }
        });

        mPreviousLevelButton=(ImageButton)view.findViewById(R.id.PreviousLevelButton);
        mPreviousLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameDrawing.previousLevel();
            }
        });

        mSettingsButton=(ImageButton)view.findViewById(R.id.SettingButton);

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PreferenceFragment newFragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.frag_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }

        });

        return view;
    }
}
