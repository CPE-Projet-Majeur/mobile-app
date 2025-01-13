package com.example.duellingwands.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.ViewDebug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePreprocessor {

    static final int MODEL_WIDTH = 28;
    static final int MODEL_HEIGHT = 28;
    //static final int MODEL_CHANNELS = 1;

    private static int counter = 201;

    public static float[][] preprocessImage(Bitmap image, Context context) {
        Bitmap bmp280 = Bitmap.createScaledBitmap(image, 280, 280, true);
        //saveBitmapToFile(bmp280, context, "bmp280.jpg"); // Dev
        // Crop
//        int[] bounds = findBoundingBox(bmp280);
//        int minX = bounds[0] - 10;
//        int minY = bounds[1] - 10;
//        int maxX = bounds[2] + 10;
//        int maxY = bounds[3] + 10;
//        if (maxX < minX || maxY < minY) { // Nothing was drawn
//            return new float[][] { new float[MODEL_WIDTH * MODEL_HEIGHT] };
//        }
//        int width = maxX - minX + 1;
//        int height = maxY - minY + 1;
//        Bitmap cropped = Bitmap.createBitmap(image, minX, minY, width, height);
//        saveBitmapToFile(cropped, context, "cropped_image.png"); // Dev
        // Resize
        Bitmap rotatedBitmap = rotateBitmap(bmp280, -90);
        //saveBitmapToFile(rotatedBitmap, context, "rotated.jpg");
        Bitmap resizedImage = Bitmap.createScaledBitmap(rotatedBitmap, MODEL_WIDTH, MODEL_HEIGHT, true);
        float[][] pixels = new float[1][MODEL_WIDTH*MODEL_HEIGHT];
        // Dev
            //saveBitmapToFile(resizedImage, context, "resized_image.jpg");
            Bitmap grayscaleBitmap = Bitmap.createBitmap(MODEL_WIDTH, MODEL_HEIGHT, Bitmap.Config.ARGB_8888);
        // Grayscale and normalize
        for (int x = 0; x < MODEL_WIDTH; x++) {
            for (int y = 0; y < MODEL_HEIGHT; y++) {
                int pixel = resizedImage.getPixel(x, y);
                float intensity = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3.0f;
                pixels[0][x * MODEL_WIDTH + y] = intensity  / 255.0f;
                pixels[0][x * MODEL_WIDTH + y] = 1 - pixels[0][x * MODEL_WIDTH + y];

                // Map back to 0-255 for visualization (Dev)
                int grayscale = (int) (pixels[0][x * MODEL_WIDTH + y] * 255);
                grayscaleBitmap.setPixel(x, y, Color.rgb(grayscale, grayscale, grayscale));
            }
        }
        // Save the grayscale image for visualization (Dev)
        saveBitmapToFile(grayscaleBitmap, context, "preprocessed_image.jpg");
        Log.d("ImagePreprocessor",  "Path : "+Environment.getExternalStorageDirectory().toString());

        Log.d("ImagePreprocessor", "Image preprocessed : " + pixels.length + "x" + pixels[0].length);
        return pixels;
    }

    private static int[] findBoundingBox(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int minX = width, minY = height;
        int maxX = -1, maxY = -1;
        int whiteColor = Color.WHITE;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = bitmap.getPixel(x, y);
                if (color != whiteColor) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        return new int[] { minX, minY, maxX, maxY };
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private static void saveBitmapToFile(Bitmap bitmap, Context context, String fileName) {
        try {
            fileName = counter + ".jpg";
            Log.d("ImagePreprocessor",  "fileName : "+ fileName);
            counter = counter + 1;
            File file = new File(context.getExternalFilesDir(null), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("ImagePreprocessor", "Image saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("ImagePreprocessor", "Failed to save image", e);
        }
    }
}
