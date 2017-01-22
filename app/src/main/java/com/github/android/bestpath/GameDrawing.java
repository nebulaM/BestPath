/*
 * Copyright (C) 2017 by nebulaM <nebulam12@gmail.com>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.android.bestpath;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.github.android.bestpath.backend.Game;
import java.util.List;
import static com.github.android.bestpath.MainActivity.GAME;

/**
 * Main view of the game
 */
public class GameDrawing extends View {
    private final String TAG="GameDrawing";
    private Game mGame;
    //max level has 10*10 nodes
    private final float mMaxLevel=MainActivity.GAME_LEVEL_MAX;
    //min level has 2*2 nodes
    private final float mMinLevel=MainActivity.SP_KEY_GAME_LEVEL_DEFAULT;
    //current game level
    private float mLevel;

    private Drawable mGameEasy;
    private Drawable mGameNormal;
    private Drawable mGameHard;


    //rotation direction of energy view
    private boolean mClockwise=true;
    //parameters for donut-shaped energy view
    private RectF outerCircle;
    private RectF innerCircle;
    private float mGameRouteOffsetX;
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

    private Context mContext;
    private int mNodeColor=0xff000000;
    private int mPathColor=0xffffffff;
    private int mTextColor=0xffffffff;

    private final int mGoldColor=0xffffdf00;
    private final int mGreyColor=0xffa2a2a2;

    private onPlayerMovingListener mOnPlayerMovingListener;

    private boolean mShowGameMode =true;
    private Toast mToastMSG;

    private int mStageCleared;
    private boolean mShowStageCleared=true;

    private float mEnergyTextLt10X;
    private float mEnergyTextGt10X;
    private float mEnergyTextY;
    private float mStageX;
    private float mStageTextY;
    private float mStageNumberTextY;

    public interface onPlayerMovingListener{
        void onPlayerMoving(Game.GameState state);
    }
    /**
     * @param context context
     */
    public GameDrawing(Context context, AttributeSet attr) {
        super(context, attr);
        mContext=context;
        //mGame points to the game stored in main activity
        mGame= GAME;
        mLevel=mGame.getGameLevel();
        mToastMSG = Toast.makeText(context.getApplicationContext(),"",Toast.LENGTH_SHORT);

        mStageCleared=mGame.getStageCleared();

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mPaint.setTextSize(40);
        /*
        * Emoji provided free by http://emojione.com
        * */
        mGameEasy= ContextCompat.getDrawable(context,R.drawable.icon_mode_easy);
        mGameNormal= ContextCompat.getDrawable(context,R.drawable.icon_mode_medium);
        mGameHard= ContextCompat.getDrawable(context,R.drawable.icon_mode_hard);
        mPlayerNormal = ContextCompat.getDrawable(context,R.drawable.neutral_black);
        mPlayerTired = ContextCompat.getDrawable(context,R.drawable.tired_black);
        mPlayerDrooling= ContextCompat.getDrawable(context,R.drawable.drooling_black);
        mPlayerWin= ContextCompat.getDrawable(context,R.drawable.smile_normal_black);
        mPlayerNotWin= ContextCompat.getDrawable(context,R.drawable.screming_black);
        mPath=new Path();
        outerCircle = new RectF();
        innerCircle = new RectF();
        notReadToDraw();
    }

    public void resetPlayer(boolean enable){
        if(enable) {
            if (mGame != null) {
                mGame.resetPlayer();
                mStageCleared=mGame.getStageCleared();
                mToastMSG.setText(R.string.reset_player);
                mToastMSG.show();
                notReadToDraw();
                invalidate();
            }
        }else{
            mToastMSG.setText(R.string.cannot_reset_player);
            mToastMSG.show();
        }
    }

    public void restart(int gameMode){
        if(mGame!=null) {
            mGame.init((int)mLevel,gameMode,null);
            mStageCleared=mGame.getStageCleared();
            mToastMSG.setText(R.string.reset_game);
            mToastMSG.show();
            notReadToDraw();
            invalidate();
        }
    }

