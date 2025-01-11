package com.example.duellingwands.ui.acquisition;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.duellingwands.ui.views.CanvasView;

public class TouchDrawingStrategy implements IDrawingStrategy, View.OnTouchListener {

    private CanvasView canvasView;

    @Override
    public void startDrawing() {
        this.canvasView.setOnTouchListener(this);
    }

    @Override
    public void stopDrawing() {
        this.canvasView.setOnTouchListener(null);
    }

    @Override
    public void setCanvas(CanvasView canvas) {
        this.canvasView = canvas;
        this.canvasView.setOnTouchListener(this);
    }

    @Override
    public void erase() {
        if(canvasView != null) {
            canvasView.clearPoints();
            canvasView.invalidate();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (canvasView == null) return false;
        Log.d("TouchDrawingStrategy", "Touch event: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                canvasView.addPoint((int) event.getX(), (int) event.getY());
                canvasView.invalidate();
                return true;
        }
        Log.d("TouchDrawingStrategy", "Unknown touch event: " + event.getAction());
        return false;
    }
}
