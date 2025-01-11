package com.example.duellingwands.ui.acquisition;

import com.example.duellingwands.ui.views.CanvasView;

public interface IDrawingStrategy {
    void startDrawing();
    void stopDrawing();
    void setCanvas(CanvasView canvas);
    void erase(); // Temporary
}
