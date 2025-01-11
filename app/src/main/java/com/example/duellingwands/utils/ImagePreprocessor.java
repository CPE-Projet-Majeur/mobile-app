package com.example.duellingwands.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePreprocessor {

    static final int MODEL_WIDTH = 28;
    static final int MODEL_HEIGHT = 28;
    //static final int MODEL_CHANNELS = 1;

    public static float[][] preprocessImage(Bitmap image, Context context) {
        // Resize
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, MODEL_WIDTH, MODEL_HEIGHT, true);
        float[][] pixels = new float[1][MODEL_WIDTH*MODEL_HEIGHT];
        // Dev
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
        // Save the grayscale image
        saveBitmapToFile(grayscaleBitmap, context, "preprocessed_image.png");

        Log.d("ImagePreprocessor", "Image preprocessed : " + pixels.length + "x" + pixels[0].length);
        return pixels;
    }

    private static void saveBitmapToFile(Bitmap bitmap, Context context, String fileName) {
        try {
            File file = new File(context.getExternalFilesDir(null), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("ImagePreprocessor", "Image saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("ImagePreprocessor", "Failed to save image", e);
        }
    }
}
