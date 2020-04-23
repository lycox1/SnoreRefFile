package com.test.myapplication;

import android.media.AudioFormat;
import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WavReader {

    private boolean isReading;
    private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)
    public boolean mFileReadComplete = false;
    FileInputStream fin = null;
    DataInputStream dis = null;

    String TAG = "SnoreRef_WavReader";
    byte[] buffer;
    byte[] totalBuf;
    byte[] headerBUffer;
    int cnt;
    String mTargetFile = null;

    // showVariableThread showVariable;
    Handler showhandler;

    public WavReader(String targetFile) {
        mFileReadComplete = false;
        mTargetFile = targetFile;

        //buffer = new byte[AlarmStaticVariables.sampleSize];
        //totalBuf = new byte[AlarmStaticVariables.sampleSize * 2];

        headerBUffer = new byte[44];
        cnt = 0;
        startReading();
    }

    public void startReading() {

        try {
            //fin = new FileInputStream("/sdcard/Music/snore_sample3.wav");
            Log.d(TAG, "startReading() mTargetFile " +mTargetFile);

            fin = new FileInputStream(mTargetFile);
            dis = new DataInputStream(fin);
            AlarmStaticVariables.absValue = 0.0f;
            int readedHeaderSize = dis.read(headerBUffer, 0, 44);
            Log.d(TAG, "readedHeaderSize " + readedHeaderSize);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            isReading = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFrameBytes() {

        Log.d(TAG, "getFrameBytes");
        int bufferReadResult = 0;
        buffer = new byte[AlarmStaticVariables.sampleSize];

        try {
            bufferReadResult = dis.read(buffer, 0, AlarmStaticVariables.sampleSize);
        }catch(IOException e){
            e.printStackTrace();
        }

        if (bufferReadResult > 0) {
            Log.d(TAG, "getFrameBytes() read " + bufferReadResult + " bytes");
        } else {
            mFileReadComplete = true;
            Log.d(TAG, "getFrameBytes() read complete");
        }

        return buffer;

/*        for (int i = 0; i < frameByteSize; i += 2) {
            sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
            totalAbsValue += Math.abs(sample);
        }*/
    }

}
