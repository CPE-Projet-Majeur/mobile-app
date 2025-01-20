package com.example.duellingwands.ui.acquisition;

import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.duellingwands.ui.views.CanvasView;

public class TouchDrawingStrategy implements IDrawingStrategy, View.OnTouchListener {

    private CanvasView canvasView;
    private View.OnTouchListener previousListener;

    @Override
    public void startDrawing() {
        if (canvasView != null) {
            previousListener = canvasView.getOnTouchListener(); // Sauvegarde
            canvasView.setOnTouchListener(this); // Définit le listener de dessin
            Log.d("TouchDrawingStrategy", "Dessin démarré. Listener précédent sauvegardé.");
        }
    }

    @Override
    public void stopDrawing() {
    }

    @Override
    public void setCanvas(CanvasView canvas) {
        this.canvasView = canvas;
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
            case MotionEvent.ACTION_UP:
                if (canvasView != null) {
                    canvasView.setOnTouchListener(previousListener); // Restaure le listener
                    previousListener.onTouch(v, event);
                    previousListener = null; // Nettoyage
                    Log.d("TouchDrawingStrategy", "Dessin arrêté. Listener précédent restauré.");
                }
        }
        Log.d("TouchDrawingStrategy", "Unknown touch event: " + event.getAction());
        return false;
    }
}
