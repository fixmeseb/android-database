package com.example.finalproject;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyDrawingArea extends View {
    Path path = new Path();
    Bitmap bmp;
    Paint paint = new Paint();

    public MyDrawingArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawingArea(Context context){
        super(context);
    }

    public MyDrawingArea(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public MyDrawingArea(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);

        canvas.drawPath(path, paint);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            path.moveTo(x, y); //path is global. Same thing that onDraw uses.
            Log.d("App", "ACTION DOWN from (" + path.toString() + ") to (" + x + "," + y + ")");
        } else if (action == MotionEvent.ACTION_MOVE) {
            path.lineTo(x, y);
            Log.d("App", "ACTION MOVE from (" + path.toString() + ") to (" + x + "," + y + ")");
        }
        return true;
    }

    /*This bmp is declared outside globally in the custom view class*/

    public Bitmap getBitmap() {
        bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        c.drawPath(path, paint); //path is global. The very same thing that onDraw uses.
        return bmp;
    }

    public void clearPath(){
        path.reset();
    }
}