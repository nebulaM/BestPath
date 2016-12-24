package com.github.android.bestpath.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.github.android.bestpath.R;

import java.util.ArrayList;
import java.util.List;


public class RecordDialog extends DialogFragment {
    public static final String TAG="RecordDialog";
    private MyDialog.onCloseListener mOnCloseListener;
    private ImageView mRecordImage;

    private ArrayList<Integer> mRecordList;

    public static RecordDialog newInstance(List recordList) {
        RecordDialog myFragment = new RecordDialog();
        Bundle args = new Bundle();
        args.putIntegerArrayList("record",new ArrayList<>(recordList));
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordList=getArguments().getIntegerArrayList("record");
        Log.d(TAG,"@onCreate: mRecordList is "+mRecordList);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_dialog_record, null);
        builder.setView(view).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.detail, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //TODO:Pop up another detailed dialog
            }});
        //http://stackoverflow.com/questions/4852281/android-how-can-i-make-a-button-flash/4852468#4852468
        final Animation animation = new AlphaAnimation(1.0f, 0.6f); // Change alpha from fully visible to invisible
        animation.setDuration(2000);
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in


        mRecordImage =(ImageView)view.findViewById(R.id.dialog_effect_image);
        if(setRecordAnimation()) {
            AnimationDrawable frameAnimation = (AnimationDrawable) mRecordImage.getDrawable();
            frameAnimation.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run(){
                    mRecordImage.startAnimation(animation);
                }

            },frameAnimation.getNumberOfFrames()*frameAnimation.getDuration(0));
        }else {
            mRecordImage.startAnimation(animation);
        }
        mRecordImage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Pop up another detailed dialog
                mRecordImage.clearAnimation();

            }
        }));


        return builder.create();
    }

    public void setOnCloseListener(MyDialog.onCloseListener onCloseListener){
        mOnCloseListener=onCloseListener;
    }
    @Override
    public void onDismiss (DialogInterface dialog) {
        //without super.onDismiss(dialog), dismissed dialog may appear again after activity onResume
        super.onDismiss(dialog);
        mOnCloseListener.onDialogClose(TAG,-1);
    }

    private boolean setRecordAnimation(){
        int count=0;
        for(int i=mRecordList.size()/2;i<mRecordList.size();++i){
            if(mRecordList.get(i)>=3){
                count++;
            }else{
                break;
            }
        }
        switch (count){
            case 1:
                mRecordImage.setImageResource(R.drawable.record_animation_1);
                break;
            case 2:
                mRecordImage.setImageResource(R.drawable.record_animation_2);
                break;
            case 3:
                mRecordImage.setImageResource(R.drawable.record_animation_3);
                break;
            case 4:
                mRecordImage.setImageResource(R.drawable.record_animation_4);
                break;
            case 5:
                mRecordImage.setImageResource(R.drawable.record_animation_5);
                break;
            case 6:
                mRecordImage.setImageResource(R.drawable.record_animation_6);
                break;
            default:
                mRecordImage.setImageResource(R.drawable.record_0);
                return false;
        }
        return true;
    }
}
