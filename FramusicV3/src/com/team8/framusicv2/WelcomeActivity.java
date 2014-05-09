package com.team8.framusicv2;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.Window;

public class WelcomeActivity extends Activity {
	private Context mContext = null;
	private boolean mFirstTimeOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.screen_welcome);

		mContext = this;
		this.getSharedPreferences();

		final Timer timer = new Timer();
		final TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (mFirstTimeOpen == true) {
					saveSharedPreferences();
					Intent intent = new Intent(mContext, WizardActivity.class);
					Bundle b = new Bundle();
					b.putString("WHO_CALLED_ME", mContext.getClass().toString());
					intent.putExtra("CALLING_INFO", b);
					startActivity(intent);
				} else {
					saveSharedPreferences();
					Intent intent = new Intent(mContext,
							DisplayBackgroundMusicActivity.class);
					Bundle b = new Bundle();
					b.putString("WHO_CALLED_ME", mContext.getClass().toString());
					intent.putExtra("CALLING_INFO", b);
					startActivity(intent);
				}
				finish();
			}
		};
		timer.schedule(timerTask, 3500);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome_page, menu);
		return true;
	}

	protected void getSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences("Setting",
				MODE_PRIVATE);

		mFirstTimeOpen = sp.getBoolean("FIRST_TIME_OPEN", true);
	}

	protected void setFromPreferencesValue() {

		if (this.mFirstTimeOpen == true) {
			//this.mFirstTimeOpen = false;
		}
		System.out.println();
	}

	protected void saveSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences("Setting",
				MODE_PRIVATE);

		Editor editor = sp.edit();
		editor.putBoolean("FIRST_TIME_OPEN", mFirstTimeOpen);
		editor.commit();
	}

}
