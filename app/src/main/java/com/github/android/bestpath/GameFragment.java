package com.github.android.bestpath;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.android.bestpath.R;
import com.github.android.bestpath.drawing.GameDrawing;

public class GameFragment extends Fragment {
    private final String TAG="GameFragment";
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
        mRestartButton=(ImageButton)view.findViewById(R.id.RestartButton);
        mNextLevelButton=(ImageButton)view.findViewById(R.id.NextLevelButton);
        mPreviousLevelButton=(ImageButton)view.findViewById(R.id.PreviousLevelButton);
        mSettingsButton=(ImageButton)view.findViewById(R.id.SettingButton);



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
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add THIS fragment to the back stack(NOT new fragment)
                transaction.replace(R.id.frag_container, new SettingsFragment()).addToBackStack(TAG).commit();
            }

        });

    }
}
