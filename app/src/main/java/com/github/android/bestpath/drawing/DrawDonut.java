/*Copyright 2016 nebulaM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.github.android.bestpath.drawing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.github.android.bestpath.R;

/**
 * Created by nebulaM on 9/16/2016.
 */
public class DrawDonut extends View {
    public static final String TAG="DrwaDonut";
    //in which direction battery level decreases
    private boolean mClockwise;
    //radius of the donut
    private float mRadius;
    private float mDiameter;

    private float mData;

    private Paint mPaint;
    private Path mPath;

    RectF outerCircle;
    RectF innerCircle;

    private final int Green=0xff38e100;
    private final int Yellow=0xfff5c401;
    private final int Red=0xffb7161b;
    private final int Grey=0xffa2a2a2;

    /**
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     *                {@link } as well as attributes inherited
     *                from {@link android.view.View}.
     */
    public DrawDonut(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DrawDonut,
                0, 0
        );

        try {
            mClockwise = true;
            mRadius=a.getDimension(R.styleable.DrawDonut_radius,20.0f);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        mDiameter=mRadius*2;

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        //mPaint.setStrokeWidth(mRadius / 14.0f);

        mPath = new Path();
        outerCircle = new RectF();
        innerCircle = new RectF();

        float startCord;

        startCord = .03f * mRadius;
        outerCircle.set(startCord, startCord, mDiameter-startCord, mDiameter-startCord);

        startCord = .3f * mRadius;
        innerCircle.set(startCord, startCord, mDiameter-startCord, mDiameter-startCord);
        Log.d(TAG,"width is "+getWidth());
    }
    /**
     *
     * @param data
     */
    public void setData(int data){
        mData=data;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) mDiameter;
        int desiredHeight = (int) mDiameter;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if (widthMode == MeasureSpec.AT_MOST) {
            //wrap content
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG,"I am drawing w/ data "+mData);
        if(mData>30){
            // green
            mPaint.setColor(Green);
        } else if(mData>15){
            // yellow
            mPaint.setColor(Yellow);
        } else{
            //red
            mPaint.setColor(Red);
        }

        //energy decreases clockwise
        if(mClockwise) {
            if(mData==100.0f){
                drawDonut(canvas, mPaint, mPath,0.0f, 359.99f);
            } else if(mData==0.0f){
                mPaint.setColor(Grey);
                drawDonut(canvas, mPaint, mPath, 0.0f, 359.99f);
            } else {
                drawDonut(canvas, mPaint, mPath, 270.0f - mData * 3.60f, mData * 3.60f);
                mPaint.setColor(Grey);
                drawDonut(canvas, mPaint, mPath, 270.0f, (100-mData) * 3.60f);
            }
        } else{//energy decreases counterclockwise
            if(mData==100.0f){
                drawDonut(canvas, mPaint, mPath,0.0f, 359.9f);
            } else if(mData==0.0f){
                mPaint.setColor(Grey);
                drawDonut(canvas, mPaint, mPath,0.0f, 359.99f);
            } else {
                drawDonut(canvas, mPaint, mPath, 270.0f, mData * 3.60f);
                mPaint.setColor(Grey);
                drawDonut(canvas, mPaint, mPath, 270.0f+mData * 3.60f, (100.0f-mData) * 3.60f);
            }
        }
    }

    private void drawDonut(Canvas canvas, Paint paint, Path path, float start,float sweep){
        path.reset();
        path.arcTo(outerCircle, start, sweep, false);
        path.arcTo(innerCircle, start+sweep, -sweep, false);
        path.close();
        canvas.drawPath(path, paint);
    }
}
