package com.team8.framusicv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team8.framusicv2.musicplay.MusicPlayer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DisplayBackgroundMusicActivity extends Activity {
	private Context mContext = null;
	private String mWhoCalledMe;
	private boolean mFirstTimeOpen;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPreferenceTitle;

	private int[] images = new int[] { R.drawable.ic_action_settings,
			R.drawable.ic_action_settings, R.drawable.ic_action_settings,
			R.drawable.ic_action_settings, R.drawable.ic_action_help,
			R.drawable.ic_action_about };

	private int[] imagesLine = new int[] { R.drawable.line, R.drawable.line,
			R.drawable.line, R.drawable.line, R.drawable.line, R.drawable.line };

	private boolean mActionBarOn;
	private ActionBar mActionBar;

	private Button mShuffle;
	private Button mPrevious;
	private Button mPlayStopMusic;
	private Button mNext;
	private Button mRepeat;

	private BroadcastReceiver batteryLevelRcvr;
	private IntentFilter batteryLevelFilter;

	private int prog = 0;
	private boolean alarmOnOff;
	private int startTimeHour;
	private int startTimeMinute;
	private int stopTimeHour;
	private int stopTimeMinute;

	private PendingIntent mAlarmMusicStopSender;
	private PendingIntent mAlarmMusicStartSender;
	boolean playing = true;
	private Button btn_play_or_pause;
	private Button btn_previous;
	private Button btn_next;
	private MusicPlayer musicPlayer;
	private boolean timeMusicStartChanged = false;
	private boolean timeMusicStopChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.screen_display_background_music);

		mContext = this;

		if (musicPlayer == null) {
			musicPlayer = new MusicPlayer(mContext);
		}
		this.imageView = (ImageView) this.findViewById(R.id.imageView);
		this.getSharedPreferences();
		this.setFromPreferencesValue();
		this.saveSharedPreferences();

		/* copy from framusicp */
		mTitle = mDrawerTitle = getTitle();
		mPreferenceTitle = getResources().getStringArray(
				R.array.preference_array);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		setAdapter();
		// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mPreferenceTitle));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer

		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {

			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				if (!mActionBarOn) {
					showAll();
				}
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				mActionBar = getActionBar();
				if (mActionBarOn) {
					hideAll();
				}
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mActionBarOn = true;
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.content_frame);
		rl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (mActionBarOn) {
					hideAll();
				} else {
					showAll();
				}
				return false;
			}
		});

		mHandler.sendEmptyMessageDelayed(HIDEALLELEMENTINSCREEN, 5000);

		monitorBatteryState();

		/* copy from framusicp */

		Button reset = (Button) findViewById(R.id.reset);
		reset.setVisibility(reset.GONE);
