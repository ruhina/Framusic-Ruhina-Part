package com.team8.framusicv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class MultiPhotoSelectActivity extends BaseActivity {

	private ArrayList<String> imageUrls;
	private DisplayImageOptions options;
	private ImageAdapter imageAdapter;
	private String mWhoCalledMe;
	private Context mContext;
	private Boolean mFirstTimeOpen;
	private Cursor imageCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);
		mContext = this;

		processExtraData();
		final String[] columns = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID };
		
		imageCursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, MediaStore.Images.Media.DATA + " DESC");

		this.imageUrls = new ArrayList<String>();

		while (imageCursor.moveToNext()) {
			int dataColumnIndex = imageCursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			
			String filename = imageCursor.getString(dataColumnIndex);
			File f = new File(filename);
			long fileSize = f.length() / 1000;

			if (fileSize > 30) {
				imageUrls.add(imageCursor.getString(dataColumnIndex));

				System.out.println("=====> Array path => " + imageUrls.get(imageUrls.size() - 1));
			}
		}

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stub_image)
				.showImageForEmptyUri(R.drawable.image_for_empty_url)
				.cacheInMemory().cacheOnDisc().build();

		imageAdapter = new ImageAdapter(this, imageUrls);

		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(imageAdapter);
		
	}

	@Override
	protected void onStop() {
		imageLoader.stop();
		super.onStop();
	}

	public void btnChoosePhotosClick(View v) {
		ArrayList<String> selectedItems = imageAdapter.getCheckedItems();

		if (this.mFirstTimeOpen == true) {
			Intent i = new Intent(mContext, MusicSettingActivity.class);
			Bundle b = new Bundle();

			if (selectedItems.size() == 0) {
				// send allImages
				b.putStringArrayList("PicList", imageUrls);
			} else {
				// send selected Images
				b.putStringArrayList("PicList", selectedItems);
			}
			i.putExtra("Bundle", b);
			startActivity(i);
		} else {
			Intent i = new Intent(mContext,
					DisplayBackgroundMusicActivity.class);
			Bundle b = new Bundle();

			if (selectedItems.size() == 0) {
				// send allImages
				b.putStringArrayList("PicList", imageUrls);
			} else {
				// send selected Images
				b.putStringArrayList("PicList", selectedItems);
			}
			i.putExtra("Bundle", b);
			startActivity(i);
		}
		imageCursor.close();
		finish();
	}

	/*
	 * private void startImageGalleryActivity(int position) { Intent intent =
	 * new Intent(this, ImagePagerActivity.class); intent.putExtra(Extra.IMAGES,
	 * imageUrls); intent.putExtra(Extra.IMAGE_POSITION, position);
	 * startActivity(intent); }
	 */

	public class ImageAdapter extends BaseAdapter {

		ArrayList<String> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;

		public ImageAdapter(Context context, ArrayList<String> imageList) {
			// TODO Auto-generated constructor stub
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			mList = new ArrayList<String>();
			this.mList = imageList;

		}

		public ArrayList<String> getCheckedItems() {
			ArrayList<String> mTempArry = new ArrayList<String>();

			for (int i = 0; i < mList.size(); i++) {
				if (mSparseBooleanArray.get(i)) {
					mTempArry.add(mList.get(i));
				}
			}

			return mTempArry;
		}

		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row_multiphoto_item,
						null);
			}

			CheckBox mCheckBox = (CheckBox) convertView
					.findViewById(R.id.checkBox1);
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);

			imageLoader.init(ImageLoaderConfiguration
					.createDefault(getBaseContext()));
			imageLoader.displayImage("file://" + imageUrls.get(position),
					imageView, options, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(Bitmap loadedImage) {
							// Animation anim = AnimationUtils.loadAnimation(
							// MultiPhotoSelectActivity.this,
							// R.anim.fade_in);
							// imageView.setAnimation(anim);
							// anim.start();
						}

					});

			mCheckBox.setTag(position);
			mCheckBox.setChecked(mSparseBooleanArray.get(position));
			mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

			return convertView;
		}

		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mSparseBooleanArray.put((Integer) buttonView.getTag(),
						isChecked);
			}
		};
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
}