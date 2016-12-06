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
package com.nebulaM.android.bestpath.drawing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import android.util.AttributeSet;
import android.view.View;


import com.nebulaM.android.bestpath.R;

/**
 * Created by nebulaM on 9/16/2016.
 */
public class DrawDonut extends View {
    //in which direction battery level decreases
    private boolean mClockwise;
    //radius of the donut
    private float mRadius;
    private final float mDiameter;

    private float mData;

    private Paint mPaint;
    private Path mPath;

    RectF outerCircle;
    RectF innerCircle;
    RectF shadowRectF;

    private final int YellowGreen=0xFF9ACD32;
    private final int Khaki=0xFFF0E68C;
    private final int LightSalmon=0xFFFF9999;


    /**
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     *                {@link } as well as attributes inherited
     *                from {@link android.view.View}.
     */
    public DrawDonut(Context context, AttributeSet attrs) {
        super(context, attrs);

        // attrs contains the raw values for the XML attributes
        // that were specified in the layout, which don't include
        // attributes set by styles or themes, and which may have
        // unresolved references. Call obtainStyledAttributes()
        // to get the final values for each attribute.
        //
        // This call uses R.styleable.Viewdata, which is an array of
        // the custom attributes that were declared in attrs.xml.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DrawDonut,
                0, 0
        );

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.Viewdata_* constants represent the index for
            // each custom attribute in the R.styleable.Viewdata array.
            mClockwise = a.getBoolean(R.styleable.DrawDonut_clockwise, false);
            mRadius=a.getDimension(R.styleable.DrawDonut_radius,20.0f);
           
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        mDiameter=mRadius*2;
        init();
    }
    private void init() {

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mRadius / 14.0f);

        mPath = new Path();
        outerCircle = new RectF();
        innerCircle = new RectF();
        shadowRectF = new RectF();

        float startCord;

        startCord = .03f * mRadius;
        outerCircle.set(startCord, startCord, mDiameter-startCord, mDiameter-startCord);

        startCord = .3f * mRadius;
        innerCircle.set(startCord, startCord, mDiameter-startCord, mDiameter-startCord);

    }
    /**
     *
     * @param data
     */
    public void setData(int data){
        if(data<0){
            mData=0.0f;
        }
        else if(data>100){
            mData=100.0f;
        }
        else{
            mData=(float)data;
        }
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

        if(mData>30){
            // green
            mPaint.setColor(0xff38e100);//, 0xff38e100);
        }
        else if(mData>15){
            // yellow
            mPaint.setColor(0xfff5c401);//,0xfff5c401);
        }
        else{
            //red
            mPaint.setColor(0xffb7161b);//,0xffb7161b);
        }

        if(mClockwise) {
            if(mData==100){
                drawDonut(canvas, mPaint,0.0f, 359.99f);
            }
            else {
                drawDonut(canvas, mPaint, 270.0f - mData * 3.60f, mData * 3.60f);
            }
        }
        else{
            if(mData==100){
                drawDonut(canvas, mPaint,0.0f, 359.9f);
            }
            else {
                drawDonut(canvas, mPaint, 270.0f, mData * 3.60f);
            }
        }
    }

    public void drawDonut(Canvas canvas, Paint paint, float start,float sweep){

        mPath.reset();
        mPath.arcTo(outerCircle, start, sweep, false);
        mPath.arcTo(innerCircle, start+sweep, -sweep, false);
        mPath.close();
        canvas.drawPath(mPath, paint);
    }


}
