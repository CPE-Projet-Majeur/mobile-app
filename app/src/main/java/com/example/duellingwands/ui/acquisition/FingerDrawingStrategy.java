package com.example.duellingwands.ui.acquisition;

import android.view.MotionEvent;
import android.view.View;

import com.example.duellingwands.ui.views.CanvasView;

public class FingerDrawingStrategy implements IDrawingStrategy, View.OnTouchListener {

    private CanvasView canvasView;

    @Override
    public void startDrawing() {

    }

    @Override
    public void stopDrawing() {

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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
