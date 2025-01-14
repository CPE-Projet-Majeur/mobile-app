package com.example.duellingwands.model.ai;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class SpellRecognition {

    private final Interpreter interpreter;
    private static final String MODEL_FILE = "model.tflite";
    private static final int NUM_CLASSES = 8;

    public SpellRecognition(Context context) throws IOException {
        this.interpreter = new Interpreter(loadModelFile(context));
        printModelInfo();
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(context.getAssets().openFd(MODEL_FILE).getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = context.getAssets().openFd("model.tflite").getStartOffset();
            long declaredLength = context.getAssets().openFd("model.tflite").getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private void printModelInfo() {
        Tensor inputTensor = interpreter.getInputTensor(0);
        int[] inputShape = inputTensor.shape();
        DataType inputDataType = inputTensor.dataType();
        Log.d("SpellRecognition", "Input Shape: " + Arrays.toString(inputShape));
        Log.d("SpellRecognition", "Input DataType: " + inputDataType);

        Tensor outputTensor = interpreter.getOutputTensor(0);
        int[] outputShape = outputTensor.shape();
        DataType outputDataType = outputTensor.dataType();
        Log.d("SpellRecognition", "Output Shape: " + Arrays.toString(outputShape));
        Log.d("SpellRecognition", "Output DataType: " + outputDataType);
    }

    public float[] recognizeSpell(float[][] input) {
        float[][] output = new float[1][NUM_CLASSES];
        interpreter.run(input, output);
        Log.d("SpellRecognition", "Output: " + Arrays.toString(output[0]));
        return output[0];
    }

    public void close(){
        if(interpreter != null){
            interpreter.close();
        }
    }
}
