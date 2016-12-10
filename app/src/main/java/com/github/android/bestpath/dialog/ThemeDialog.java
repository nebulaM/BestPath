package com.github.android.bestpath.dialog;

import android.app.Dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.github.android.bestpath.R;

/**
 * Created by nebulaM on 12/9/2016.
 */

public class ThemeDialog extends DialogFragment{
    public static final String TAG="ThemeDialog";
    private MyDialog.onCloseListener mOnCloseListener;

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
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    public void setOnCloseListener(MyDialog.onCloseListener onCloseListener){
        mOnCloseListener=onCloseListener;
    }
    @Override
    public void onDismiss (DialogInterface dialog){
        mOnCloseListener.onDialogClose(TAG);
    }

}
