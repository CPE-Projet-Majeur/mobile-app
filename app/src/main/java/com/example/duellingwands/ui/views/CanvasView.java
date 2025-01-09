package com.example.duellingwands.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class CanvasView extends View {

    private final Paint paint;
    private final ArrayList<Point> points;

    public CanvasView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.paint = new Paint();
        this.points = new ArrayList<>();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
    }

    public void addPoint(Point point){
        this.points.add(point);
    }

    public void addPoint(int x, int y){
        this.points.add(new Point(x, y));
    }

    public void addPoints(ArrayList<Point> points){
        this.points.addAll(points);
    }

    public void removePoint(Point point){
        this.points.remove(point);
    }

    public void clearPoints(){
        this.points.clear();
    }

    // debug
    public void changeColorRandom() {
        paint.setColor(Color.rgb(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (this.points.size() < 2) {
            return;
        }

        for (int i = 0; i < this.points.size() - 1; i++) {
            Point point1 = this.points.get(i);
            Point point2 = this.points.get(i + 1);
            canvas.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY(), paint);
        }
    }

    // Convertir le contenu en Bitmap
    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas); // Dessiner le contenu actuel sur le bitmap
        return bitmap;
    }
}
