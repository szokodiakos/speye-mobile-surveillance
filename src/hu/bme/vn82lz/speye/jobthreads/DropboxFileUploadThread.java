package hu.bme.vn82lz.speye.jobthreads;

import hu.bme.vn82lz.speye.alarm.Alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;

public class DropboxFileUploadThread extends Thread {
	private String key;
	private String secret;
	private Alarm alarm;
	private DropboxAPI<AndroidAuthSession> mDBApi;

	public DropboxFileUploadThread(String key, String secret, Alarm alarm, DropboxAPI<AndroidAuthSession> mDBApi) {
		super();
		this.key = key;
		this.secret = secret;
		this.alarm = alarm;
		this.mDBApi = mDBApi;
	}

	public void run() {
		File file = null;
		try {
			Bitmap bmp = alarm.getPictureOfAlarm();
			String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
			file = new File(extStorageDirectory, "speye" + alarm.getDateOfAlarm() + ".png");
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			mDBApi.getSession().setAccessTokenPair(new AccessTokenPair(key, secret));
			mDBApi.putFile("speye" + alarm.getDateOfAlarm() + ".png", inputStream, file.length(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
