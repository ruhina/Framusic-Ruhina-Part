package com.team8.framusicv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class WizardActivity extends Activity {

	private Context mContext = null;
	private boolean mFirstTimeOpen;
	private String[] mWhoCanCallMe = new String[] {
			WelcomeActivity.class.toString(),
			DisplayBackgroundMusicActivity.class.toString() };
	private String mWhoCalledMe;

	private ViewFlipper myViewFlipper;
	private float initialXPoint;
	int[] image = { R.drawable.layout, R.drawable.choosefolder,
			R.drawable.choosemusic, R.drawable.preference };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_wizard);
		mContext = this;
		getSharedPreferences();
		processExtraData();
		saveSharedPreferences();

		myViewFlipper = (ViewFlipper) findViewById(R.id.myflipper);

		for (int i = 0; i < image.length; i++) {
			ImageView imageView = new ImageView(WizardActivity.this);
			imageView.setImageResource(image[i]);
			myViewFlipper.addView(imageView);
		}
//
//		Button next = (Button) findViewById(R.id.button1);
//		next.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (mFirstTimeOpen) {
//					Intent intent = new Intent(mContext,
//							LayoutSettingActivity.class);
//					Bundle b = new Bundle();
//					b.putString("WHO_CALLED_ME",
//							LayoutSettingActivity.class.toString());
//					intent.putExtra("CALLING_INFO", b);
//					startActivity(intent);
//				} else {
//					if (mWhoCalledMe
//							.equals(DisplayBackgroundMusicActivity.class
//									.toString())) {
//						Intent intent = new Intent(mContext,
//								DisplayBackgroundMusicActivity.class);
//						Bundle b = new Bundle();
//						b.putString("WHO_CALLED_ME",
//								DisplayBackgroundMusicActivity.class.toString());
//						intent.putExtra("CALLING_INFO", b);
//						startActivity(intent);
//					}
//				}
//				finish();
//			}
//		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will
							// return the old one
		processExtraData();
	}

	private void processExtraData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		// use the data received here
		getSharedPreferences();
		Bundle b = intent.getBundleExtra("CALLING_INFO");
		mWhoCalledMe = b.getString("WHO_CALLED_ME");
		this.count = 0;
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

	private int count = 0;

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			initialXPoint = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			float finalx = event.getX();
			if (initialXPoint > finalx) {
				System.out.println(count);
				if (count >= 3) {
					//if (mFirstTimeOpen) {
						Intent intent = new Intent(mContext,
								LayoutSettingActivity.class);
						Bundle b = new Bundle();
						b.putString("WHO_CALLED_ME",
								LayoutSettingActivity.class.toString());
						intent.putExtra("CALLING_INFO", b);
						startActivity(intent);
					// } else {
					// Intent intent = new Intent(mContext,
					// DisplayBackgroundMusicActivity.class);
					// Bundle b = new Bundle();
					// b.putString("WHO_CALLED_ME",
					// DisplayBackgroundMusicActivity.class.toString());
					// intent.putExtra("CALLING_INFO", b);
					// startActivity(intent);
					// }
					finish();
				} else {

					if (myViewFlipper.getDisplayedChild() == image.length)
						break;
					
					Animation animation = AnimationUtils.loadAnimation(mContext,
							R.anim.fadein);
					myViewFlipper.setAnimation(animation);
					myViewFlipper.showNext();
					count++;
				}
			} else {
				if (myViewFlipper.getDisplayedChild() == 0)
					break;
				myViewFlipper.showPrevious();
				count--;
			}
			break;
		}
		return false;
	}
}
