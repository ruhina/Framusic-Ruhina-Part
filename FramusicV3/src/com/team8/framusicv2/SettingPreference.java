package com.team8.framusicv2;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingPreference extends PreferenceActivity {

	private Context mContext = null;
	private ActionBar mActionBar = null;

	private Preference mBatterySaving = null;
	private Preference mBattryBottomLine = null;
	private Preference mAction = null;
	private SwitchPreference mStopSlide = null;
	private SwitchPreference mStopMusic = null;
	private SwitchPreference mQuitApp = null;

	private SwitchPreference mAlarm = null;
	private Preference mStartPlayingMusic = null;
	private Preference mStopPlayingMusic = null;

	private String batterySummery;
	private String startPlayingMusicSummery;
	private String stopPlayingMusicSummery;

	// sharedPreference Value
	private int prog = 0;// battery bottom line
	private int startTimeHour = 0;// alarm start time
	private int startTimeMinute = 0;
	private int stopTimeHour = 0;// alarm stop time
	private int stopTimeMinute = 0;
	private boolean stopSlidingShow = false;
	private boolean stopPlayingMusic = false;
	private boolean quitFramusic = false;
	private boolean alarmOnOff = false;

	public SettingPreference() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.setting);
		mContext = this;

		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);

		this.mBatterySaving = findPreference("battery_saving_key");
		this.mBattryBottomLine = findPreference("battery_bottom_line");
		// this.mAction = findPreference("action_key");
		this.mStopSlide = (SwitchPreference) findPreference("stop_slide");
		this.mStopMusic = (SwitchPreference) findPreference("stop_music");
		this.mQuitApp = (SwitchPreference) findPreference("quit_app");

		this.mAlarm = (SwitchPreference) findPreference("alarm_on_off");
		this.mStartPlayingMusic = findPreference("start_play_music_at");
		this.mStopPlayingMusic = findPreference("stop_play_music_at");

		// need to write into string.xml
		batterySummery = mBattryBottomLine.getSummary().toString();

		startPlayingMusicSummery = this.mStartPlayingMusic.getSummary()
				.toString();
		stopPlayingMusicSummery = this.mStopPlayingMusic.getSummary()
				.toString();
		getSharedPreferences();
		setFromPreferencesValue();

		if (mAlarm.isChecked()) {
			mStartPlayingMusic.setEnabled(true);
			mStopPlayingMusic.setEnabled(true);
			mStartPlayingMusic.setSelectable(true);
			mStopPlayingMusic.setSelectable(true);
			mStopPlayingMusic.setLayoutResource(R.layout.preference);
			mStartPlayingMusic.setLayoutResource(R.layout.preference);
		} else {
			mStartPlayingMusic.setEnabled(false);
			mStopPlayingMusic.setEnabled(false);
			mStartPlayingMusic.setSelectable(false);
			mStopPlayingMusic.setSelectable(false);
			mStartPlayingMusic.setLayoutResource(R.layout.blank);
			mStopPlayingMusic.setLayoutResource(R.layout.blank);
		}

		mStopSlide
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						if (mStopSlide.isChecked()) {

							stopSlidingShow = false;
						} else {

							stopSlidingShow = true;
						}
						saveSharedPreferences();
						return true;
					}

				});

		mStopMusic
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						if (mStopMusic.isChecked()) {

							stopPlayingMusic = false;
						} else {

							stopPlayingMusic = true;
						}
						saveSharedPreferences();
						return true;
					}

				});

		mQuitApp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				if (mQuitApp.isChecked()) {

					quitFramusic = false;
				} else {

					quitFramusic = true;
				}
				saveSharedPreferences();
				return true;
			}

		});

		mAlarm.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				if (mAlarm.isChecked()) {
					mStartPlayingMusic.setEnabled(false);
					mStopPlayingMusic.setEnabled(false);
					mStartPlayingMusic.setSelectable(false);
					mStopPlayingMusic.setSelectable(false);
					mStartPlayingMusic.setLayoutResource(R.layout.blank);
					mStopPlayingMusic.setLayoutResource(R.layout.blank);

					alarmOnOff = false;
				} else {
					mStartPlayingMusic.setEnabled(true);
					mStopPlayingMusic.setEnabled(true);
					mStartPlayingMusic.setSelectable(true);
					mStopPlayingMusic.setSelectable(true);
					mStopPlayingMusic.setLayoutResource(R.layout.preference);
					mStartPlayingMusic.setLayoutResource(R.layout.preference);

					alarmOnOff = true;
				}
				saveSharedPreferences();
				return true;
			}

		});

		this.mBattryBottomLine
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub

						displayBatteryBottomLineDialog(new String(
								"Battry Bottom Line Setting"));
						return false;
					}

				});

		this.mStartPlayingMusic
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						displayTimePickerDialog("StartPlayingMusic",
								mStartPlayingMusic);
						return false;
					}

				});
		;
		this.mStopPlayingMusic
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub

						displayTimePickerDialog("StopPlayingMusic",
								mStopPlayingMusic);
						return false;
					}

				});

	}

	private void displayBatteryBottomLineDialog(String title) {
		/**
		 * Listing 10-22: Creating a new dialog using the Dialog class
		 */
		// Create the new Dialog.
		final Dialog dialog = new Dialog(mContext, R.style.WhiteTextDialog);
		// Set the title.

		dialog.setTitle(title);
		dialog.setContentView(R.layout.bottomlinepicker);

		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		lp.width = width / 2; // 욱똑

		dialogWindow.setAttributes(lp);

		// Display the Dialog.
		dialog.show();

		Button bOk = (Button) dialog.findViewById(R.id.Ok);
		bOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// add service for battery
				String s = batterySummery.concat(new Integer(prog).toString()
						+ "%");
				mBattryBottomLine.setSummary(s);
				saveSharedPreferences();
				dialog.dismiss();
			}
		});

		Button bCancel = (Button) dialog.findViewById(R.id.Cancel);
		bCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});

		final TextView p = (TextView) dialog.findViewById(R.id.batteryText);
		p.setText(new Integer(prog).toString() + "%");

		SeekBar s = (SeekBar) dialog.findViewById(R.id.seekBar1);
		s.setProgress(prog);
		s.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				// System.out.println(seekBar.getProgress());
				String s = new String(new Integer(progress).toString() + "%");
				p.setText(s);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// System.out.println(seekBar.getProgress());
				String s = new String(new Integer(seekBar.getProgress())
						.toString() + "%");
				p.setText(s);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// System.out.println(seekBar.getProgress());
				String s = new String(new Integer(seekBar.getProgress())
						.toString() + "%");
				p.setText(s);
				prog = seekBar.getProgress();
			}

		});

	}

	private void displayTimePickerDialog(String title, final Preference p) {
		/**
		 * Listing 10-22: Creating a new dialog using the Dialog class
		 */
		// Create the new Dialog.
		final Dialog dialog = new Dialog(mContext, R.style.WhiteTextDialog);
		// Set the title.

		dialog.setTitle(title);
		dialog.setContentView(R.layout.timepicker);

		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		lp.width = width / 2; // 욱똑

		dialogWindow.setAttributes(lp);

		// Display the Dialog.
		dialog.show();

		Button bOk = (Button) dialog.findViewById(R.id.Ok);
		bOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// add service for battery
				TimePicker t = (TimePicker) dialog
						.findViewById(R.id.timePicker1);

				int h = t.getCurrentHour();
				int m = t.getCurrentMinute();

				String hs = new Integer(h).toString();
				String ms = new Integer(m).toString();
				if (h < 10) {
					hs = "0" + hs;
				}
				if (m < 10) {
					ms = "0" + ms;
				}

				if (p.getTitle().equals("Start Playing Music")) {
					p.setSummary(startPlayingMusicSummery + hs + ":" + ms);
					startTimeHour = h;
					startTimeMinute = m;
				} else if (p.getTitle().equals("Stop Playing Music")) {
					p.setSummary(stopPlayingMusicSummery + hs + ":" + ms);
					stopTimeHour = h;
					stopTimeMinute = m;
				}
				saveSharedPreferences();
				dialog.dismiss();
			}
		});

		Button bCancel = (Button) dialog.findViewById(R.id.Cancel);
		bCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		// Handle action buttons
		// save to shared preference
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			Intent it = new Intent(mContext, DisplayBackgroundMusicActivity.class);
			startActivity(it);
			//saveSharedPreferences();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void getSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences("Setting",
				MODE_PRIVATE);

		prog = sp.getInt("PROGRESS_OF_BATTERY", prog);
		startTimeHour = sp.getInt("START_TIME_HOUR", startTimeHour);
		startTimeMinute = sp.getInt("START_TIME_MUNITE", startTimeMinute);
		stopTimeHour = sp.getInt("STOP_TIME_HOUR", stopTimeHour);
		stopTimeMinute = sp.getInt("STOP_TIME_MINUTE", stopTimeMinute);
		stopSlidingShow = sp.getBoolean("STOP_SLIDING_SHOW", stopSlidingShow);
		stopPlayingMusic = sp
				.getBoolean("STOP_PLAYING_MUSIC", stopPlayingMusic);
		quitFramusic = sp.getBoolean("QUIT_FRAMUSIC", quitFramusic);
		alarmOnOff = sp.getBoolean("ALARM_ON_OFF", alarmOnOff);
	}

	protected void setFromPreferencesValue() {
		this.mBattryBottomLine.setSummary(batterySummery.concat(new Integer(
				prog).toString() + "%"));
		this.mStopSlide.setChecked(stopSlidingShow);
		this.mStopMusic.setChecked(stopPlayingMusic);
		this.mQuitApp.setChecked(quitFramusic);

		this.mAlarm.setChecked(alarmOnOff);
		String hs = new Integer(startTimeHour).toString();
		String ms = new Integer(startTimeMinute).toString();
		if (startTimeHour < 10) {
			hs = "0" + hs;
		}
		if (startTimeMinute < 10) {
			ms = "0" + ms;
		}
		this.mStartPlayingMusic.setSummary(startPlayingMusicSummery + hs + ":"
				+ ms);

		hs = new Integer(stopTimeHour).toString();
		ms = new Integer(stopTimeMinute).toString();
		if (stopTimeHour < 10) {
			hs = "0" + hs;
		}
		if (stopTimeMinute < 10) {
			ms = "0" + ms;
		}
		this.mStopPlayingMusic.setSummary(stopPlayingMusicSummery + hs + ":"
				+ ms);
		System.out.println();
	}

	protected void saveSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences("Setting",
				MODE_PRIVATE);

		Editor editor = sp.edit();
		editor.putInt("PROGRESS_OF_BATTERY", prog);
		editor.putInt("START_TIME_HOUR", startTimeHour);
		editor.putInt("START_TIME_MUNITE", startTimeMinute);
		editor.putInt("STOP_TIME_HOUR", stopTimeHour);
		editor.putInt("STOP_TIME_MINUTE", stopTimeMinute);
		editor.putBoolean("STOP_SLIDING_SHOW", stopSlidingShow);
		editor.putBoolean("STOP_PLAYING_MUSIC", stopPlayingMusic);
		editor.putBoolean("QUIT_FRAMUSIC", quitFramusic);
		editor.putBoolean("ALARM_ON_OFF", alarmOnOff);
		editor.commit();

		// private int prog = 0;//battery bottom line
		// private int startTimeHour = 0;//alarm start time
		// private int startTimeMinute = 0;
		// private int stopTimeHour = 0;//alarm stop time
		// private int stopTimeMinute = 0;
		// private boolean stopSlidingShow = false;
		// private boolean stopPlayingMusic = false;
		// private boolean quitFramusic = false;
		// private boolean alarmOnOff = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//saveSharedPreferences();
			
			Intent it = new Intent(mContext, DisplayBackgroundMusicActivity.class);
			startActivity(it);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
