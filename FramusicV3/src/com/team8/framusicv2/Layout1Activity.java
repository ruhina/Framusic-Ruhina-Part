package com.team8.framusicv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Layout1Activity extends Activity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout2);
		mContext = this;
		findViews();
		setListeners();
//		Toast.makeText(mContext, "layout1Activity",
//				Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
//		Toast.makeText(mContext, "layout1Activity stop",
//				Toast.LENGTH_LONG).show();
		super.onStop();
	}

	Button btn1, btn2, btn_save;
	ImageView image1, image2;
	Bitmap bm1, bm2;

	private void findViews() {

		btn1 = (Button) findViewById(R.id.btn2_1);
		btn2 = (Button) findViewById(R.id.btn2_2);
		btn_save = (Button) findViewById(R.id.btn_save2);
		image1 = (ImageView) findViewById(R.id.image2_1);
		image2 = (ImageView) findViewById(R.id.image2_2);

	}

	private void setListeners() {
		btn1.setOnClickListener(button1);
		btn2.setOnClickListener(button2);
		btn_save.setOnClickListener(button_save);
	}

	private Button.OnClickListener button1 = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Layout1Activity.this,
					SinglePhotoSelectActivity.class);

			intent.putExtra("aspectX", 400);// 设置剪切框1:1比例的效果
			intent.putExtra("aspectY", 600);
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 600);

			Bundle bundle = new Bundle();
			bundle.putInt("position", 1);
			bundle.putString("WhoCalledMe", Layout1Activity.class.toString());
			intent.putExtra("Layout", bundle);
			startActivity(intent);
		}
	};

	private Button.OnClickListener button2 = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Layout1Activity.this,
					SinglePhotoSelectActivity.class);

			intent.putExtra("aspectX", 400);// 设置剪切框1:1比例的效果
			intent.putExtra("aspectY", 600);
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 600);

			Bundle bundle = new Bundle();
			bundle.putInt("position", 2);
			bundle.putString("WhoCalledMe", Layout1Activity.class.toString());
			intent.putExtra("Layout", bundle);
			startActivity(intent);
		}
	};

	private Button.OnClickListener button_save = new Button.OnClickListener() {
		public void onClick(View v) {
			if (bm1 != null && bm2 != null) {
				Bitmap s1 = add2BitmapHorizontal(bm1, bm2);
				
				mF1.delete();
				mF2.delete();
				
				long a = System.currentTimeMillis();
				saveMyBitmap(new Long(a).toString(), s1);
				Intent i = new Intent(mContext, LayoutSettingActivity.class);
				startActivity(i);
				finish();
			} else {
				Toast.makeText(mContext, "Need to add picture!",
						Toast.LENGTH_LONG).show();
			}
		}

	};

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
//		Toast.makeText(mContext, "new Intent layout1Activity",
//				Toast.LENGTH_LONG).show();
		Bundle b = intent.getBundleExtra("Bundle");
		try {
			setImage(b.getInt("position"), b.getString("PicPath"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	private File mF1;
	private File mF2;
	private void setImage(int position, String path) throws IOException {
		File file = new File(path);
		Uri uri = Uri.parse("file://" + file.getPath());
		switch (position) {
		case 1: {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			// bitmap = Bitmap.createScaledBitmap(bitmap, 400, 800, true);
			bm1 = bitmap;
			image1.setImageBitmap(bitmap);
			btn1.setVisibility(btn1.INVISIBLE);
			mF1 = file;
			break;
		}
		case 2: {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			// bitmap = Bitmap.createScaledBitmap(bitmap, 400, 800, true);
			bm2 = bitmap;
			image2.setImageBitmap(bitmap);
			btn2.setVisibility(btn2.INVISIBLE);
			mF2 = file;
			break;
		}
		}

	}

	private Bitmap add2BitmapHorizontal(Bitmap first, Bitmap second) {
		int width = first.getWidth() + second.getWidth() + 5;
		int height = Math.max(first.getHeight(), second.getHeight());
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(first, 0, 0, null);
		canvas.drawBitmap(second, first.getWidth() + 5, 0, null);
		return result;
	}

	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		/*File f = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera/" + bitName + ".png");
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
		}*/
		//mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		
		Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap,
				"", ""));
		Toast.makeText(mContext, "New image is saved to " + uri.getPath(), Toast.LENGTH_LONG).show();
/*
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
}
