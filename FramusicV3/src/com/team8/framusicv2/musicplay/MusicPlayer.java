package com.team8.framusicv2.musicplay;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.team8.framusicv2.R;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

public class MusicPlayer implements Parcelable {
	private String filename;
	private MediaPlayer mediaPlayer;
	private int currentPosition;
	private ArrayList<String> musicList = new ArrayList<String>();
	private int currentMusic = 0;
	private Context mContext;
	private boolean repeat;

	public MusicPlayer(Context mContext) {
		mediaPlayer = new MediaPlayer();
		this.mContext = mContext;

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				next();
			}
		});
		// File findMusic = new File(Environment.getExternalStorageDirectory(),
		// "");
		// if (findMusic.isDirectory()) {
		// if (findMusic.exists()) {
		// File[] files = findMusic.listFiles();
		// for (File f : files) {
		//
		// if (f.getAbsolutePath().toString().contains(".mp3")) {
		// System.out.println(f.getAbsolutePath().toString());
		// musicList.add(f);
		// }
		// ;
		// }
		// if (musicList.size() == 0) {
		// Toast.makeText(mContext, "No music!", Toast.LENGTH_LONG)
		// .show();
		// }
		// System.out.println("");
		// }
		// }
	}

	public MusicPlayer() {
		mediaPlayer = new MediaPlayer();
	}

	protected void destroyPlayer() {
		mediaPlayer.release();
	}

	public void pause() {
		// if (mediaPlayer.isPlaying()) {
		mediaPlayer.pause();
		// }
	}

	public void previous() {
		if (this.musicList.size() > 0) {
			currentMusic--;
			if (currentMusic < 0) {
				currentMusic = 0;
			}
			try {
				play();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void next() {
		if (this.musicList.size() > 0) {
			currentMusic++;
			if (currentMusic > musicList.size() - 1) {
				if (this.isRepeat()) {
					currentMusic = 0;
				} else {
					currentMusic = musicList.size() - 1;
				}
			}

			try {
				play();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * �Ѳ��Ŷ�������������ȶ���Ϊplay����
	 * 
	 * @throws IOException
	 */
	SoundPool sp;

	public void play() throws IOException {
		if (this.musicList.size() > 0) {
			File androidFile = new File(this.musicList.get(currentMusic));
			Toast.makeText(mContext, "Playing " + androidFile.getName(),
					Toast.LENGTH_LONG).show();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(androidFile.getAbsolutePath());

			mediaPlayer.prepare();
			// mediaPlayer.setLooping(true);
			mediaPlayer.start();
		} else {
			AssetFileDescriptor afd = mContext.getAssets().openFd("track1.mp3");
			Toast.makeText(mContext, "Playing Default Music", Toast.LENGTH_LONG)
					.show();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			mediaPlayer.prepare();
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		}
	}

	public void pauseByInterrupt() {
		// ����������ڲ��ţ���ȡ�����ֵĵ�ǰλ��
		if (mediaPlayer.isPlaying()) {
			currentPosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
	}

	public void resumeFromInterrupt() {
		if (currentPosition > 0 && filename != null) {
			try {
				play();
				mediaPlayer.seekTo(currentPosition);
				currentPosition = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

	public ArrayList<String> getMusicList() {
		return musicList;
	}

	public void setMusicList(ArrayList<String> musicList) {
		this.musicList = musicList;
	}

	public int getCurrentMusic() {
		return currentMusic;
	}

	public void setCurrentMusic(int currentMusic) {
		this.currentMusic = currentMusic;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat() {
		if (this.repeat == true) {
			this.repeat = false;
		} else {
			this.repeat = true;
		}
	}

	/**
	 * ��ϵͳɱ��activity�󣬵��û�����绰�󣬻����»ص����activity</br>
	 * ϵͳ���Զ������Ǵ������activity����ִ��onCreate()����</br>
	 * ��ִ��onRestoreInstanceState()���������������������</br> Bundle�б�������ݻָ�
	 */
	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// filename=savedInstanceState.getString("filename");
	// currentPosition=savedInstanceState.getInt("currentPosition");
	// }

	/**
	 * ��һЩλ�����������ʱ�򣬵���Activity���ɼ���</br>
	 * ϵͳ���ܻ�ɱ�������activity�������������ϵͳɱ��activity</br>
	 * ֮ǰ����һЩ��Ҫ������,���ﱣ��������ǲ��ŵ��ļ��͵�ǰ���ŵ�λ��
	 */
	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// outState.putString("filename", filename);
	// outState.putInt("currentPosition", currentPosition);
	// }

}