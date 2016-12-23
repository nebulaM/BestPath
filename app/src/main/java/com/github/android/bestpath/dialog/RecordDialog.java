package com.github.android.bestpath.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

        mRecordImage =(ImageView)view.findViewById(R.id.dialog_effect_image);
        setRecordImage();
        mRecordImage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Pop up another detailed dialog
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

    private void setRecordImage(){
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
                mRecordImage.setImageResource(R.mipmap.record_1);
                break;
            case 2:
                mRecordImage.setImageResource(R.mipmap.record_2);
                break;
            case 3:
                mRecordImage.setImageResource(R.mipmap.record_3);
                break;
            case 4:
                mRecordImage.setImageResource(R.mipmap.record_4);
                break;
            case 5:
                mRecordImage.setImageResource(R.mipmap.record_5);
                break;
            case 6:
                mRecordImage.setImageResource(R.mipmap.record_6);
                break;
            default:
                mRecordImage.setImageResource(R.mipmap.record_0);
                break;
        }
    }
}