//		reset.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mFirstTimeOpen = true;
//				saveSharedPreferences();
//			}
//
//		});

		mPlayStopMusic = (Button) findViewById(R.id.play_stop_music);
		mPrevious = (Button) findViewById(R.id.previous);
		mNext = (Button) findViewById(R.id.next);
		this.mShuffle = (Button) findViewById(R.id.shuffle);
		this.mRepeat = (Button) findViewById(R.id.repeat);

		mPlayStopMusic.setOnClickListener(musicListener);
		mPrevious.setOnClickListener(musicListener);
		mNext.setOnClickListener(musicListener);
		this.mShuffle.setOnClickListener(musicListener);
		this.mRepeat.setOnClickListener(musicListener);

		processExtraData();
		try {
			if (this.musicList != null) {
				if (!musicPlayer.isPlaying()) {
					musicPlayer.play();
					mPlayStopMusic
							.setBackgroundResource(R.drawable.ic_action_pause);
					Toast.makeText(mContext, "Playing", Toast.LENGTH_LONG)
							.show();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		receiverAlarm = new MyReceiver(musicPlayer);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.musicalarm");
		DisplayBackgroundMusicActivity.this.registerReceiver(receiverAlarm,
				filter);

		String svcName = Context.NOTIFICATION_SERVICE;

		notificationManager = (NotificationManager) getSystemService(svcName);


		try {
			updateUI();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ImageView imageView;
	int ImageCurrentCount = 0;
	int imgid[] = { R.drawable.sample_0, R.drawable.sample_1,
			R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4,
			R.drawable.sample_5, R.drawable.sample_6 };
	RefreshHandler refreshHandler = new RefreshHandler();
	Button ctrl;

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			try {

				DisplayBackgroundMusicActivity.this.updateUI();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}

	};

	AnimationSet animationSet;

	public void updateUI() throws FileNotFoundException, IOException {

		refreshHandler.sleep(7010);
		if (!activeStopSlidingShow) {
			animationSet = new AnimationSet(true);
			Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
			Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
			animationSet.addAnimation(fadeInAnimation);
			animationSet.addAnimation(fadeOutAnimation);
			fadeInAnimation.setDuration(1000);
			fadeInAnimation.setStartOffset(0);
			fadeOutAnimation.setDuration(900);
			fadeOutAnimation.setStartOffset(6100);
			imageView.startAnimation(animationSet);

			if (this.picList != null) {
				if (this.picList.size() > 0) {
					File file = new File(this.picList.get(ImageCurrentCount));
					Uri uri = Uri.parse("file://" + file.getPath());
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), uri);
					imageView.setImageBitmap(bitmap);
					ImageCurrentCount = (ImageCurrentCount + 1)
							% this.picList.size();
				} else {
					imageView.setImageResource(imgid[ImageCurrentCount]);
					ImageCurrentCount = (ImageCurrentCount + 1) % imgid.length;
				}
			} else {
				imageView.setImageResource(imgid[ImageCurrentCount]);
				ImageCurrentCount = (ImageCurrentCount + 1) % imgid.length;
			}
		}
	}

	private OnClickListener musicListener = new OnClickListener() {

		public void onClick(View v) {
			Button button = (Button) v;
			try {
				switch (v.getId()) {
				case R.id.play_stop_music:
					if (musicPlayer.isPlaying()) {
						musicPlayer.pause();
						playing = musicPlayer.isPlaying();
						mPlayStopMusic
								.setBackgroundResource(R.drawable.ic_action_play);
						Toast.makeText(mContext, "Pause", Toast.LENGTH_LONG)
								.show();
					} else {
						musicPlayer.play();
						stopPlayingMusic= false;
						playing = musicPlayer.isPlaying();
						mPlayStopMusic
								.setBackgroundResource(R.drawable.ic_action_pause);
						Toast.makeText(mContext, "Playing", Toast.LENGTH_LONG)
								.show();
					}
					break;
				case R.id.previous:
					mPlayStopMusic
							.setBackgroundResource(R.drawable.ic_action_pause);
					musicPlayer.previous();
					break;
				case R.id.next:
					mPlayStopMusic
							.setBackgroundResource(R.drawable.ic_action_pause);
					musicPlayer.next();
					break;
				case R.id.shuffle:
					Collections.shuffle(musicList);
					musicPlayer.setMusicList(musicList);
					break;
				case R.id.repeat:
					musicPlayer.setRepeat();
					break;
				}
			} catch (IOException e) {
				Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT);
			}
		}
	};
	private boolean stopSlidingShow;
	private boolean stopPlayingMusic;
	private boolean quitFramusic;
	private ArrayList<String> picList;
	private int currentMusic;
	private ArrayList<String> musicList;

	private void processExtraData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		// use the data received here

		Bundle b = intent.getBundleExtra("Bundle");
		if (b != null) {
			picList = b.getStringArrayList("PicList");

			try {
				ImageCurrentCount = 0;
				updateUI();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		b = intent.getBundleExtra("MusicBundle");
		if (b != null) {
			ArrayList<String> tPicList = b.getStringArrayList("PicList");
			if(tPicList != null){
				picList = tPicList;
				
				try {
					ImageCurrentCount = 0;
					updateUI();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			currentMusic = b.getInt("CurrenMusic");
			musicList = b.getStringArrayList("MusicList");
			if (musicList != null) {
				this.musicPlayer.setCurrentMusic(currentMusic);
				this.musicPlayer.setMusicList(musicList);
				try {
					musicPlayer.play();
					stopPlayingMusic= false;
					mPlayStopMusic
							.setBackgroundResource(R.drawable.ic_action_pause);
					Toast.makeText(mContext, "Playing", Toast.LENGTH_LONG)
							.show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (alarmOnOff == true) {
			startMusicAlarm();
			stopMusicAlarm();
		} else {
			stopAlarm();
		}
	}

	private void processNewExtraData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		// use the data received here
		this.getSharedPreferences();
		this.setFromPreferencesValue();
		this.saveSharedPreferences();

		Bundle b = intent.getBundleExtra("Bundle");
		if (b != null) {
			picList = b.getStringArrayList("PicList");

			try {
				ImageCurrentCount = 0;
				updateUI();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		b = intent.getBundleExtra("MusicBundle");
		if (b != null) {
			ArrayList<String> tPicList = b.getStringArrayList("PicList");
			if(tPicList != null){
				picList = tPicList;
				
				try {
					ImageCurrentCount = 0;
					updateUI();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			currentMusic = b.getInt("CurrenMusic");
			musicList = b.getStringArrayList("MusicList");
			if (musicList != null) {
				this.musicPlayer.setCurrentMusic(currentMusic);
				this.musicPlayer.setMusicList(musicList);
				try {
					musicPlayer.play();
					mPlayStopMusic
							.setBackgroundResource(R.drawable.ic_action_pause);
					Toast.makeText(mContext, "Playing", Toast.LENGTH_LONG)
							.show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// Bundle b = intent.getBundleExtra("CALLING_INFO");
		// // ArrayList<String> selectedPhotos=
		// // b.getStringArrayList("photoschosen");
		// if (b != null) {
		// mWhoCalledMe = b.getString("WHO_CALLED_ME");
		// }
		/*if (stopPlayingMusic) {
			musicPlayer.pause();
			playing = musicPlayer.isPlaying();
			mPlayStopMusic.setBackgroundResource(R.drawable.ic_action_play);
			Toast.makeText(mContext, "Pause", Toast.LENGTH_LONG).show();
		}*/
		if (alarmOnOff == true) {
			startMusicAlarm();
			stopMusicAlarm();
		} else {
			stopAlarm();
		}
	}

	protected void getSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences("Setting",
				MODE_PRIVATE);

		mFirstTimeOpen = sp.getBoolean("FIRST_TIME_OPEN", true);
		prog = sp.getInt("PROGRESS_OF_BATTERY", prog);

		int tempStartTimeHour = sp.getInt("START_TIME_HOUR", 0);
		int tempStartTimeMinute = sp.getInt("START_TIME_MUNITE", 0);
		int tempStopTimeHour = sp.getInt("STOP_TIME_HOUR", 0);
		int tempStopTimeMinute = sp.getInt("STOP_TIME_MINUTE", 0);

		if (startTimeHour != tempStartTimeHour
				|| startTimeMinute != tempStartTimeMinute) {
			startTimeHour = tempStartTimeHour;
			startTimeMinute = tempStartTimeMinute;
			timeMusicStartChanged = true;
		} else {
			timeMusicStartChanged = false;
		}

		if (stopTimeHour != tempStopTimeHour
				|| stopTimeMinute != tempStopTimeMinute) {
			stopTimeHour = tempStopTimeHour;
			stopTimeMinute = tempStopTimeMinute;
			timeMusicStopChanged = true;
		} else {
			timeMusicStopChanged = false;
		}

		stopSlidingShow = sp.getBoolean("STOP_SLIDING_SHOW", stopSlidingShow);
		stopPlayingMusic = sp
				.getBoolean("STOP_PLAYING_MUSIC", stopPlayingMusic);
		quitFramusic = sp.getBoolean("QUIT_FRAMUSIC", quitFramusic);
		alarmOnOff = sp.getBoolean("ALARM_ON_OFF", alarmOnOff);
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
		editor.commit();
	}

	/* copy from framusicp */
	public void hideAll() {
		Animation fadeout = AnimationUtils.loadAnimation(mContext,
				R.anim.fadeout);
		mActionBar.hide();

		LinearLayout musicPlayBar = (LinearLayout) findViewById(R.id.musicControlBar);
		musicPlayBar.startAnimation(fadeout);
		musicPlayBar.setVisibility(View.INVISIBLE);

		mActionBarOn = false;
		mHandler.removeMessages(1);
	}

	final int HIDEALLELEMENTINSCREEN = 1;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HIDEALLELEMENTINSCREEN:
				hideAll();
				break;

			default:
				break;
			}
		};
	};

	public void showAll() {
		Animation fadein = AnimationUtils
				.loadAnimation(mContext, R.anim.fadein);
		mActionBar.show();

		mActionBarOn = true;

		LinearLayout musicPlayBar = (LinearLayout) findViewById(R.id.musicControlBar);
		musicPlayBar.startAnimation(fadein);
		musicPlayBar.setVisibility(View.VISIBLE);

		mHandler.removeMessages(HIDEALLELEMENTINSCREEN);
		mHandler.sendEmptyMessageDelayed(HIDEALLELEMENTINSCREEN, 5000);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		// case R.id.:
		// // create intent to perform web search for this planet
		// Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		// intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
		// // catch event that there's no activity to handle intent
		// if (intent.resolveActivity(getPackageManager()) != null) {
		// startActivity(intent);
		// } else {
		// Toast.makeText(this, R.string.app_not_available,
		// Toast.LENGTH_LONG).show();
		// }
		// return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		// update selected item and title, then close the drawer
		switch (position) {
		case 0:// Gallery
			Intent intentMultiPhotoSelectActivity = new Intent(mContext,
					MultiPhotoSelectActivity.class);
			Bundle bMultiPhotoSelectActivity = new Bundle();
			bMultiPhotoSelectActivity.putString("WHO_CALLED_ME",
					DisplayBackgroundMusicActivity.class.toString());
			intentMultiPhotoSelectActivity.putExtra("CALLING_INFO",
					bMultiPhotoSelectActivity);
			this.saveSharedPreferences();
			startActivity(intentMultiPhotoSelectActivity);

			break;
		case 1:// Layout
			Intent intentChooseLayoutActivity = new Intent(mContext,
					LayoutSettingActivity.class);
			Bundle bChooseLayoutActivity = new Bundle();
			bChooseLayoutActivity.putString("WHO_CALLED_ME",
					DisplayBackgroundMusicActivity.class.toString());
			intentChooseLayoutActivity.putExtra("CALLING_INFO",
					bChooseLayoutActivity);
			this.saveSharedPreferences();
			startActivity(intentChooseLayoutActivity);

			break;
		case 2:// Playlist
			Intent intentMusicSettingActivity = new Intent(mContext,
					MusicSettingActivity.class);
			Bundle bMusicSettingActivity = new Bundle();
			bMusicSettingActivity.putString("WHO_CALLED_ME",
					DisplayBackgroundMusicActivity.class.toString());
			intentMusicSettingActivity.putExtra("CALLING_INFO",
					bMusicSettingActivity);
			this.saveSharedPreferences();
			startActivity(intentMusicSettingActivity);

			break;
		case 3:
			Intent intentSettingPreference = new Intent(mContext,
					SettingPreference.class);
			this.saveSharedPreferences();
			startActivity(intentSettingPreference);
			break;
		case 4:// Wizard
			Intent intentWizardActivity = new Intent(mContext,
					WizardActivity.class);
			Bundle bWizardActivity = new Bundle();
			bWizardActivity.putString("WHO_CALLED_ME",
					DisplayBackgroundMusicActivity.class.toString());
			intentWizardActivity.putExtra("CALLING_INFO", bWizardActivity);

			this.mFirstTimeOpen = true;
			this.saveSharedPreferences();

			startActivity(intentWizardActivity);

			break;
		case 5:
			Intent intentAboutPreference = new Intent(mContext,
					AboutPreference.class);
			this.saveSharedPreferences();
			startActivity(intentAboutPreference);
			break;
		}
		mDrawerList.setItemChecked(position, true);
		// setTitle(mPreferenceTitle[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void setAdapter() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mPreferenceTitle.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("preferenceTitle", this.mPreferenceTitle[i]);
			listItem.put("images", images[i]);
			listItem.put("imageLine", imagesLine[i]);
			listItems.add(listItem);
		}
		PreferenceSimpleAdapter adapter = new PreferenceSimpleAdapter(mContext,
				listItems, R.layout.drawer_list_element, new String[] {
						"preferenceTitle", "images", "imageLine" }, new int[] {
						R.id.SettingTitle, R.id.SettingImage,
						R.id.SettingLineImage });

		mDrawerList.setAdapter(adapter);
	}

	private boolean activeStopSlidingShow = false ;
	private void monitorBatteryState() {
		batteryLevelRcvr = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				StringBuilder sb = new StringBuilder();
				int rawlevel = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				int status = intent.getIntExtra("status", -1);
				int health = intent.getIntExtra("health", -1);
				int level = -1; // percentage, or -1 for unknown
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}
				sb.append("The phone");
				if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
					sb.append("'s battery feels very hot!");
				} else {
					switch (status) {
					case BatteryManager.BATTERY_STATUS_UNKNOWN:
						sb.append("no battery.");
						break;
					case BatteryManager.BATTERY_STATUS_CHARGING:
						sb.append("'s battery");
						if (level <= 33)
							sb.append(" is charging, battery level is low"
									+ "[" + level + "]");
						else if (level <= 84)
							sb.append(" is charging." + "[" + level + "]");
						else
							sb.append(" will be fully charged.");
						break;
					case BatteryManager.BATTERY_STATUS_DISCHARGING:
					case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
						if (level == 0)
							sb.append(" needs charging right away.");
						else if (level > 0 && level <= prog) // add what to do
																// in
																// this part for
																// low
																// battery
						{

							sb.append(" is about ready to be recharged, battery level is low"
									+ "[" + level + "]");
							if (stopSlidingShow) {
								// add code for stop sliding show
								sb.append(": stop sliding show");
								Toast t = Toast.makeText(mContext, sb,
										Toast.LENGTH_LONG);
								activeStopSlidingShow = true;
								t.setGravity(Gravity.CENTER, 0, 0);
								t.show();
							}
							if (stopPlayingMusic) {
								sb.append(": stop music");
								musicPlayer.pause();
								mPlayStopMusic
										.setBackgroundResource(R.drawable.ic_action_play);
								Toast t = Toast.makeText(mContext, sb,
										Toast.LENGTH_LONG);
								t.setGravity(Gravity.CENTER, 0, 0);
								t.show();
							}
							if (quitFramusic) {
								sb.append(": quit app");
								Toast t = Toast.makeText(mContext, sb,
										Toast.LENGTH_LONG);
								t.setGravity(Gravity.CENTER, 0, 0);
								t.show();
								quitFramusic = false;
								saveSharedPreferences();
								finish();
							}
						}

						else
							sb.append("'s battery level is" + "[" + level + "]");
						break;
					case BatteryManager.BATTERY_STATUS_FULL:
						sb.append(" is fully charged.");
						break;
					default:
						sb.append("'s battery is indescribable!");
						break;
					}
				}
				// sb.append(' ');
				// Toast t = Toast.makeText(mContext, sb, Toast.LENGTH_LONG);
				// t.setGravity(Gravity.CENTER, 0, 0);
				// t.show();
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelRcvr, batteryLevelFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(batteryLevelRcvr);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will
							// return the old one
		processNewExtraData();
	}

	private void stopAlarm() {
		// TODO Auto-generated method stub
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (mAlarmMusicStopSender != null) {
			am.cancel(mAlarmMusicStopSender);
			mAlarmMusicStopSender = null;

		}
		if (mAlarmMusicStartSender != null) {
			am.cancel(mAlarmMusicStartSender);
			mAlarmMusicStartSender = null;

		}
	}

	private void startMusicAlarm() {// start the alarm for starting music
		// TODO Auto-generated method stub
		// add notification bar
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent iStart = new Intent(mContext, MusicAlarmControl.class);
		iStart.putExtra("TYPE", "toStartMusic");

		if (timeMusicStartChanged) {
			mAlarmMusicStartSender = PendingIntent.getService(mContext, 0,
					iStart, 1);
			Date t = new Date();
			t.setTime(System.currentTimeMillis());
			t.setHours(this.startTimeHour);
			t.setMinutes(this.startTimeMinute);
			t.setSeconds(0);
			if (t.getTime() > System.currentTimeMillis()) {
				Toast.makeText(mContext, "Alarm for starting music is set",
						Toast.LENGTH_SHORT).show();
				am.set(AlarmManager.RTC_WAKEUP, t.getTime(),
						mAlarmMusicStartSender);
				triggerNotification(
						customLayoutNotification("Music alarm",
								"Music will start at", t.toString(), ""),
						NOTIFICATION_REF_STARTMUSICALARM);
			}
		}
	}

	private void stopMusicAlarm() {// start the alarm for stopping music
		// TODO Auto-generated method stub
		// add notification bar
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent iStop = new Intent(mContext, MusicAlarmControl.class);
		iStop.putExtra("TYPE", "toStopMusic");

		if (timeMusicStopChanged) {
			mAlarmMusicStopSender = PendingIntent.getService(mContext, 1,
					iStop, 1);
			Date t = new Date();
			t.setTime(System.currentTimeMillis());
			t.setHours(this.stopTimeHour);
			t.setMinutes(this.stopTimeMinute);
			t.setSeconds(0);
			long a = t.getTime();
			long b = System.currentTimeMillis();
			if (a > b) {
				Toast.makeText(mContext, "Alarm for stop music is set",
						Toast.LENGTH_SHORT).show();
				am.set(AlarmManager.RTC_WAKEUP, t.getTime(),
						mAlarmMusicStopSender);
				triggerNotification(
						customLayoutNotification("Music alarm",
								"Music will stop at", t.toString(), ""),
						NOTIFICATION_REF_STOPMUSICALARM);
			}
		}
	}

	private MyReceiver receiverAlarm;

	public class MyReceiver extends BroadcastReceiver {
		MusicPlayer mp = new MusicPlayer();

		// receiver
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("OnReceiver");
			Bundle bundle = intent.getExtras();
			String myType = bundle.getString("MusicAlarmControl");

			if (myType.equals("toStartMusic")) {
				System.out.println(myType);
				if (!mp.isPlaying()) {
					try {
						mp.play();
						playing = musicPlayer.isPlaying();
						mPlayStopMusic
								.setBackgroundResource(R.drawable.ic_action_pause);
						Toast.makeText(mContext, "Playing", Toast.LENGTH_LONG)
								.show();
						cancelNotification(notificationManager,
								NOTIFICATION_REF_STARTMUSICALARM);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (myType.equals("toStopMusic")) {
				System.out.println(myType);
				mp.pause();
				cancelNotification(notificationManager,
						NOTIFICATION_REF_STOPMUSICALARM);
			}
		}

		public MyReceiver(MusicPlayer mp) {
			System.out.println("MyReceiver set mp");
			// constructor
			this.mp = mp;
		}
	}

	private static final String BUTTON_CLICK_ACTION = "com.team8.framusicv2.action.BUTTON_CLICK";
	NotificationManager notificationManager;

	private void triggerNotification(Notification.Builder builder,
			int NOTIFICATION_REF) {
		/**
		 * Listing 10-43: Triggering a Notification
		 */
		String svc = Context.NOTIFICATION_SERVICE;

		NotificationManager notificationManager = (NotificationManager) getSystemService(svc);

		Notification notification = builder.getNotification();

		notificationManager.notify(NOTIFICATION_REF, notification);
	}

	private Notification.Builder customLayoutNotification(String notification,
			String title, String subtitle, String info) {
		Notification.Builder builder = new Notification.Builder(
				DisplayBackgroundMusicActivity.this);

		Intent newIntent = new Intent(BUTTON_CLICK_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				DisplayBackgroundMusicActivity.this, 2, newIntent, 0);
		Bitmap myIconBitmap = null; // TODO Obtain Bitmap

		/**
		 * Listing 10-36: Applying a custom layout to the Notification status
		 * window
		 */
		builder.setSmallIcon(R.drawable.ic_launcher).setTicker(notification)
				.setWhen(System.currentTimeMillis()).setContentTitle(title)
				.setContentText(subtitle).setContentInfo(info)
				.setLargeIcon(myIconBitmap).setContentIntent(pendingIntent);

		//
		return builder;
	}

	private void cancelNotification(NotificationManager notificationManager,
			int NOTIFICATION_REF) {
		/**
		 * Listing 10-46: Canceling a Notification
		 */
		System.out.println("close notification");
		notificationManager.cancel(NOTIFICATION_REF);

	}

	int NOTIFICATION_REF_STARTMUSICALARM = 1;
	int NOTIFICATION_REF_STOPMUSICALARM = 2;
}
