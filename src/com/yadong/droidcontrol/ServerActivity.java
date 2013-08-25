package com.yadong.droidcontrol;

import android.app.Activity;
import android.app.UiAutomation.OnAccessibilityEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ServerActivity extends Activity implements OnSeekBarChangeListener{
	private static final String TAG = "ServerActivity"; 
	private static final int CENTER_PROCESS = 50;
	private static final int MAX_SPEED = 255;
	private static final int MAX_SEEKBAR = 100;
	private static final int DIRECTION_NULL = 50;
	private SeekBar mDirectionSeekbar;
	private Handler mHander;
	private Switch mRunDirection;
	private SeekBar mSpeedSeekbar;
	private SeekBar mCameraDirectionSeekbar;
	private int mSpeedLeft = 0;
	private int mSpeedRight = 0;
	private TextView mDebugView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		mDirectionSeekbar = (SeekBar) findViewById(R.id.directionSeekBar);
		mDirectionSeekbar.setOnSeekBarChangeListener(this);

		mSpeedSeekbar = (SeekBar) findViewById(R.id.speedSeekBar);
		mSpeedSeekbar.setOnSeekBarChangeListener(this);

		mCameraDirectionSeekbar = (SeekBar) findViewById(R.id.cameraDirection);
		mCameraDirectionSeekbar.setOnSeekBarChangeListener(this);

		mDebugView = (TextView) findViewById(R.id.debugInfo);

		mRunDirection = (Switch) findViewById(R.id.runDirection);
		mRunDirection.setOnCheckedChangeListener(mRunDirectionChangeListener);
		mHander = new Handler(getMainLooper());
	}

	private OnCheckedChangeListener mRunDirectionChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		}
	};

	private Runnable mDirectionSeekBarRoolback = new Runnable() {
		@Override
		public void run() {
			if (mDirectionSeekbar != null) {
				int process = mDirectionSeekbar.getProgress();
				if (process != CENTER_PROCESS) {
					while (process > CENTER_PROCESS) {
						process--;
						mDirectionSeekbar.setProgress(process);
					}
					while (process < CENTER_PROCESS) {
						process++;
						mDirectionSeekbar.setProgress(process);
					}
				}
			}
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.equals(mSpeedSeekbar) || seekBar.equals(mDirectionSeekbar)){
			resetSpeed();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar.equals(mDirectionSeekbar)){
			if(seekBar.getProgress() != CENTER_PROCESS){
				mHander.post(mDirectionSeekBarRoolback);
			}
		}
	}

	private void resetSpeed(){
		int process = mSpeedSeekbar.getProgress();
		log("resetSpeed process="+process);
		int speed = (int) ((float)process / MAX_SEEKBAR * MAX_SPEED);
		log("resetSpeed speed="+speed);
		int direction = mDirectionSeekbar.getProgress();
		if (direction != DIRECTION_NULL){
			if(direction < 50){
				//turn left
				int leftSpeed = (int) ((float)direction / DIRECTION_NULL * speed);
				mSpeedLeft = leftSpeed;
				mSpeedRight = speed;
			} else {
				//trun right
				int rightSpeed = (int) ((float)(MAX_SEEKBAR-direction)/DIRECTION_NULL * speed);
				mSpeedRight = rightSpeed;
				mSpeedLeft = speed;
			}
		} else {
			mSpeedLeft = mSpeedRight = speed;
		}
		log("Change speed left ="+mSpeedLeft+" right ="+mSpeedRight);
	}

	private String mNeedWrite;
	private void log(String message){
		Log.v(TAG, message);
		mNeedWrite = message;
	}
}
