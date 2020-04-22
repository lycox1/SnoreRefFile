package com.test.myapplication;

import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WavReader extends Thread {

    private boolean isReading;
    private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)
    public boolean readComplete = false;
    FileInputStream fin = null;
    DataInputStream dis = null;

    String TAG = "SnoreRef_WavReader";
    byte[] buffer;
    byte[] totalBuf;
    int cnt;
    String mTargetFile = null;

    // showVariableThread showVariable;
    Handler showhandler;

    public WavReader(String targetFile) {
        this.showhandler = showhandler;
        mTargetFile = targetFile;

        buffer = new byte[frameByteSize];
        totalBuf = new byte[AlarmStaticVariables.sampleSize * 2];
        cnt = 0;
    }


    public boolean isReading() {
        return this.isAlive() && isReading;
    }

    public void startReading() {

        try {
            //fin = new FileInputStream("/sdcard/Music/snore_sample3.wav");
            fin = new FileInputStream(mTargetFile);
            dis = new DataInputStream(fin);
            AlarmStaticVariables.absValue = 0.0f;
        }catch(FileNotFoundException e){
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

        Log.e(TAG, "getFrameBytes");
//		int bufferReadResult = audioRecord.read(buffer, 0, frameByteSize);
        int bufferReadResult = 0;

        try {
            while (dis==null) {
                Log.e(TAG, "dis is null");
                Thread.sleep(100);
                Log.e(TAG, "dis is null 2");
            }
            Log.e(TAG, "dis is not null");
            //bufferReadResult = dis.read(buffer, 44, frameByteSize);
            bufferReadResult = dis.read(buffer, 0, frameByteSize);
        }catch(IOException e){
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "bufferReadResult " + bufferReadResult);
        if (bufferReadResult <= -1) {
            Log.e(TAG, "short file read complete");
            cnt = 0;
            return totalBuf;
        }

        // analyze sound
        int totalAbsValue = 0;
        short sample = 0;
        short[] tmp = new short[frameByteSize];
        // float averageAbsValue = 0.0f;

        for (int i = 0; i < frameByteSize; i += 2) {
            sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
            totalAbsValue += Math.abs(sample);
        }

        for (int i = 0; i < buffer.length; i++) {
            totalBuf[cnt++] = buffer[i];
        }

        // System.out.println(cnt + " vs " + AlarmStaticVariables.sampleSize);
        if (cnt > AlarmStaticVariables.sampleSize) {
            cnt = 0;
            return totalBuf;
        } else
            return null;
    }

    public void run() {
        startReading();
    }

}
