package com.test.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int DETECT_NONE = 0;
	public static final int DETECT_SNORE = 1;
	public static int selectedDetection = DETECT_NONE;

	private DetectorThread detectorThread;

	private Button mSnoreTestStartBtn, mSnoreTestStoptBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_new);
		setTitle("UIC SleepTracker Demo");

		mSnoreTestStartBtn = (Button) this.findViewById(R.id.btnSleepRecord);
		mSnoreTestStoptBtn = (Button) findViewById(R.id.btnAlarmTest);

		/**
		 * Sleep Record Button
		 */
		mSnoreTestStartBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				selectedDetection = DETECT_SNORE;
				detectorThread = new DetectorThread();
				detectorThread.start();
			}
		});

		/**
		 * Test
		 */
		mSnoreTestStoptBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				detectorThread.stopDetection();
			}
		});

	}

	public void setLevel(int l) {
		switch (l) {
		case 0:
			AlarmStaticVariables.level = AlarmStaticVariables.level0;
			break;
		case 1:
			AlarmStaticVariables.level = AlarmStaticVariables.level1;
			break;
		case 2:
			AlarmStaticVariables.level = AlarmStaticVariables.level2;
			break;
		case 3:
			AlarmStaticVariables.level = AlarmStaticVariables.level3;
			break;
		default:
			AlarmStaticVariables.level = AlarmStaticVariables.level1;
			break;
		}
	}

	private void goHomeView() {

		if (detectorThread != null) {
			detectorThread.stopDetection();
			detectorThread = null;
		}
		selectedDetection = DETECT_NONE;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Quit demo");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			goHomeView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
