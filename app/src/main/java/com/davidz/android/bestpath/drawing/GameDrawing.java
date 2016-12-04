package com.nebulaM.android.bestpath.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nebulaM.android.bestpath.R;
import com.nebulaM.android.bestpath.backend.Game;

import java.util.List;

/**
 * Created by nebulaM on 9/19/2016.
 * Main view of the game
 */
public class GameDrawing extends View {
    private final String TAG="GameDrawing";
    private Game mGame;
    //max level has 10*10 nodes
    private final float mMaxLevel=10.0f;
    //min level has 2*2 nodes
    private final float mMinLevel=2.0f;
    //default level has 5*5 nodes
    private float mLevel=5.0f;
    //probability of having edge, due to the way of implementation, 40 actually = ~80% probability of having an edge between two nodes
    private final int mEdgeProb=40;

    //draw shortest path if this flag = true
    private boolean mDrawShortestPathFlag=false;
    //rotation direction of energy view
    private boolean mClockwise=true;
    //parameters for donut-shaped energy view
    private RectF outerCircle;
    private RectF innerCircle;
    private RectF shadowRectF;
    //node-edge offset under energy view
    private float mGameRouteOffsetY;
    //edge/node dimensions
    private float mEdgeLengthX;
    private float mEdgeLengthY;
    private float mNodeLength;
    //canvas parameters
    private Paint mPaint;
    private Path mPath;
    //canvas will update all drawing parameters before drawing if the following flag is set to false
    private boolean mDrawingParametersReady=false;
    //facial expression for players
    private Drawable mPlayerNormal;
    private Drawable mPlayerTired;
    private Drawable mPlayerDrooling;
    private Drawable mPlayerWin;
    private Drawable mPlayerNotWin;

    private float mRadius;
    private float mDiameter;

    /**
     * @param context context
     */
    public GameDrawing(Context context, AttributeSet attr) {
        super(context, attr);
        mGame =new Game((int)mLevel,mEdgeProb,'M');

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mPaint.setTextSize(50);
        /*
        * Emoji provided free by http://emojione.com
        * */
        mPlayerNormal =context.getResources().getDrawable(R.drawable.neutral);
        mPlayerTired =context.getResources().getDrawable(R.drawable.tired);
        mPlayerDrooling=context.getResources().getDrawable(R.drawable.drooling);
        mPlayerWin=context.getResources().getDrawable(R.drawable.smile_normal);
        mPlayerNotWin=context.getResources().getDrawable(R.drawable.screming);
        mPath=new Path();
        outerCircle = new RectF();
        innerCircle = new RectF();
        shadowRectF = new RectF();
    }
    public void reset(){
        if(mGame!=null) {
            mGame.resetPlayer();
            this.clearDrawShortestPath();
            invalidate();
        }
    }

    public void restart(){
        if(mGame!=null) {
            mGame.resetGame();
            this.clearDrawShortestPath();
            invalidate();
        }
    }

    public void nextLevel(){
        if(mLevel<mMaxLevel) {
            mLevel += 1.0f;
            mGame=null;
            mGame = new Game((int) mLevel, mEdgeProb, 'M');
            //not ready to draw, need to re-calculate drawing parameters in canvas method
            this.notReadToDraw();
            invalidate();
        }
    }

