package com.nebulaM.android.bestpath.drawing;

import android.content.Context;
//import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.nebulaM.android.bestpath.R;
import com.nebulaM.android.bestpath.backend.Game;

/**
 * Created by nebulaM on 9/19/2016.
 */
public class GameDrawing extends View {
    private Game mGame;


    private float mEgdeLengthX;
    private float mEgdeLengthY;

    private Paint mPaint;
    private float mNodeLength;

    private float mGameRouteOffsetY;

    private float mEnergyBarW;
    private float mEnergyBarH;


    private Drawable mNode;

    private Path mPath;

    private float mLevel=10.0f;

    private final float mMaxLevel=20.0f;
    private final float mMinLevel=2.0f;

    private final int mEdgeProb=40;
    /**
     * @param context
     */
    public GameDrawing(Context context, AttributeSet attr) {
        super(context, attr);



        mGame =new Game((int)mLevel,mEdgeProb,'S');

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mPaint.setTextSize(50);

      //  mNode =context.getResources().getDrawable(R.drawable.node2d);
        mPath=new Path();


    }
    public void reset(){
        if(mGame!=null) {
            mGame.resetPlayer();
            invalidate();
        }
    }

    public void restart(){
        if(mGame!=null) {
            mGame.resetGame();
            invalidate();
        }
    }

    public void nextLevel(){
        if(mLevel<mMaxLevel) {
            mLevel += 1.0f;
            mGame=null;
            mGame = new Game((int) mLevel, mEdgeProb, 'S');
            invalidate();

        }
    }

    public void previousLevel(){
        if(mLevel>mMinLevel) {
            mLevel -= 1.0f;
            mGame=null;
            mGame = new Game((int) mLevel, mEdgeProb, 'S');
            invalidate();
        }
    }

   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) mWidth;
        int desiredHeight = (int) mHeight;

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if (widthMode == View.MeasureSpec.AT_MOST) {
            //wrap content
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mNodeLength=Math.min(getWidth(),getHeight())/(mLevel+(mLevel-1.0f)*0.8f);
        mEnergyBarW=getWidth()/2.0f;
        mEnergyBarH=getHeight()/20.0f;
        mGameRouteOffsetY=mEnergyBarH*1.2f;
        mEgdeLengthX=(getWidth()-mLevel*mNodeLength)/(mLevel-1.0f);
        mEgdeLengthY=(getHeight()-mLevel*mNodeLength-mEnergyBarH*1.2f)/(mLevel-1.0f);

            //draw nodes
            for (int i = 0; i < mGame.getNodeNum(); ++i) {
                /*int startX = (int) (mGame.getNodeXCord(i) * (mEgdeLengthX + mNodeLength));
                int startY = (int) (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEgdeLengthY + mNodeLength));
                drawDrawable(canvas, mNode, startX,startY,(int)(startX + mNodeLength),(int)(startY + mNodeLength));*/
                mPaint.setColor(0xffff7f27);
                float startX = (mGame.getNodeXCord(i) * (mEgdeLengthX + mNodeLength));
                float startY = (int) (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEgdeLengthY + mNodeLength));

                drawRoundRect(canvas,mPaint,mPath,startX,startY,(startX + mNodeLength),(startY + mNodeLength),mNodeLength/8.0f,mNodeLength/8.0f);


            }
            //draw edges(different cost has different color)
            for (int i = 0; i < mGame.getEdgeNum(); ++i) {
                if (mGame.getEdgeCost(i) == 1) {
                    mPaint.setColor(0xff3fff00);
                } else if (mGame.getEdgeCost(i) == 2) {
                    mPaint.setColor(0xfff0f000);
                } else {
                    mPaint.setColor(0xffb7161b);
                }
                if (mGame.getEdgeStartYCord(i) == mGame.getEdgeEndYCord(i)) {
                    float startX = (mGame.getEdgeStartXCord(i)) * (mEgdeLengthX + mNodeLength) + mNodeLength;
                    float startY = mGameRouteOffsetY +(mGame.getEdgeStartYCord(i) * (mEgdeLengthY + mNodeLength)) + mNodeLength/ 3.0f;
                    float endX = startX + mEgdeLengthX;
                    float endY = startY + mNodeLength/ 3.0f;
                    canvas.drawRect(startX, startY, endX, endY,mPaint);
                } else if (mGame.getEdgeStartXCord(i) == mGame.getEdgeEndXCord(i)) {
                    float startX = (mGame.getEdgeStartXCord(i)) * (mEgdeLengthX + mNodeLength) + mNodeLength/ 3.0f;
                    float startY = mGameRouteOffsetY +(mGame.getEdgeStartYCord(i) * (mEgdeLengthY + mNodeLength)) + mNodeLength;
                    float endX = startX + mNodeLength/ 3.0f;
                    float endY = startY + mEgdeLengthY;

                    canvas.drawRect(startX, startY, endX, endY,mPaint);
                }
            }

            //draw player
            // TODO:better shape and color
            int i=mGame.getPlayerPosition();
            mPaint.setColor(0xff3fff00);
            float startX = (mGame.getNodeXCord(i) * (mEgdeLengthX + mNodeLength));
            float startY = mGameRouteOffsetY +(mGame.getNodeYCord(i) * (mEgdeLengthY + mNodeLength));
            //canvas.drawRect(startX,startY,(startX + mNodeLength),(startY + mNodeLength),mPaint);
            drawRoundRect(canvas,mPaint,mPath,startX,startY,(startX + mNodeLength),(startY + mNodeLength),mNodeLength/8.0f,mNodeLength/8.0f);

            //draw energy bar
            float currentEnergy=(float)mGame.getPlayerEnergy();
            float maxEnergy=(float)mGame.getMaxEnergy();
            //draw shadow for the energy bar first
            mPaint.setColor(0xffa2a2a2);
            canvas.drawRect(mEnergyBarW*(currentEnergy/maxEnergy),0,mEnergyBarW,mEnergyBarH,mPaint);
            mPaint.setColor(0xffffff00);
            canvas.drawRect(0,0,mEnergyBarW*(currentEnergy/maxEnergy),mEnergyBarH,mPaint);
            mPaint.setColor(0xffffa448);
            canvas.drawText("Energy "+mGame.getMaxEnergy(),mEnergyBarW,mEnergyBarH,mPaint);

    }

    private void drawDrawable(Canvas canvas, Drawable draw, int startingX,
                              int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }


    private void drawRoundRect(Canvas canvas, Paint paint, Path path, float left, float top, float right, float bottom, float rx, float ry){
        path.reset();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        path.rLineTo(0, heightMinusCorners);


        path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        path.rLineTo(widthMinusCorners, 0);
        path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner


        path.rLineTo(0, -heightMinusCorners);

        path.close();
        canvas.drawPath(path, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //only interested in single touch
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                int isGameOver=mGame.gameOver();
                if(isGameOver==0) {
                    for (int i = 0; i < mGame.getNodeNum(); ++i) {
                        float startX = (mGame.getNodeXCord(i) * (mEgdeLengthX + mNodeLength));
                        float startY = mGameRouteOffsetY +(mGame.getNodeYCord(i) * (mEgdeLengthY + mNodeLength));
                        if (x > startX && x < startX + mNodeLength && y > startY && y < startY + mNodeLength) {
                            if (mGame.setPlayerPosition(i)){
                                invalidate();
                            }
                            break;
                        }
                    }
                }
                break;
        }
        return true;
    }
}