    public void nextLevel(int gameMode){
        if(mLevel<mMaxLevel) {
            mLevel += 1.0f;
            mGame.init((int) mLevel, gameMode,null);
            mStageCleared=mGame.getStageCleared();
            mToastMSG.setText("Lv "+String.valueOf((int)(mLevel-MainActivity.SP_KEY_GAME_LEVEL_DEFAULT+1.0f)));
            mToastMSG.show();
            //not ready to draw, need to re-calculate drawing parameters in canvas method
            notReadToDraw();
            invalidate();
        }else{
            mToastMSG.setText(R.string.max_level);
            mToastMSG.show();
        }
    }

    public void previousLevel(int gameMode){
        if(mLevel>mMinLevel) {
            mLevel -= 1.0f;
            mGame.init((int) mLevel, gameMode,null);
            mStageCleared=mGame.getStageCleared();
            mToastMSG.setText("Lv "+String.valueOf((int)(mLevel-MainActivity.SP_KEY_GAME_LEVEL_DEFAULT+1.0f)));
            mToastMSG.show();
            notReadToDraw();
            invalidate();
        }else{
            mToastMSG.setText(R.string.min_level);
            mToastMSG.show();
        }
    }

    public void setThemeColor(int nodeColor,int pathColor){
        mNodeColor=ContextCompat.getColor(mContext, nodeColor);
        mPathColor=ContextCompat.getColor(mContext, pathColor);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //disable this for drawing problem after fragment transaction
        //if(!mDrawingParametersReady) {
            if(getWidth()<getHeight()) {

                mRadius = getHeight() / 16.0f;
                mDiameter = mRadius * 2;
                float side = Math.min(getHeight() - mDiameter * 1.2f, getWidth());
                float startCord;
                float horizontalOffset = getWidth() / 2 - mRadius;
                startCord = .03f * mRadius;
                //left top right bottom
                outerCircle.set(startCord + horizontalOffset, startCord, mDiameter - startCord + horizontalOffset, mDiameter - startCord);
                startCord = .1f * mRadius;
                innerCircle.set(startCord + horizontalOffset, startCord, mDiameter - startCord + horizontalOffset, mDiameter - startCord);


                mNodeLength = side / (mLevel + (mLevel - 1.0f) * 0.8f);
                mEdgeLengthX = (side - mLevel * mNodeLength) / (mLevel - 1.0f);
                mEdgeLengthY = mEdgeLengthX;

                mGameRouteOffsetY = mDiameter * 1.2f;
                mGameRouteOffsetX = (getWidth() - (mNodeLength * (mLevel) + mEdgeLengthX * (mLevel - 1))) / 2;

                mEnergyTextLt10X= getWidth() / 2 - mRadius * 0.4f;
                mEnergyTextGt10X=getWidth() / 2 - mRadius * 0.2f;
                mEnergyTextY=mRadius * 1.2f;

                mStageX=getWidth()-mDiameter*1.6f;
                mStageTextY=mDiameter*0.4f;
                mStageNumberTextY=mDiameter*0.8f;
            }else{
                mRadius = getWidth() / 16.0f;
                mDiameter = mRadius * 2;
                float side = Math.min(getHeight() , getWidth()- mDiameter * 1.2f);
                float startCord;
                float verticalOffset = getHeight() / 2 - mRadius;
                startCord = .03f * mRadius;
                //left top right bottom
                outerCircle.set(startCord , startCord+ verticalOffset, mDiameter - startCord , mDiameter - startCord+ verticalOffset);
                startCord = .1f * mRadius;
                innerCircle.set(startCord , startCord+ verticalOffset, mDiameter - startCord , mDiameter - startCord+ verticalOffset);
                mNodeLength = side / (mLevel + (mLevel - 1.0f) * 0.8f);
                mEdgeLengthY = (side - mLevel * mNodeLength) / (mLevel - 1.0f);
                mEdgeLengthX = mEdgeLengthY;
                mGameRouteOffsetY = 0;
                mGameRouteOffsetX = (getWidth() - (mNodeLength * (mLevel) + mEdgeLengthX * (mLevel - 1))) / 2;

                mEnergyTextLt10X=mRadius*0.6f;
                mEnergyTextGt10X=mRadius*0.8f;
                mEnergyTextY=getHeight() / 2+mRadius * 0.2f;

                mStageX=0;
                mStageTextY=getHeight()-mDiameter*0.8f;
                mStageNumberTextY=getHeight()-mDiameter*0.4f;
            }
        //}
        mDrawingParametersReady=true;
        //draw mode icon
        if(mShowGameMode) {
            switch (mGame.getGameMode()) {
                case 0:
                    drawDrawable(canvas, mGameEasy, 0, 0, (int) mDiameter, (int) mDiameter);
                    break;
                case 1:
                    drawDrawable(canvas, mGameNormal, 0, 0, (int) mDiameter, (int) mDiameter);
                    break;
                case 2:
                    drawDrawable(canvas, mGameHard, 0, 0, (int) mDiameter, (int) mDiameter);
                    break;
                default:
                    break;
            }
        }

        //draw energy view
        float currentEnergyPercent=(float)(mGame.getPlayerEnergy()*100/mGame.getMaxEnergy());
        //use different color for energy view depend on current energy(in percentage)
        if(currentEnergyPercent>30){
            // green
            mPaint.setColor(0xff38e100);
        } else if(currentEnergyPercent>15){
            // yellow
            mPaint.setColor(0xfff5c401);
        } else{
            //red
            mPaint.setColor(0xffb7161b);
        }
        //energy decreases clockwise
        if(mClockwise) {
            if(currentEnergyPercent==100.0f){
                drawDonut(canvas, mPaint, mPath,0.0f, 359.99f);
            } else if(currentEnergyPercent==0.0f){
                mPaint.setColor(mGreyColor);
                drawDonut(canvas, mPaint, mPath, 0.0f, 359.99f);
            } else {
                drawDonut(canvas, mPaint, mPath, 270.0f - currentEnergyPercent * 3.60f, currentEnergyPercent * 3.60f);
                mPaint.setColor(mGreyColor);
                drawDonut(canvas, mPaint, mPath, 270.0f, (100-currentEnergyPercent) * 3.60f);
            }
        } else{//energy decreases counterclockwise
            if(currentEnergyPercent==100.0f){
                drawDonut(canvas, mPaint, mPath,0.0f, 359.9f);
            } else if(currentEnergyPercent==0.0f){
                mPaint.setColor(mGreyColor);
                drawDonut(canvas, mPaint, mPath,0.0f, 359.99f);
            } else {
                drawDonut(canvas, mPaint, mPath, 270.0f, currentEnergyPercent * 3.60f);
                mPaint.setColor(mGreyColor);
                drawDonut(canvas, mPaint, mPath, 270.0f+currentEnergyPercent * 3.60f, (100.0f-currentEnergyPercent) * 3.60f);
            }
        }
        //draw energy text
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mRadius*0.6f );
        if(mGame.getPlayerEnergy()>9) {
                canvas.drawText(Integer.toString(mGame.getPlayerEnergy()), mEnergyTextLt10X, mEnergyTextY, mPaint);
        } else{
                canvas.drawText(Integer.toString(mGame.getPlayerEnergy()), mEnergyTextGt10X,mEnergyTextY ,  mPaint);
        }
        //draw stage clear
        if(mShowStageCleared) {
            mPaint.setColor(mTextColor);
            canvas.drawText(getResources().getString(R.string.stage_clear), mStageX, mStageTextY, mPaint);
            if (mStageCleared != Game.NOT_SHOW_GAME_STAGE) {
                String space = "        ";//8 space
                int temp = mStageCleared;
                while (temp > 9) {
                    if (!space.isEmpty()) {
                        space = space.substring(1);
                    } else {
                        break;
                    }
                    temp = temp / 10;
                }
                canvas.drawText(space + Integer.toString(mStageCleared), mStageX, mStageNumberTextY, mPaint);
            } else {
                canvas.drawText("         -", mStageX, mStageNumberTextY, mPaint);
            }
        }
        //draw nodes
        for (int i = 0; i < mGame.getNodeNum(); ++i) {
            if(mGame.getNodeNeedVisit(i)){
                mPaint.setColor(mGoldColor);
            }else{
                mPaint.setColor(mNodeColor);
            }

            /*int startX = (int) (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            int startY = (int) (mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
            drawDrawable(canvas, mNode, startX,startY,(int)(startX + mNodeLength),(int)(startY + mNodeLength));*/
            float startX = mGameRouteOffsetX+ mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength);
            float startY = mGameRouteOffsetY +mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength);
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
                float startX = mGameRouteOffsetX +(mGame.getNodeXCord(startNodeID)) * (mEdgeLengthX + mNodeLength) + mNodeLength;
                float startY = mGameRouteOffsetY +(mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength)) + mNodeLength/ 3.0f;
                float endX = startX + mEdgeLengthX;
                float endY = startY + mNodeLength/ 3.0f;
                canvas.drawRect(startX, startY, endX, endY,mPaint);
            } else if (mGame.getNodeXCord(startNodeID) == mGame.getNodeXCord(endNodeID)) {
                float startX = mGameRouteOffsetX +(mGame.getNodeXCord(startNodeID) * (mEdgeLengthX + mNodeLength)) + mNodeLength/ 3.0f;
                float startY = mGameRouteOffsetY +(mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength)) + mNodeLength;
                float endX = startX + mNodeLength/ 3.0f;
                float endY = startY + mEdgeLengthY;
                canvas.drawRect(startX, startY, endX, endY,mPaint);
            } else{
                if (mGame.getNodeXCord(startNodeID) < mGame.getNodeXCord(endNodeID)) {
                    float startX = mGameRouteOffsetX +(mGame.getNodeXCord(startNodeID) * (mEdgeLengthX + mNodeLength))+mNodeLength;
                    float startY = (mGameRouteOffsetY +mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength))+mNodeLength;
                    float endX = mGameRouteOffsetX +(mGame.getNodeXCord(endNodeID) * (mEdgeLengthX + mNodeLength));
                    float endY = (mGameRouteOffsetY +mGame.getNodeYCord(endNodeID) * (mEdgeLengthY + mNodeLength));
                    drawDiagonal(canvas,mPaint,mPath, startX,startY,endX,endY,mEdgeLengthX,true);
                }
                else{
                    float startX = mGameRouteOffsetX +(mGame.getNodeXCord(startNodeID) * (mEdgeLengthX + mNodeLength));
                    float startY = (mGameRouteOffsetY +mGame.getNodeYCord(startNodeID) * (mEdgeLengthY + mNodeLength))+mNodeLength;
                    float endX = mGameRouteOffsetX +(mGame.getNodeXCord(endNodeID) * (mEdgeLengthX + mNodeLength))+mNodeLength;
                    float endY = (mGameRouteOffsetY +mGame.getNodeYCord(endNodeID) * (mEdgeLengthY + mNodeLength));
                    drawDiagonal(canvas,mPaint,mPath,startX,startY,endX,endY,mEdgeLengthX,false);
                }
            }
        }

        //draw player
        if(true) {//useless if statement, just want to locally define parameters i, startX and startY
            int i = mGame.getPlayerPosition();
            mPaint.setColor(0xff3fff00);
            float startX = mGameRouteOffsetX +(mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
            float startY = mGameRouteOffsetY + (mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
            if (mGame.gameOver()==Game.GameState.PLAYER_LOSE) {
                drawDrawable(canvas, mPlayerNotWin, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else if (mGame.gameOver()==Game.GameState.PLAYER_WIN) {
                drawDrawable(canvas, mPlayerWin, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else if (currentEnergyPercent > 50) {
                drawDrawable(canvas, mPlayerNormal, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else if (currentEnergyPercent > 30) {
                drawDrawable(canvas, mPlayerTired, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            } else {
                drawDrawable(canvas, mPlayerDrooling, (int) startX, (int) startY, (int) (startX + mNodeLength), (int) (startY + mNodeLength));
            }
        }
        //show one of the possible shortest paths if player not win
        if(mGame.gameOver()==Game.GameState.PLAYER_LOSE){
            List<Integer> shortestPath=mGame.getShortestList();
            mPaint.setColor(mPathColor);
            float circleCenterOffset=mNodeLength/2.0f;
            for(int i=0;i<shortestPath.size();++i){
                float startX = mGameRouteOffsetX +(mGame.getNodeXCord(shortestPath.get(i)) * (mEdgeLengthX + mNodeLength));
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


    private void notReadToDraw(){
        mDrawingParametersReady=false;
    }

    private float lastTouchX;
    private float lastTouchY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //interested in single touch and slide event
        float toleranceX=mEdgeLengthX*0.4f;
        float toleranceY=mEdgeLengthY*0.4f;
        float slideTolerance=getWidth()*0.15f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX=event.getX();
                lastTouchY=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                float dx=x-lastTouchX;
                float dy=y-lastTouchY;
                if(mGame.gameOver()==Game.GameState.GAME_NOT_END) {//disallow player to move once game over
                    if(Math.abs(dx)>slideTolerance||Math.abs(dy)>slideTolerance) {
                        if(Math.abs(dx)<slideTolerance){
                            if(dy>0){
                                step(mGame.getPlayerPosition()+mGame.getGameLevel());
                            }else{
                                step(mGame.getPlayerPosition()-mGame.getGameLevel());
                            }
                        }else if(Math.abs(dy)<slideTolerance){
                            if(dx>0){
                                step(mGame.getPlayerPosition()+1);
                            }else{
                                step(mGame.getPlayerPosition()-1);
                            }
                        }else if(dx>slideTolerance&&dy>slideTolerance){
                            step(mGame.getPlayerPosition()+mGame.getGameLevel()+1);
                        }else if(dx>slideTolerance&&dy<=slideTolerance){
                            step(mGame.getPlayerPosition()-mGame.getGameLevel()+1);
                        }else if(dx<=slideTolerance&&dy>slideTolerance){
                            step(mGame.getPlayerPosition()+mGame.getGameLevel()-1);
                        }else{
                            step(mGame.getPlayerPosition()-mGame.getGameLevel()-1);
                        }
                    }else{

                        for (int i = 0; i < mGame.getNodeNum(); ++i) {
                            float startX = mGameRouteOffsetX + (mGame.getNodeXCord(i) * (mEdgeLengthX + mNodeLength));
                            float startY = mGameRouteOffsetY + (mGame.getNodeYCord(i) * (mEdgeLengthY + mNodeLength));
                            if (x > (startX - toleranceX) && x < (startX + mNodeLength + toleranceX) && (y > startY - toleranceY) && (y < startY + mNodeLength + toleranceY)) {
                                step(i);
                                break;
                            }
                        }

                    }
                    invalidate();
                }else if(x<mDiameter &x>0 && y<mDiameter && y>0){
                    mShowGameMode=!mShowGameMode;
                    if(mShowGameMode){
                        mToastMSG.setText(R.string.show_game_mode);
                    }else{
                        mToastMSG.setText(R.string.hide_game_mode);
                    }
                    mToastMSG.show();
                    invalidate();
                }else if(x>mStageX&&y>mStageTextY&&y<mStageNumberTextY*1.5f){
                    mShowStageCleared=!mShowStageCleared;
                    if(mShowStageCleared){
                        mToastMSG.setText(R.string.show_stage_clear);
                    }else{
                        mToastMSG.setText(R.string.hide_stage_clear);
                    }
                    mToastMSG.show();
                    invalidate();
                }
                break;
        }
        return true;
    }

    private void step(int target){
        int move = mGame.setPlayerPosition(target);
        switch (move) {
            case Game.noEdge:
                break;
            case Game.setPlayer:
                Game.GameState state = mGame.gameOver();
                switch (state) {
                    case PLAYER_WIN:
                        //okay to increase it here async wrt GAME because mStageCleared will be overwritten next time
                        if (mStageCleared != Game.NOT_SHOW_GAME_STAGE) {
                            mStageCleared = mGame.getStageCleared();
                        }
                        mToastMSG.setText(R.string.player_win);
                        mToastMSG.show();
                        break;
                    case PLAYER_LOSE:
                        mToastMSG.setText(R.string.player_lose);
                        mToastMSG.show();
                        break;
                    default:
                        break;
                }
                //check if game over so we know which sound to play
                mOnPlayerMovingListener.onPlayerMoving(state);
                break;
            default:
                break;
        }
    }

    public void setOnPlayerMovingListener(onPlayerMovingListener listener){
        mOnPlayerMovingListener=listener;
    }
}

