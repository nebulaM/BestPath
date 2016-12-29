package com.github.android.bestpath.dialog;

import android.app.Dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.github.android.bestpath.R;

/**
 * Created by nebulaM on 12/9/2016.
 * Dialog for changing theme
 */

public class ThemeDialog extends DialogFragment implements View.OnClickListener{
    public static final String TAG="ThemeDialog";
    private MyDialog.onCloseListener mOnCloseListener;
    private ImageView[] mTheme=new ImageView[4];
    //default -1 to indicate user close this dialog without giving any input
    private int selected=-1;
    /*public static ThemeDialog newInstance() {
        ThemeDialog frag = new ThemeDialog();
        return frag;
    }*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view=inflater.inflate(R.layout.fragment_dialog_theme, null);
        //Setup clickable theme buttons
        mTheme[0]=(ImageView) view.findViewById(R.id.dialog_theme_circle_0);
        mTheme[1]=(ImageView) view.findViewById(R.id.dialog_theme_circle_1);
        mTheme[2]=(ImageView) view.findViewById(R.id.dialog_theme_circle_2);
        mTheme[3]=(ImageView) view.findViewById(R.id.dialog_theme_circle_3);
        for(ImageView clickable : mTheme){
            clickable.setOnClickListener(this);
        }
        builder.setView(view)
                .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_theme_circle_0:
                selected=0;
                break;
            case R.id.dialog_theme_circle_1:
                selected=1;
                break;
            case R.id.dialog_theme_circle_2:
                selected=2;
                break;
            case R.id.dialog_theme_circle_3:
                selected=3;
                break;
            default:
                break;
        }
        //Log.d(TAG,"click on theme "+selected);
        this.dismiss();
    }

    public void setOnCloseListener(MyDialog.onCloseListener onCloseListener){
        mOnCloseListener=onCloseListener;
    }
    @Override
    public void onDismiss (DialogInterface dialog) {
        //without super.onDismiss(dialog), dismissed dialog may appear again after activity onResume
        super.onDismiss(dialog);
        mOnCloseListener.onDialogClose(TAG,selected);
    }
}
