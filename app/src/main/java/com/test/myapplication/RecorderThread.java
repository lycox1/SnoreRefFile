package com.test.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecorderThread extends Thread {
	private AudioRecord audioRecord;
	private boolean isRecording;
	private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private int sampleRate = 44100;
	private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)
	public boolean readComplete = false;
	FileInputStream fin = null;
	DataInputStream dis = null;

	String TAG = "SnoreRef_RecorderThread";
	byte[] buffer;
	byte[] totalBuf;
	int cnt;

	// showVariableThread showVariable;
	Handler showhandler;

	public RecorderThread(Handler showhandler) {
		this.showhandler = showhandler;
		int recBufSize = AudioRecord.getMinBufferSize(sampleRate,
				channelConfiguration, audioEncoding); // need to be larger than
														// size of a frame
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, channelConfiguration, audioEncoding, recBufSize);

		buffer = new byte[frameByteSize];
		totalBuf = new byte[AlarmStaticVariables.sampleSize * 2];
		cnt = 0;
	}

	public AudioRecord getAudioRecord() {
		return audioRecord;
	}

	public boolean isRecording() {
		return this.isAlive() && isRecording;
	}

	public void startRecording() {
		try {
			audioRecord.startRecording();
			isRecording = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			fin = new FileInputStream("/sdcard/Music/snore_sample3.wav");
			dis = new DataInputStream(fin);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	public void stopRecording() {
		try {
			audioRecord.stop();
			isRecording = false;
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
			Log.e(TAG, "read complete");
			cnt = 0;
			return totalBuf;
		}

		// analyze sound
		int totalAbsValue = 0;
		short sample = 0;
		short[] tmp = new short[frameByteSize];
		// float averageAbsValue = 0.0f;
		AlarmStaticVariables.absValue = 0.0f;

		for (int i = 0; i < frameByteSize; i += 2) {
			sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
			tmp[i] = sample;
			totalAbsValue += Math.abs(sample);
		}
		AlarmStaticVariables.absValue = totalAbsValue / frameByteSize / 2;

		Message msg = new Message();
		msg.obj = AlarmStaticVariables.absValue;
		showhandler.sendMessage(msg);

		for (int i = 0; i < buffer.length; i++) {
			totalBuf[cnt++] = buffer[i];
		}

		// ----------save into buf----------------------
		short[] tmpBuf = new short[bufferReadResult
				/ AlarmStaticVariables.rateX];
		for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i
				* AlarmStaticVariables.rateX) {
			tmpBuf[i] = tmp[ii];
		}
		synchronized (AlarmStaticVariables.inBuf) {//
			AlarmStaticVariables.inBuf.add(tmpBuf);// add data
		}
		// ----------save into buf----------------------

		// System.out.println(cnt + " vs " + AlarmStaticVariables.sampleSize);
		if (cnt > AlarmStaticVariables.sampleSize) {
			cnt = 0;
			return totalBuf;
		} else
			return null;
		// return buffer;
	}

	public void run() {
		startRecording();
	}

}
