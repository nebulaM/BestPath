package com.nebulaM.android.bestpath.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nebulaM.android.bestpath.backend.Game;

import java.util.List;

/**
 * Created by nebulaM on 9/19/2016.
 */
public class GameDrawing extends View {
    private Game mGame;

    private float mEdgeLengthX;
    private float mEdgeLengthY;

    private Paint mPaint;
    private float mNodeLength;

    private float mGameRouteOffsetY;

    private Path mPath;

    private float mLevel=5.0f;

    private final float mMaxLevel=20.0f;
    private final float mMinLevel=2.0f;

    private final int mEdgeProb=40;

    private boolean mDrawShortestPathFlag;

    private float mRadius;
    private float mDiameter;
    private boolean mClockwise=true;
    RectF outerCircle;
    RectF innerCircle;
    RectF shadowRectF;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRadius=getHeight()/16.0f;
        mDiameter=mRadius*2;
        outerCircle = new RectF();
        innerCircle = new RectF();
        shadowRectF = new RectF();
        float startCord;
        float horizontalOffset=getWidth()/2-mRadius;
        startCord = .03f * mRadius;
        //left top right bottom
        outerCircle.set(startCord+horizontalOffset, startCord, mDiameter-startCord+horizontalOffset, mDiameter-startCord);
        startCord = .3f * mRadius;
        innerCircle.set(startCord+horizontalOffset, startCord, mDiameter-startCord+horizontalOffset, mDiameter-startCord);

        mGameRouteOffsetY= mDiameter*1.2f;
        mNodeLength=Math.min(getWidth(),getHeight())/(mLevel+(mLevel-1.0f)*0.8f);
        mEdgeLengthX =(getWidth()-mLevel*mNodeLength)/(mLevel-1.0f);
        mEdgeLengthY =(getHeight()-mLevel*mNodeLength-mDiameter*1.2f)/(mLevel-1.0f);
        //draw energy view
        float currentEnergyPercent=(float)(mGame.getPlayerEnergy()*100/mGame.getMaxEnergy());
        if(currentEnergyPercent>30){
            // green
            mPaint.setColor(0xff38e100);//, 0xff38e100);
        }
        else if(currentEnergyPercent>15){
            // yellow
            mPaint.setColor(0xfff5c401);//,0xfff5c401);
        }
        else{
            //red
            mPaint.setColor(0xffb7161b);//,0xffb7161b);
        }
        if(mClockwise) {
            if(currentEnergyPercent==100.0f){
                drawDonut(canvas, mPaint,0.0f, 359.99f);
            }
            else if(currentEnergyPercent==0.0f){
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint,0.0f, 359.99f);
            }
            else {
                drawDonut(canvas, mPaint, 270.0f - currentEnergyPercent * 3.60f, currentEnergyPercent * 3.60f);
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint, 270.0f, (100-currentEnergyPercent) * 3.60f);
            }
        }
        else{
            if(currentEnergyPercent==100.0f){
                drawDonut(canvas, mPaint,0.0f, 359.9f);
            }
            else if(currentEnergyPercent==0.0f){
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint,0.0f, 359.99f);
            }
            else {
                drawDonut(canvas, mPaint, 270.0f, currentEnergyPercent * 3.60f);
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint, 270.0f+currentEnergyPercent * 3.60f, (100.0f-currentEnergyPercent) * 3.60f);
            }
        }
        mPaint.setColor(0xffa2a2a2);
        if(mGame.getPlayerEnergy()>9) {
            canvas.drawText(Integer.toString(mGame.getPlayerEnergy()), getWidth() / 2 - mRadius * 0.4f, mRadius * 1.2f, mPaint);
        }
        else{
            canvas.drawText(" "+mGame.getPlayerEnergy(), getWidth() / 2 - mRadius * 0.4f, mRadius * 1.2f, mPaint);
        }

        //draw nodes
        for (int i = 0; i < mGame.getNodeNum(); ++i) {
            /*int startX = (int) (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            int startY = (int) (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
            drawDrawable(canvas, mNode, startX,startY,(int)(startX + mNodeLength),(int)(startY + mNodeLength));*/
            mPaint.setColor(0xffff7f27);
            float startX = (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            float startY = (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
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
                float startX = (mGame.getEdgeStartXCord(i)) * (mEdgeLengthX + mNodeLength) + mNodeLength;
                float startY = mGameRouteOffsetY +(mGame.getEdgeStartYCord(i) * (mEdgeLengthY + mNodeLength)) + mNodeLength/ 3.0f;
                float endX = startX + mEdgeLengthX;
                float endY = startY + mNodeLength/ 3.0f;
                canvas.drawRect(startX, startY, endX, endY,mPaint);
            } else if (mGame.getEdgeStartXCord(i) == mGame.getEdgeEndXCord(i)) {
                float startX = (mGame.getEdgeStartXCord(i)) * (mEdgeLengthX + mNodeLength) + mNodeLength/ 3.0f;
                float startY = mGameRouteOffsetY +(mGame.getEdgeStartYCord(i) * (mEdgeLengthY + mNodeLength)) + mNodeLength;
                float endX = startX + mNodeLength/ 3.0f;
                float endY = startY + mEdgeLengthY;

                canvas.drawRect(startX, startY, endX, endY,mPaint);
            }
        }

        //draw player
        // TODO:better shape and color
        int i=mGame.getPlayerPosition();
        mPaint.setColor(0xff3fff00);
        float startX = (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
        float startY = mGameRouteOffsetY +(mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
        //canvas.drawRect(startX,startY,(startX + mNodeLength),(startY + mNodeLength),mPaint);
        drawRoundRect(canvas,mPaint,mPath,startX,startY,(startX + mNodeLength),(startY + mNodeLength),mNodeLength/8.0f,mNodeLength/8.0f);


        //draw shortestPath if player lose
        if(mDrawShortestPathFlag){
            List<Integer> shortestPath=mGame.getShortestList();
            for(i=0;i<shortestPath.size();++i){
                mPaint.setColor(0xffff6c6c);
                startX = (mGame.getNodeXCord(shortestPath.get(i)) * (mEdgeLengthX + mNodeLength));
                startY = (mGameRouteOffsetY +mGame.getNodeYCord(shortestPath.get(i)) * (mEdgeLengthY + mNodeLength));
                drawRoundRect(canvas,mPaint,mPath,startX,startY,(startX + mNodeLength),(startY + mNodeLength),mNodeLength/8.0f,mNodeLength/8.0f);
            }
            mDrawShortestPathFlag=false;
        }

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


    public void drawDonut(Canvas canvas, Paint paint, float start,float sweep){
        mPath.reset();
        mPath.arcTo(outerCircle, start, sweep, false);
        mPath.arcTo(innerCircle, start+sweep, -sweep, false);
        mPath.close();
        canvas.drawPath(mPath, paint);
    }

    private void setDrawShortestPath(){
        mDrawShortestPathFlag=true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //only interested in single touch
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if(mGame.gameOver()==0) {
                    for (int i = 0; i < mGame.getNodeNum(); ++i) {
                        float startX = (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
                        float startY = mGameRouteOffsetY + (mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
                        if (x > startX && x < startX + mNodeLength && y > startY && y < startY + mNodeLength) {
                            if (mGame.setPlayerPosition(i)) {
                                invalidate();
                            } else {
                                if (mGame.gameOver(i) == -1) {
                                    setDrawShortestPath();
                                    invalidate();
                                }
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

