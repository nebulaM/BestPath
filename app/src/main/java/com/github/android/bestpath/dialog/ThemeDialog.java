package com.github.android.bestpath.dialog;

import android.app.Dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.github.android.bestpath.MainActivity;
import com.github.android.bestpath.R;

/**
 * Created by nebulaM on 12/9/2016.
 * Dialog for changing theme
 */

public class ThemeDialog extends DialogFragment implements View.OnClickListener{
    public static final String TAG="ThemeDialog";
    private MyDialog.onCloseListener mOnCloseListener;
    private ImageView[] mTheme=new ImageView[2];
    private int selected;
    private SharedPreferences.Editor mSPEditor;
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
        for(ImageView clickable : mTheme){
            clickable.setOnClickListener(this);
        }
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        mSPEditor=getActivity().getSharedPreferences(MainActivity.SP_FILE_NAME, Context.MODE_PRIVATE).edit();
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_theme_circle_0:
                Log.d(TAG,"click on theme 0");
                selected=0;
                mSPEditor.putInt(MainActivity.SP_KEY_THEME,0).commit();
                this.dismiss();
                break;
            case R.id.dialog_theme_circle_1:
                Log.d(TAG,"click on theme 1");
                selected=1;
                mSPEditor.putInt(MainActivity.SP_KEY_THEME,1).commit();
                this.dismiss();
                break;
            default:
                break;
        }
    }

    public void setOnCloseListener(MyDialog.onCloseListener onCloseListener){
        mOnCloseListener=onCloseListener;
    }
    @Override
    public void onDismiss (DialogInterface dialog) {
        mOnCloseListener.onDialogClose(TAG,selected);
    }
}
