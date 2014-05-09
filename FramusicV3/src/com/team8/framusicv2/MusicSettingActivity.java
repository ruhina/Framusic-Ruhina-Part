package com.team8.framusicv2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicSettingActivity extends Activity {
	private Context mContext;
	private boolean mFirstTimeOpen;
	private String mWhoCalledMe;
	private ArrayList<String> allImages = new ArrayList<String>();

	ListView musiclist;
	Cursor musiccursor;
	int music_column_index;
	int count;
	MediaPlayer mMediaPlayer;
	ArrayList<String> mMusicPaths = new ArrayList<String>();
	ArrayList<String> mMusicNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.screen_music_setting);
		mContext = this;
		getSharedPreferences();
		processExtraData();

		Button b = (Button) findViewById(R.id.button6);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,
						DisplayBackgroundMusicActivity.class);
				Bundle b = new Bundle();
				b.putStringArrayList("PicList", allImages);
				intent.putExtra("Bundle", b);
				startActivity(intent);

				finish();
			}

		});
		init_phone_music_grid();
	}

	private void processExtraData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		// use the data received here
		getSharedPreferences();
		Bundle b = intent.getBundleExtra("CALLING_INFO");
		if (b != null) {
			mWhoCalledMe = b.getString("WHO_CALLED_ME");
		}
		b = intent.getBundleExtra("Bundle");
		if (b != null) {
			allImages = b.getStringArrayList("PicList");
		}
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

	private void init_phone_music_grid() {
		System.gc();
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE };
		musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);

		while (musiccursor.moveToNext()) {
			music_column_index = musiccursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
			String id = musiccursor.getString(music_column_index);

			if (id.contains(".mp3")) {
				String fullpath = musiccursor.getString(musiccursor
						.getColumnIndex(MediaStore.Audio.Media.DATA));

				mMusicPaths.add(fullpath);
				mMusicNames.add("  " + id);
			}
		}
		System.out.println();

		count = mMusicNames.size() - 1;
		musiclist = (ListView) findViewById(R.id.PhoneMusicList);
		musiclist.setAdapter(new MusicAdapter(getApplicationContext()));

		musiclist.setOnItemClickListener(musicgridlistener);

	}

	private OnItemClickListener musicgridlistener = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {
			System.gc();
			music_column_index = musiccursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			musiccursor.moveToPosition(position);
			String filename = musiccursor.getString(music_column_index);

			try {
				if(mFirstTimeOpen == true){
					Intent intent = new Intent(mContext,
							DisplayBackgroundMusicActivity.class);

					Bundle b = new Bundle();
					b.putStringArrayList("MusicList", mMusicPaths);
					int currentMusic = position;
					b.putInt("CurrenMusic", currentMusic);
					b.putStringArrayList("PicList", allImages);

					intent.putExtra("MusicBundle", b);
					startActivity(intent);
					musiccursor.close();
					System.out.print(currentMusic);
					finish();
				}
				else{
					Intent intent = new Intent(mContext,
							DisplayBackgroundMusicActivity.class);

					Bundle b = new Bundle();
					b.putStringArrayList("MusicList", mMusicPaths);
					// String currentMusicPath =
					// musiccursor.getString(musiccursor
					// .getColumnIndex(MediaStore.Audio.Media.DATA));
					int currentMusic = position;
					b.putInt("CurrenMusic", currentMusic);

					intent.putExtra("MusicBundle", b);
					startActivity(intent);
					musiccursor.close();
					System.out.print(currentMusic);
					finish();
				}
			} catch (Exception e) {

			}
		}
	};

	public class MusicAdapter extends BaseAdapter {
		private Context mContext;

		public MusicAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// System.gc();
			if (convertView == null) {
				convertView = new TextView(mContext.getApplicationContext());
			}

			TextView tv = (TextView) convertView;
			tv = (TextView) convertView;

			tv.setText(mMusicNames.get(position));
			tv.setTextSize(20);

			return tv;

		}
	}
}
