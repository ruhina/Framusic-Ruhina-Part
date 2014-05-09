package com.team8.framusicv2;

import com.team8.framusicv2.musicplay.MusicPlayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.SyncStateContract.Constants;
import android.widget.Toast;

public class MusicAlarmControl extends Service {
	private Context mContext = null;
	private String mMyType = null;
	private Intent intent = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mContext = this;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		this.intent = intent;
		return mBinder;
	}

	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		this.intent = intent;
		mMyType = intent.getStringExtra("TYPE");
		
		Toast.makeText(mContext, mMyType, Toast.LENGTH_LONG).show();
		//get current music player and stop or start music
		
		Intent i=new Intent();
		i.putExtra("MusicAlarmControl", mMyType);
		i.setAction("android.intent.action.musicalarm");//action与接收器相同
		sendBroadcast(i);
		
		// Done with our work... stop the service!
		MusicAlarmControl.this.stopSelf();
	}
}
