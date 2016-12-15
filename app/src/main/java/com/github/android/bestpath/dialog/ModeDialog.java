package com.github.android.bestpath.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.github.android.bestpath.MainActivity;
import com.github.android.bestpath.R;

/**
 * Created by nebulaM on 12/14/2016.
 */

public class ModeDialog extends DialogFragment implements View.OnClickListener{
    public static final String TAG="ModeDialog";
    private MyDialog.onCloseListener mOnCloseListener;
    private ImageView[] mTheme=new ImageView[3];
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
        View view=inflater.inflate(R.layout.fragment_dialog_mode, null);
        //Setup clickable theme buttons
        mTheme[0]=(ImageView) view.findViewById(R.id.card_mode_easy);
        mTheme[1]=(ImageView) view.findViewById(R.id.card_mode_medium);
        mTheme[2]=(ImageView) view.findViewById(R.id.card_mode_hard);
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
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_mode_easy:
                selected=0;
                break;
            case R.id.card_mode_medium:
                selected=1;
                break;
            case R.id.card_mode_hard:
                selected=2;
                break;
            default:
                break;
        }
        Log.d(TAG,"click on mode "+selected);
        this.dismiss();
    }

    public void setOnCloseListener(MyDialog.onCloseListener onCloseListener){
        mOnCloseListener=onCloseListener;
    }
    @Override
    public void onDismiss (DialogInterface dialog) {
        mOnCloseListener.onDialogClose(TAG,selected);
    }
}

