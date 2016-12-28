package com.github.android.bestpath.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.android.bestpath.MainActivity;
import com.github.android.bestpath.R;

/**
 * Created by nebulaM on 12/14/2016.
 * Mode dialog
 */
//TODO:Bug in landscape view, need to implement onMeasure when rotating screen
public class ModeDialog extends DialogFragment implements View.OnClickListener{
    public static final String TAG="ModeDialog";
    private MyDialog.onCloseListener mOnCloseListener;
    private ImageView[] mCard =new ImageView[3];
    private float[] mLeftCardXY=new float[2];
    private float[] mRightCardXY=new float[2];
    private float[] mFrontCardXY=new float[2];

    //default -1 to indicate user close this dialog without giving any input
    private int selected=-1;

    private TranslateAnimation[] anim =new TranslateAnimation[3];

    private int mMode=0;
    private int mCCWAnimStep =0;
    private int mCWAnimStep =0;

    private boolean mSound;
    private Toast mToast;

    public static ModeDialog newInstance(int gameMode,boolean sound) {
        ModeDialog myFragment = new ModeDialog();
        Bundle args = new Bundle();
        args.putInt("gameMode", gameMode);
        args.putBoolean("sound", sound);
        myFragment.setArguments(args);
        return myFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode=getArguments().getInt("gameMode");
        mSound=getArguments().getBoolean("sound");
        mToast=Toast.makeText(getActivity().getApplicationContext(),"",Toast.LENGTH_SHORT);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view=inflater.inflate(R.layout.fragment_dialog_mode, null);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    //do something
                }
                return true;
            }
        });

        //Setup clickable
        mCard[0]=(ImageView) view.findViewById(R.id.card_left);
        mCard[1]=(ImageView) view.findViewById(R.id.card_right);
        mCard[2]=(ImageView) view.findViewById(R.id.card_front);

        setInitCard(mMode);


        mCard[0].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCard[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mLeftCardXY[0]=mCard[0].getX();
                mLeftCardXY[1]=mCard[0].getY();
            }
        });

        mCard[1].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCard[1].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mRightCardXY[0]=mCard[1].getX();
                mRightCardXY[1]=mCard[1].getY();
            }
        });


        mCard[2].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCard[2].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mFrontCardXY[0]=mCard[2].getX();
                mFrontCardXY[1]=mCard[2].getY();
            }
        });

        for(ImageView clickable: mCard){
            clickable.setOnClickListener(this);
        }

        builder.setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    selected=-1;
                    dialog.dismiss();
                }
            }).setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                selected=mMode;
                toastSelectMSG(mMode);
                dialog.dismiss();
            }});
        return builder.create();
    }


    @Override
    public void onClick(View view) {
        if(view.getY()>=mFrontCardXY[1]){
            return;
        }else if (view.getX() >= mRightCardXY[0]) {
                setCWAnimation(mCWAnimStep);
                mMode = (mMode == 2) ? 0 : mMode + 1;
                mCCWAnimStep = (mCCWAnimStep == 0) ? 2 : mCCWAnimStep - 1;
                mCWAnimStep = (mCWAnimStep == 2) ? 0 : mCWAnimStep + 1;
        }else {
                setCCWAnimation(mCCWAnimStep);
                mMode = (mMode == 0) ? 2 : mMode - 1;
                mCCWAnimStep = (mCCWAnimStep == 2) ? 0 : mCCWAnimStep + 1;
                mCWAnimStep = (mCWAnimStep == 0) ? 2 : mCWAnimStep - 1;
        }

        toastModeMSG(mMode);

        //Log.d(TAG,"click on game mode "+selected);
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


    private void setCWAnimation(int step) {
        MainActivity.playSound(TAG,mSound,"click");
        switch (step) {
            case 0:
                Log.d(TAG,"@setCWAnimation: set card case 0");
                mCard[1].setX(mFrontCardXY[0]);
                mCard[1].setY(mFrontCardXY[1]);
                mCard[2].setX(mLeftCardXY[0]);
                mCard[2].setY(mLeftCardXY[1]);
                mCard[0].setX(mRightCardXY[0]);
                mCard[0].setY(mRightCardXY[1]);
                break;
            case 1:
                Log.d(TAG,"@setCWAnimation: set card case 1");
                mCard[0].setX(mFrontCardXY[0]);
                mCard[0].setY(mFrontCardXY[1]);
                mCard[1].setX(mLeftCardXY[0]);
                mCard[1].setY(mLeftCardXY[1]);
                mCard[2].setX(mRightCardXY[0]);
                mCard[2].setY(mRightCardXY[1]);
                break;
            case 2:
                Log.d(TAG,"@setCWAnimation: set card case 2");
                mCard[2].setX(mFrontCardXY[0]);
                mCard[2].setY(mFrontCardXY[1]);
                mCard[0].setX(mLeftCardXY[0]);
                mCard[0].setY(mLeftCardXY[1]);
                mCard[1].setX(mRightCardXY[0]);
                mCard[1].setY(mRightCardXY[1]);
                break;
            default:
                break;
        }
    }
    //TODO:Animation
    private void setCCWAnimation(int step){
        /*float leftTOFrontX=mFrontCardXY[0]-mLeftCardXY[0];
        float rightToLeftX=mLeftCardXY[0]-mRightCardXY[0];
        float frontToRightX= mRightCardXY[0]-mFrontCardXY[0];

        float leftTOFrontY=mFrontCardXY[1]-mLeftCardXY[1];
        float rightToLeftY=mLeftCardXY[1]-mRightCardXY[1];
        float frontToRightY= mRightCardXY[1]-mFrontCardXY[1];
        Log.d(TAG,"x0:  "+(mFrontCardXY[0])+" y0: "+(mFrontCardXY[1]));
        Log.d(TAG,"x1:  "+(mLeftCardXY[0])+" y1: "+(mLeftCardXY[1]));
        Log.d(TAG,"x2:  "+(mRightCardXY[0])+" y2: "+(mRightCardXY[1]));
        switch_sound (animStep) {
            case 0:
                //card 0 at left
                anim[0] = new TranslateAnimation(0,leftTOFrontX , 0, leftTOFrontY);
                anim[1] = new TranslateAnimation(0,rightToLeftX, 0, rightToLeftY);
                anim[2] = new TranslateAnimation(0,frontToRightX, 0,frontToRightY );
                break;
            case 1:
                //card 0 in the front
                anim[0] = new TranslateAnimation(0, frontToRightX,0, frontToRightY);
                anim[1] = new TranslateAnimation(0, leftTOFrontX, 0, leftTOFrontY);
                anim[2] = new TranslateAnimation(0, rightToLeftX, 0, rightToLeftY);
                break;
            case 2:
                //card 0 at right
                anim[0] = new TranslateAnimation(0, rightToLeftX, 0, rightToLeftY);
                anim[1] = new TranslateAnimation(0, frontToRightX, 0,  frontToRightY);
                anim[2] = new TranslateAnimation(0, leftTOFrontX, 0, leftTOFrontY);
                break;
            default:
                break;
        }
        for(TranslateAnimation anim : this.anim) {
            anim.setRepeatMode(0);
            anim.setDuration(500);
            anim.setFillAfter(true);
        }
        for(int i = 0; i< mCard.length; ++i){
                mCard[i].startAnimation(anim[i]);
        }*/
        MainActivity.playSound(TAG,mSound,"click");
        switch (step) {
            case 0:
                mCard[0].setX(mFrontCardXY[0]);
                mCard[0].setY(mFrontCardXY[1]);
                mCard[1].setX(mLeftCardXY[0]);
                mCard[1].setY(mLeftCardXY[1]);
                mCard[2].setX(mRightCardXY[0]);
                mCard[2].setY(mRightCardXY[1]);
                break;
            case 1:
                mCard[1].setX(mFrontCardXY[0]);
                mCard[1].setY(mFrontCardXY[1]);
                mCard[2].setX(mLeftCardXY[0]);
                mCard[2].setY(mLeftCardXY[1]);
                mCard[0].setX(mRightCardXY[0]);
                mCard[0].setY(mRightCardXY[1]);
                break;
            case 2:
                mCard[2].setX(mFrontCardXY[0]);
                mCard[2].setY(mFrontCardXY[1]);
                mCard[0].setX(mLeftCardXY[0]);
                mCard[0].setY(mLeftCardXY[1]);
                mCard[1].setX(mRightCardXY[0]);
                mCard[1].setY(mRightCardXY[1]);
               break;
            default:
                break;
        }
    }

    /**
     * Card 2 is initially at the center, set card 2 corresponding to the input mode
     * @param currentMode 0 easy
     *                    1 medium
     *                    2 hard
     */
    private void setInitCard(int currentMode){
        switch (currentMode){
            case 0:
                mCard[0].setImageResource(R.mipmap.card_hard);
                mCard[1].setImageResource(R.mipmap.card_medium);
                mCard[2].setImageResource(R.mipmap.card_easy);
                break;
            case 1:
                mCard[0].setImageResource(R.mipmap.card_easy);
                mCard[1].setImageResource(R.mipmap.card_hard);
                mCard[2].setImageResource(R.mipmap.card_medium);
                break;
            case 2:
                mCard[0].setImageResource(R.mipmap.card_medium);
                mCard[1].setImageResource(R.mipmap.card_easy);
                mCard[2].setImageResource(R.mipmap.card_hard);
                break;
            default:
                break;
        }
    }

    private void toastModeMSG(int mode){
        switch (mode){
            case 0:
                mToast.setText(R.string.easy);
                break;
            case 1:
                mToast.setText(R.string.medium);
                break;
            case 2:
                mToast.setText(R.string.hard);
                break;
            default:
                break;
        }
        mToast.show();
    }
    private void toastSelectMSG(int mode){
        String selectString;
        if(MainActivity.DISPLAY_LANGUAGE==MainActivity.LANGUAGE_EN){
            selectString=" "+getText(R.string.selectModeNotify).toString();
        }else{
            selectString=getText(R.string.selectModeNotify).toString();
        }
        switch (mode){
            case 0:
                mToast.setText(getText(R.string.easy)+selectString);
                break;
            case 1:
                mToast.setText(getText(R.string.medium)+selectString);
                break;
            case 2:
                mToast.setText(getText(R.string.hard)+selectString);
                break;
            default:
                break;
        }
        mToast.show();
    }

}

