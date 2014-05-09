package com.team8.framusicv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

public class SinglePhotoSelectActivity extends BaseActivity {

	private ArrayList<String> imageUrls;
	private DisplayImageOptions options;
	private ImageAdapter imageAdapter;
	private int checkedItemCount;
	private String whoCalledMe;
	private int position;
	private static final int IMAGE_CUT = 2; // 裁剪图片

	private int mAspectX;
	private int mAspectY;
	private int mOutputX;
	private int mOutputY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		Intent intent = this.getIntent();

		mAspectX = intent.getIntExtra("aspectX", 1);
		mAspectY = intent.getIntExtra("aspectY", 1);
		mOutputX = intent.getIntExtra("outputX", 80);
		mOutputY = intent.getIntExtra("outputY", 80);

		Bundle b = intent.getBundleExtra("Layout");
		whoCalledMe = b.getString("WhoCalledMe");
		position = b.getInt("position");

		final String[] columns = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
		@SuppressWarnings("deprecation")
		Cursor imagecursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy + " DESC");

		this.imageUrls = new ArrayList<String>();

		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			String filename = imagecursor.getString(dataColumnIndex);
			File f = new File(filename);
			long fileSize = f.length() / 1000;

			if (fileSize > 30) {
				imageUrls.add(imagecursor.getString(dataColumnIndex));
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
	protected void onStart() {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Choose pic Start", Toast.LENGTH_LONG).show();

		super.onStart();
	}

	@Override
	protected void onStop() {
		imageLoader.stop();
//		Toast.makeText(this, "Choose pic Stopped", Toast.LENGTH_LONG).show();
		super.onStop();
	}

	public void btnChoosePhotosClick(View v) {
		ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
		if (selectedItems.size() == 0) {
			Toast.makeText(SinglePhotoSelectActivity.this,
					"At least select one item!", Toast.LENGTH_SHORT).show();
		} else {
			// send intent back with picture path and sequence
			Toast.makeText(SinglePhotoSelectActivity.this,
					selectedItems.get(0), Toast.LENGTH_LONG).show();
			startActivityToLayouts(this.whoCalledMe, this.position,
					selectedItems.get(0));
		}
	}

	/*
	 * private void startImageGalleryActivity(int position) { Intent intent =
	 * new Intent(this, ImagePagerActivity.class); intent.putExtra(Extra.IMAGES,
	 * imageUrls); intent.putExtra(Extra.IMAGE_POSITION, position);
	 * startActivity(intent); }
	 */
	// 获取裁剪图片意图的方法
	private Intent getImageClipIntent(String PicPath) {
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		Intent intent = new Intent("com.android.camera.action.CROP");
		// 实现对图片的裁剪,必须要设置图片的属性和大小
		Uri uri = Uri.parse("file://" + PicPath);
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "false");// 设置可以滑动选选择区域的属性,注意这里是字符串"true"
		intent.putExtra("aspectX", mAspectX);
		intent.putExtra("aspectY", mAspectY);
		intent.putExtra("outputX", mOutputX);
		intent.putExtra("outputY", mOutputY);
		intent.putExtra("return-data", true);

		// intent.putExtra("scale", true);
		// intent.putExtra("outputFormat",
		// Bitmap.CompressFormat.PNG.toString());

		return intent;
	}

	private void startActivityToLayouts(String whoCalledMe2, int position2,
			String PicPath) {
		// TODO Auto-generated method stub
		Intent intentCrop = getImageClipIntent(PicPath);
		startActivityForResult(intentCrop, IMAGE_CUT);
	}

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
			final ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			imageLoader.init(ImageLoaderConfiguration
					.createDefault(getBaseContext()));
			imageLoader.displayImage("file://" + imageUrls.get(position),
					imageView, options, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									SinglePhotoSelectActivity.this,
									R.anim.fade_in);
							imageView.setAnimation(anim);
							anim.start();
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
				if (mSparseBooleanArray.get((Integer) buttonView.getTag())) {
					mSparseBooleanArray.put((Integer) buttonView.getTag(),
							isChecked);
					checkedItemCount--;
				} else {
					if (checkedItemCount > 0) {
						Toast.makeText(mContext, "Can only select one item",
								Toast.LENGTH_LONG).show();
						buttonView.setChecked(false);

					} else {
						mSparseBooleanArray.put((Integer) buttonView.getTag(),
								isChecked);
						checkedItemCount++;
					}
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Toast.makeText(this, "return result", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == IMAGE_CUT) { // 裁剪图片
				// 一定要和"return-data"返回的标签"data"一致
				Bitmap bmp = data.getParcelableExtra("data");
				if (bmp != null) {
					bmp = Bitmap.createScaledBitmap(bmp, mOutputX, mOutputY,
							true);
					long a = System.currentTimeMillis();

					String PicPath = saveTempBitmap(new Long(a).toString(), bmp);

					if (whoCalledMe.contains("Layout3Activity")) {
						Intent i = new Intent(this, Layout3Activity.class);
						Bundle b = new Bundle();
						b.putInt("position", position);
						b.putString("PicPath", PicPath);
						i.putExtra("Bundle", b);
						startActivity(i);
						finish();
					} else if (whoCalledMe.contains("Layout2Activity")) {
						Intent i = new Intent(this, Layout2Activity.class);
						Bundle b = new Bundle();
						b.putInt("position", position);
						b.putString("PicPath", PicPath);
						i.putExtra("Bundle", b);
						startActivity(i);
						finish();
					} else if (whoCalledMe.contains("Layout1Activity")) {
						Intent i = new Intent(this, Layout1Activity.class);
						Bundle b = new Bundle();
						b.putInt("position", position);
						b.putString("PicPath", PicPath);
						i.putExtra("Bundle", b);
						startActivity(i);
						finish();
					}

				}
			}
		}
	}

	public String saveTempBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ bitName + ".png");
//		Toast.makeText(this, f.getAbsolutePath(), Toast.LENGTH_LONG).show();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return f.getAbsolutePath();
	}

}