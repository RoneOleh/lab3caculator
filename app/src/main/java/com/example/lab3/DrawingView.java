package com.example.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.mlkit.vision.digitalink.Ink;

public class DrawingView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private Ink.Builder inkBuilder = Ink.builder();
    private Ink.Stroke.Builder strokeBuilder;
    private boolean empty = true;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public Ink getInk() {
        return inkBuilder.build();
    }

    public boolean isEmpty() {
        return empty;
    }

    public void clear() {
        path.reset();
        inkBuilder = Ink.builder();
        strokeBuilder = null;
        empty = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long time = System.currentTimeMillis();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                path.moveTo(x, y);
                strokeBuilder = Ink.Stroke.builder();
                strokeBuilder.addPoint(Ink.Point.create(x, y, time));
                break;
            case MotionEvent.ACTION_MOVE:
                if (strokeBuilder == null) {
                    return true;
                }
                path.lineTo(x, y);
                strokeBuilder.addPoint(Ink.Point.create(x, y, time));
                break;
            case MotionEvent.ACTION_UP:
                if (strokeBuilder == null) {
                    return true;
                }
                path.lineTo(x, y);
                strokeBuilder.addPoint(Ink.Point.create(x, y, time));
                inkBuilder.addStroke(strokeBuilder.build());
                strokeBuilder = null;
                empty = false;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}
