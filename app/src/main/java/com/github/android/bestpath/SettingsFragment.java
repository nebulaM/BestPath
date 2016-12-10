package com.github.android.bestpath;


import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.android.bestpath.dialog.MyDialog;
import com.github.android.bestpath.dialog.ThemeDialog;

public class SettingsFragment extends PreferenceFragment implements View.OnClickListener,MyDialog.onCloseListener{
    public static final String TAG="SettingsFragment";
    public static final String TAG_DIALOG_ON_BACK_STACK="dialog";
    private TextView mThemeText;
    //image modified from http://www.freepik.com, Designed by Milano83 / Freepik
    private ImageView mThemeImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mThemeText=(TextView)view.findViewById(R.id.ThemeColorText);
        mThemeImage=(ImageView)view.findViewById(R.id.ThemeColorImage);
        mThemeText.setOnClickListener(this);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.settings);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();



    }
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
            default:

                break;
        }
    }
    /**
     * Clear selected setting
     */
    @Override
    public void onDialogClose(String tag){
        switch (tag) {
            case ThemeDialog.TAG:
                mThemeImage.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //clear dialog, (without cleaning back stack, sometimes need to click dialog multiply times to close it after resumed from pause)
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_DIALOG_ON_BACK_STACK);
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

}


