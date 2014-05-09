package com.team8.framusicv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class LayoutSettingActivity extends Activity {
	Context mContext = null;
	private String mWhoCalledMe;
	private boolean mFirstTimeOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.screen_layout_setting);

		mContext = this;
		getSharedPreferences();
		processExtraData();
		

		ImageButton layout1 = (ImageButton) findViewById(R.id.layout1);
		layout1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, Layout1Activity.class);
				startActivity(intent);
			}
		});
		
		ImageButton layout2 = (ImageButton) findViewById(R.id.layout2);
		layout2.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Layout2Activity.class);
				startActivity(intent);
			}
		});
		
		ImageButton layout3 = (ImageButton) findViewById(R.id.layout3);
		layout3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, Layout3Activity.class);
				startActivity(intent);
			}
		});	
		
		Button done = (Button) findViewById(R.id.btn_done);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mFirstTimeOpen) {
					Intent intent = new Intent(mContext,
							MultiPhotoSelectActivity.class);
					Bundle b = new Bundle();
					b.putString("WHO_CALLED_ME",
							LayoutSettingActivity.class.toString());
					intent.putExtra("CALLING_INFO", b);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext,
							DisplayBackgroundMusicActivity.class);
					startActivity(intent);
				}
				finish();
			}
		});
	}

	private void processExtraData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		// use the data received here
		getSharedPreferences();
		Bundle b = intent.getBundleExtra("CALLING_INFO");
		mWhoCalledMe = b.getString("WHO_CALLED_ME");
	}

	protected void getSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences("Setting",
				MODE_PRIVATE);

		mFirstTimeOpen = sp.getBoolean("FIRST_TIME_OPEN", true);
	}

	protected void setFromPreferencesValue() {

		if (this.mFirstTimeOpen == true) {
			this.mFirstTimeOpen = false;
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

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}
}