    public void previousLevel(){
        if(mLevel>mMinLevel) {
            mLevel -= 1.0f;
            mGame=null;
            mGame = new Game((int) mLevel, mEdgeProb, 'M');
            this.notReadToDraw();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!mDrawingParametersReady) {
            mRadius = getHeight() / 16.0f;
            mDiameter = mRadius * 2;

            float startCord;
            float horizontalOffset = getWidth() / 2 - mRadius;
            startCord = .03f * mRadius;
            //left top right bottom
            outerCircle.set(startCord + horizontalOffset, startCord, mDiameter - startCord + horizontalOffset, mDiameter - startCord);
            startCord = .1f * mRadius;
            innerCircle.set(startCord + horizontalOffset, startCord, mDiameter - startCord + horizontalOffset, mDiameter - startCord);

            mGameRouteOffsetY = mDiameter * 1.2f;
            mNodeLength = Math.min(getWidth(), getHeight()) / (mLevel + (mLevel - 1.0f) * 0.8f);
            mEdgeLengthX = (getWidth() - mLevel * mNodeLength) / (mLevel - 1.0f);
            mEdgeLengthY = (getHeight() - mLevel * mNodeLength - mDiameter * 1.2f) / (mLevel - 1.0f);
        }
        mDrawingParametersReady=true;
        //draw energy view
        float currentEnergyPercent=(float)(mGame.getPlayerEnergy()*100/mGame.getMaxEnergy());
        //use different color for energy view depend on current energy(in percentage)
        if(currentEnergyPercent>30){
            // green
            mPaint.setColor(0xff38e100);//, 0xff38e100);
        } else if(currentEnergyPercent>15){
            // yellow
            mPaint.setColor(0xfff5c401);//,0xfff5c401);
        } else{
            //red
            mPaint.setColor(0xffb7161b);//,0xffb7161b);
        }
        //energy decreases clockwise
        if(mClockwise) {
            if(currentEnergyPercent==100.0f){
                drawDonut(canvas, mPaint, mPath,0.0f, 359.99f);
            }
            else if(currentEnergyPercent==0.0f){
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint, mPath, 0.0f, 359.99f);
            }
            else {
                drawDonut(canvas, mPaint, mPath, 270.0f - currentEnergyPercent * 3.60f, currentEnergyPercent * 3.60f);
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint, mPath, 270.0f, (100-currentEnergyPercent) * 3.60f);
            }
        } else{//energy decreases counterclockwise
            if(currentEnergyPercent==100.0f){
                drawDonut(canvas, mPaint, mPath,0.0f, 359.9f);
            }
            else if(currentEnergyPercent==0.0f){
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint, mPath,0.0f, 359.99f);
            }
            else {
                drawDonut(canvas, mPaint, mPath, 270.0f, currentEnergyPercent * 3.60f);
                mPaint.setColor(0xffa2a2a2);
                drawDonut(canvas, mPaint, mPath, 270.0f+currentEnergyPercent * 3.60f, (100.0f-currentEnergyPercent) * 3.60f);
            }
        }
        mPaint.setColor(0xffa2a2a2);
        if(mGame.getPlayerEnergy()>9) {
            canvas.drawText(Integer.toString(mGame.getPlayerEnergy()), getWidth() / 2 - mRadius * 0.4f, mRadius * 1.2f, mPaint);
        } else{
            canvas.drawText(" "+mGame.getPlayerEnergy(), getWidth() / 2 - mRadius * 0.4f, mRadius * 1.2f, mPaint);
        }

        //draw nodes
        mPaint.setColor(0xff984c15);
        for (int i = 0; i < mGame.getNodeNum(); ++i) {
            /*int startX = (int) (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            int startY = (int) (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
            drawDrawable(canvas, mNode, startX,startY,(int)(startX + mNodeLength),(int)(startY + mNodeLength));*/
            float startX = (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            float startY = (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
            drawRoundRect(canvas,mPaint,mPath,startX,startY,(startX + mNodeLength),(startY + mNodeLength),mNodeLength/8.0f,mNodeLength/8.0f);
        }
        //draw edges
        for (int i = 0; i < mGame.getEdgeNum(); ++i) {
            //different cost has different color
            if (mGame.getEdgeCost(i) == 1) {
                mPaint.setColor(0xff98fb98);
            } else if (mGame.getEdgeCost(i) == 2) {
                mPaint.setColor(0xffe1bc00);
            } else {
                mPaint.setColor(0xffffa089);
            }
            int startNodeID=mGame.getEdgeStartNode(i);
            int endNodeID=mGame.getEdgeEndNode(i);
            if ( mGame.getNodeYCord(startNodeID)==mGame.getNodeYCord(endNodeID)) {
                float startX = (mGame.getNodeXCord(startNodeID)) * (mEdgeLengthX + mNodeLength) + mNodeLength;
                float startY = mGameRouteOffsetY +(mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength)) + mNodeLength/ 3.0f;
                float endX = startX + mEdgeLengthX;
                float endY = startY + mNodeLength/ 3.0f;
                canvas.drawRect(startX, startY, endX, endY,mPaint);
            } else if (mGame.getNodeXCord(startNodeID) == mGame.getNodeXCord(endNodeID)) {
                float startX = (mGame.getNodeXCord(startNodeID) * (mEdgeLengthX + mNodeLength)) + mNodeLength/ 3.0f;
                float startY = mGameRouteOffsetY +(mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength)) + mNodeLength;
                float endX = startX + mNodeLength/ 3.0f;
                float endY = startY + mEdgeLengthY;
                canvas.drawRect(startX, startY, endX, endY,mPaint);
            } else{
                if (mGame.getNodeXCord(startNodeID) < mGame.getNodeXCord(endNodeID)) {
                    float startX = (mGame.getNodeXCord(startNodeID) * (mEdgeLengthX + mNodeLength))+mNodeLength;
                    float startY = (mGameRouteOffsetY +mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength))+mNodeLength;
                    float endX = (mGame.getNodeXCord(endNodeID) * (mEdgeLengthX + mNodeLength));
                    float endY = (mGameRouteOffsetY +mGame.getNodeYCord(endNodeID) * (mEdgeLengthY + mNodeLength));
                    drawDiagonal(canvas,mPaint,mPath, startX,startY,endX,endY,mEdgeLengthX,true);
                }
                else{
                    float startX = (mGame.getNodeXCord(startNodeID) * (mEdgeLengthX + mNodeLength));
                    float startY = (mGameRouteOffsetY +mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength))+mNodeLength;
                    float endX = (mGame.getNodeXCord(endNodeID) * (mEdgeLengthX + mNodeLength))+mNodeLength;
                    float endY = (mGameRouteOffsetY +mGame.getNodeYCord(endNodeID) * (mEdgeLengthY + mNodeLength));
                    drawDiagonal(canvas,mPaint,mPath,startX,startY,endX,endY,mEdgeLengthX,false);
                }
            }
        }
        Log.d(TAG,"NodeLength = "+mNodeLength);
        Log.d(TAG,"EdgeLengthX = "+mEdgeLengthX+" and EdgeLengthY = "+mEdgeLengthY);

        //draw player
        if(true) {//useless if statement, just want to locally define parameters i, startX and startY
            int i = mGame.getPlayerPosition();
            mPaint.setColor(0xff3fff00);
            float startX = (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            float startY = mGameRouteOffsetY + (mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
            if (mDrawShortestPathFlag) {
                drawDrawable(canvas, mPlayerNotWin, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else if (mGame.gameOver() == 1) {
                drawDrawable(canvas, mPlayerWin, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else if (currentEnergyPercent > 50) {
                drawDrawable(canvas, mPlayerNormal, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else if (currentEnergyPercent > 30) {
                drawDrawable(canvas, mPlayerTired, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else {
                drawDrawable(canvas, mPlayerDrooling, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            }
        }

        //TODO:predict if player can win or not
        //show one of the possible shortest paths if player not win
        if(mDrawShortestPathFlag){
            List<Integer> shortestPath=mGame.getShortestList();
            mPaint.setColor(0xff919191);
            float circleCenterOffset=mNodeLength/2.0f;
            for(int i=0;i<shortestPath.size();++i){
                float startX = (mGame.getNodeXCord(shortestPath.get(i)) * (mEdgeLengthX + mNodeLength));
                float startY = (mGameRouteOffsetY +mGame.getNodeYCord(shortestPath.get(i)) * (mEdgeLengthY + mNodeLength));
                float centerX=startX+circleCenterOffset;
                float centerY=startY+circleCenterOffset;
                canvas.drawCircle(centerX,centerY,mNodeLength/4.0f,mPaint);
            }
        }

    }

    /**
     * given boundaries, draw from source
     * @param canvas canvas to draw
     * @param draw  source to draw
     * @param startingX start x
     * @param startingY start y
     * @param endingX   end x
     * @param endingY   end y
     */
    private void drawDrawable(Canvas canvas, Drawable draw, int startingX,
                              int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    /*
    * http://stackoverflow.com/questions/5896234/how-to-use-android-canvas-to-draw-a-rectangle-with-only-topleft-and-topright-cor
    * */
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

    /**
     * Draw donut shape
     * @param canvas canvas to draw on
     * @param paint canvas paint
     * @param path canvas path
     * @param start start angle in degree
     * @param sweep draw how many degrees from start angle
     */
    private void drawDonut(Canvas canvas, Paint paint, Path path, float start,float sweep){
        path.reset();
        path.arcTo(outerCircle, start, sweep, false);
        path.arcTo(innerCircle, start+sweep, -sweep, false);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * Draw diagonal edge between two nodes
     * @param canvas canvas
     * @param paint paint
     * @param path path
     * @param startX x cord of the corner of start node
     * @param startY y cord of the corner of start node
     * @param endX x cord of the corner of end node
     * @param endY y cord of the corner of end node
     * @param width width of edge
     * @param LtoR  since in our implementation, node index increases as we go from left to right, from top to bottom
     *              define LtoR means "the edge from start node to end node" is "from left to right", which looks like this:
     *              start
     *                  \ \
     *                   \ \
     *                      end
     *              whereas RtoL(LtoR=false) looks like this:
     *                      start
     *                    / /
     *                   / /
     *                  end
     */
    private void drawDiagonal(Canvas canvas, Paint paint, Path path, float startX, float startY, float endX, float endY, float width, boolean LtoR){
        path.reset();
        float offset=width/4.0f;
        if(LtoR){
            path.moveTo(startX-offset,startY);
            path.quadTo(startX, startY, startX, startY-offset); //top-left corner of this edge, which is bottom-right corner of start node
            path.lineTo(endX+offset,endY);
            path.quadTo(endX,endY,endX,endY+offset); //bottom-right corner of this edge, which is top-left corner of end node
        }
        else{
            path.moveTo(startX,startY-offset);
            path.quadTo(startX,startY,startX+offset,startY); //top-right corner of this edge, which is left-bottom corner of start node
            path.lineTo(endX,endY+offset);
            path.quadTo(endX,endY,endX-offset,endY); //bottom-left corner of this edge, which is top-right corner of end node
        }
        path.close();
        canvas.drawPath(path, paint);
    }
    //request drawing of a shortest path(may have more than one shortest path but only show one)
    private void setDrawShortestPath(){
        mDrawShortestPathFlag=true;
    }

    private void clearDrawShortestPath(){mDrawShortestPathFlag=false;}

    private void notReadToDraw(){
        mDrawingParametersReady=false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //only interested in single touch
        float toleranceX=mEdgeLengthX*0.4f;
        float toleranceY=mEdgeLengthY*0.4f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if(mGame.gameOver()==0) {
                    for (int i = 0; i < mGame.getNodeNum(); ++i) {
                        float startX = (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
                        float startY = mGameRouteOffsetY + (mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));

                        if (x > (startX-toleranceX) && x < (startX + mNodeLength+toleranceX) && (y > startY-toleranceY) && (y < startY + mNodeLength+toleranceY)) {
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

