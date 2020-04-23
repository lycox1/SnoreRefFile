package com.test.myapplication;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioRecordingConfiguration;
import android.os.Handler;
import android.util.Log;

public class DetectorThread extends Thread {

	String TAG = "SnoreRef_DetectorThread";
	private WavReader mWaveReader;
	private volatile Thread _thread;
	private WaveHeader waveHeader;
	private SnoringApi snoringApi;

	public DetectorThread() {
		Log.d(TAG, "DetectorThread");
		// TODO Auto-generated constructor stub
		this.mWaveReader = new WavReader("/sdcard/Music/snore_sample3.wav");

/*		if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
			bitsPerSample = 16;
		} else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
			bitsPerSample = 8;
		}*/

		// whistle detection only supports mono channel
/*		if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_CONFIGURATION_MONO) {
			channel = 1;
		}*/

		int bitsPerSample = 16;
		int channel = 1;
		int samplerate = 44100;

		// TODO: added detection init
		waveHeader = new WaveHeader();
		waveHeader.setChannels(channel);
		waveHeader.setBitsPerSample(bitsPerSample);
		waveHeader.setSampleRate(samplerate);
		snoringApi = new SnoringApi(waveHeader);
	}

	public void stopDetection() {
		// TODO Auto-generated method stub
		_thread = null;
	}

	public int getTotalSnoreDetected() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void start() {
		_thread = new Thread(this);
		_thread.start();
	}

	public void run() {
		// @TODO: added run method content
		try {
			byte[] buffer;
			// initBuffer();

			Log.e(TAG, "run()");
//			while (_thread == thisThread) {
//			while ( mDetectionState == true ) {
			while ( mWaveReader.mFileReadComplete == false ) {
				// detect sound
				Log.e(TAG, "while");
				buffer = mWaveReader.getFrameBytes();
				Log.d(TAG, "buffer size " + buffer.length);
				// audio analyst
				if (mWaveReader.mFileReadComplete == false) {
					Log.d(TAG, "How many bytes? " + buffer.length);
					AlarmStaticVariables.snoringCount = snoringApi.isSnoring(buffer);
					Log.d(TAG,"count=" + AlarmStaticVariables.snoringCount);
					break;
					// end snore detection

				} else {
					Log.d(TAG, "run() Read File Complete ");
				//	MainActivity.snoreValue = 0;
				}
				// end audio analyst
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
