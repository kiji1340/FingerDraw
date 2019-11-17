package com.dbao1608.fingerdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;


public class DrawFingerView extends AppCompatImageView {
    private Path lastestPath;
    private Paint lastestPaint;
    private ArrayList<Path> pathList;
    private ArrayList<Paint> paintList;
    private int lineWitdh = 15;
    private int currentColor = -1;
    private OnCoordinateListener listener;

    protected boolean isDrawing() {
        return currentColor != -1;
    }

    public DrawFingerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pathList = new ArrayList<>();
        paintList = new ArrayList<>();
        init();
    }

    public void setOnCoordinateListener(OnCoordinateListener listener) {
        this.listener = listener;
    }


    public void setColor(int color) {
        this.currentColor = currentColor == color ? -1: color;
    }

    private void init() {
        if(currentColor == -1) return;
        lastestPaint = getNewPaint();
        lastestPath = getNewPath();

        pathList.add(lastestPath);
        paintList.add(lastestPaint);


    }

    private Path getNewPath() {
        return new Path();
    }

    private Paint getNewPaint() {
        Paint paint = new Paint();
        paint.setStrokeWidth(lineWitdh);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(currentColor);
        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(currentColor == -1) return true;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null)
                    listener.start(x, y);
                startPath(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener != null)
                    listener.moving(x, y);
                updatePath(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null)
                    listener.end(x, y);
                endPath(x, y);
                break;
        }
        postInvalidate();
        return true;
    }

    private void startPath(float x, float y) {
        init();
        lastestPath.moveTo(x, y);
    }

    private void updatePath(float x, float y) {
        lastestPath.lineTo(x, y);
    }

    private void endPath(float x, float y) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < paintList.size(); i++) {
            canvas.drawPath(pathList.get(i), paintList.get(i));
        }
    }

    public void resetView() {
        lastestPath.reset();
        lastestPaint.reset();

        pathList.clear();
        paintList.clear();

        init();
        postInvalidate();
    }

    public void undoPath() {
        if (paintList.size() > 1) {
            lastestPaint = paintList.get(paintList.size() - 2);
            lastestPath = pathList.get(pathList.size() - 2);

            paintList.remove(paintList.size() - 1);
            pathList.remove(pathList.size() - 1);
            currentColor = -1;
        } else {
            resetView();
        }
        postInvalidate();
    }

}
