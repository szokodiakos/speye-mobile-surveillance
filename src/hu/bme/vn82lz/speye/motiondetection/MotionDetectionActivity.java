package hu.bme.vn82lz.speye.motiondetection;

import hu.bme.vn82lz.speye.R;
import hu.bme.vn82lz.speye.alarm.Alarm;
import hu.bme.vn82lz.speye.customui.VerticalSeekBar;
import hu.bme.vn82lz.speye.db.DatabaseHelper;
import hu.bme.vn82lz.speye.entities.CallAccount;
import hu.bme.vn82lz.speye.entities.DropboxAccount;
import hu.bme.vn82lz.speye.entities.EmailAccount;
import hu.bme.vn82lz.speye.entities.SmsAccount;
import hu.bme.vn82lz.speye.jobthreads.DropboxFileUploadThread;
import hu.bme.vn82lz.speye.jobthreads.SendEmailThread;
import hu.bme.vn82lz.speye.jobthreads.SendSmsThread;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class MotionDetectionActivity extends SensorsActivity implements OnSeekBarChangeListener {

	private static final String APP_KEY = "gc6amplx4vxxqqb";
	private static final String APP_SECRET = "bspr0ebu0qqr0sm";
	private static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<AndroidAuthSession> mDBApi;

	private static SurfaceView preview = null;
	private static SurfaceHolder previewHolder = null;
	private static Camera camera = null;
	private static boolean inPreview = false;
	private static RgbMotionDetection detector = null;

	private int motionLevel;
	private int thresholdLevel;

	private static boolean atWork = false;
	private VerticalSeekBar seekbar;

	private boolean armed;
	private int motionColor;
	private boolean waitingForArm;

	private DatabaseHelper dbHelper;
	private TelephonyManager telephonyManager;
	private PhoneStateListener listener;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		dbHelper = new DatabaseHelper(getApplicationContext());
		showThreshold = false;
		thresholdLevel = 50;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		setArmed(false);
		waitingForArm = false;
		seekbar = (VerticalSeekBar) findViewById(R.id.sensivity_seekbar);
		seekbar.setMax(100);
		seekbar.setOnSeekBarChangeListener(this);
		seekbar.setProgress(thresholdLevel);
		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		detector = new RgbMotionDetection();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, final String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					final boolean newArmedStatus;
					if (armed) {
						newArmedStatus = false;
						new SetArmedRemotelyTask().execute(false, incomingNumber);
					} else {
						newArmedStatus = true;
						new SetArmedRemotelyTask().execute(true, incomingNumber);
					}
					Thread notifyEnabledAccounts = new Thread() {
						public void run() {
							List<EmailAccount> enabledEmailAccounts = dbHelper.getEnabledEmailAccounts();
							for (int i = 0; i < enabledEmailAccounts.size(); i++) {
								String emailAddress = enabledEmailAccounts.get(i).getAddress();
								String user = enabledEmailAccounts.get(i).getName();
								String emailAlarmMessage = "";
								if (newArmedStatus) {
									emailAlarmMessage = getResources().getString(
											R.string.armed_status_remote_notify_message, incomingNumber);
								} else {
									emailAlarmMessage = getResources().getString(
											R.string.disarmed_status_remote_notify_message, incomingNumber);
								}
								String emailAlarmHeader = getResources().getString(R.string.remote_notify_header);
								new SendEmailThread(emailAddress, user, null, emailAlarmMessage, emailAlarmHeader)
										.start();
							}
						}
					};
					notifyEnabledAccounts.start();
					break;
				}
			}
		};
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		final Button armButton = (Button) findViewById(R.id.arm_button);
		armButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (armed) {
					setArmed(false);
				} else {
					if (waitingForArm) {
						String toastLabel = getResources().getString(R.string.arm_cancelled);
						Toast.makeText(getApplicationContext(), toastLabel, Toast.LENGTH_SHORT).show();
						waitingForArm = false;
						setArmed(false);
					} else {
						String toastLabel = getResources().getString(R.string.wait_for_arm_label);
						Toast.makeText(getApplicationContext(), toastLabel, Toast.LENGTH_SHORT).show();
						Button button = (Button) findViewById(R.id.arm_button);
						String label = getResources().getString(R.string.cancel_label);
						button.setText(label);
						final Handler handler = new Handler();
						waitingForArm = true;
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (waitingForArm) {
									setArmed(true);
								}
							}
						}, 5000);
					}
				}
			}
		});
	}

	private class SetArmedRemotelyTask extends AsyncTask<Object, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			boolean setArmed = (Boolean) params[0];
			String incomingNumber = (String) params[1];
			List<CallAccount> enabledCallAccounts = dbHelper.getEnabledCallAccounts();
			for (int i = 0; i < enabledCallAccounts.size(); i++) {
				String number = enabledCallAccounts.get(i).getNumber();
				if (PhoneNumberUtils.compare(incomingNumber, number)) {
					return Boolean.valueOf(setArmed);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result != null) {
				setArmed(result);
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		super.onPause();
		camera.setPreviewCallback(null);
		if (inPreview)
			camera.stopPreview();
		inPreview = false;
		camera.release();
		camera = null;
		atWork = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private PreviewCallback previewCallback = new PreviewCallback() {

		@SuppressWarnings("unchecked")
		@Override
		public void onPreviewFrame(byte[] data, Camera cam) {
			if (data == null)
				return;
			Camera.Size size = cam.getParameters().getPreviewSize();
			if (size == null)
				return;

			if (!GlobalData.isPhoneInMotion()) {
				if (!atWork) {
					atWork = true;
					new DetectorTask().execute(new Triplet<byte[], Integer, Integer>(data, size.width, size.height));
				} else {
				}
			} else {
				Toast.makeText(getApplicationContext(), "Please hold still!", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
				camera.setPreviewCallback(previewCallback);
			} catch (Throwable t) {
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);
			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				Log.d("PREVSIZE", "Using width=" + size.width + " height=" + size.height);
			}
			parameters.setPreviewFormat(ImageFormat.NV21);
			camera.setParameters(parameters);
			camera.startPreview();
			inPreview = true;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};

	private boolean showThreshold;

	private static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea)
						result = size;
				}
			}
		}
		return result;
	}

	private class DetectorTask extends AsyncTask<Triplet<byte[], Integer, Integer>, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Triplet<byte[], Integer, Integer>... arg0) {
			byte[] data = arg0[0].getA();
			int width = arg0[0].getB();
			int height = arg0[0].getC();

			try {
				int[] img = null;
				img = ImageProcessing.decodeYUV420SPtoRGB(data, width, height);
				int[] org = null;
				if (img != null) {
					org = img.clone();
					if (detector.detect(img, width, height)) {
						Bitmap bitmap = null;
						bitmap = ImageProcessing.rgbToBitmap(img, width, height);
						motionLevel = Math.round(detector.getDifference() * 100f);
						return bitmap;
					} else {
						Bitmap original = null;
						if (org != null) {
							original = ImageProcessing.rgbToBitmap(org, width, height);
						}
						motionLevel = 0;
						return original;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				atWork = false;
			}

			return null;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				if (motionLevel >= thresholdLevel && armed) {
					setArmed(false);
					Date dateOfAlarm = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.SS");
					String formattedDateOfAlarm = sdf.format(dateOfAlarm);
					Alarm alarm = new Alarm(formattedDateOfAlarm, result);
					new AlarmEnabledAccountsTask().execute(alarm);
					Toast.makeText(getApplicationContext(), "Motion detected! Alarm switched off.", Toast.LENGTH_SHORT)
							.show();
				}
				ImageView iv = (ImageView) MotionDetectionActivity.this.findViewById(R.id.iview);
				iv.setImageBitmap(result);
				TextView tv = (TextView) MotionDetectionActivity.this.findViewById(R.id.percent_textview);
				int id = 0;
				int value = 0;
				if (showThreshold) {
					id = R.string.current_threshold;
					value = seekbar.getProgress();
				} else {
					id = R.string.current_motion;
					value = motionLevel;
				}
				String label = getResources().getString(id);
				tv.setText(label + value + "%");
				seekbar.setSecondaryProgress(motionLevel);
			}
			super.onPostExecute(result);
		}
	}

	private class AlarmEnabledAccountsTask extends AsyncTask<Alarm, Void, Void> {

		@Override
		protected Void doInBackground(Alarm... params) {
			Alarm alarm = params[0];
			dbHelper.getWritableDatabase();
			final List<CallAccount> enabledCallAccounts = dbHelper.getEnabledCallAccounts();
			List<SmsAccount> enabledSmsAccounts = dbHelper.getEnabledSmsAccounts();
			List<EmailAccount> enabledEmailAccounts = dbHelper.getEnabledEmailAccounts();
			List<DropboxAccount> enabledDropboxAccounts = dbHelper.getEnabledDropboxAccounts();
			for (int i = 0; i < enabledSmsAccounts.size(); i++) {
				String smsNumber = enabledSmsAccounts.get(i).getNumber();
				String alarmMessage = getResources().getString(R.string.sms_alarm_message, alarm.getDateOfAlarm());
				new SendSmsThread(smsNumber, alarmMessage).start();
			}
			for (int i = 0; i < enabledEmailAccounts.size(); i++) {
				String emailAddress = enabledEmailAccounts.get(i).getAddress();
				String user = enabledEmailAccounts.get(i).getName();
				String alarmMessage = getResources().getString(R.string.email_alarm_message, alarm.getDateOfAlarm());
				String alarmHeader = getResources().getString(R.string.email_alarm_header);
				new SendEmailThread(emailAddress, user, alarm, alarmMessage, alarmHeader).start();
			}
			for (int i = 0; i < enabledDropboxAccounts.size(); i++) {
				String key = enabledDropboxAccounts.get(i).getKey();
				String secret = enabledDropboxAccounts.get(i).getSecret();
				new DropboxFileUploadThread(key, secret, alarm, mDBApi).start();
			}
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < enabledCallAccounts.size(); i++) {
						Log.i("CallingTry", "Now");
						String callNumber = enabledCallAccounts.get(i).getNumber();
						call(callNumber);
					}
				}
			}.start();
			return null;
		}
	}

	public void call(final String callNumber) {
		Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
		startActivity(i);
		try {
			Log.i("sleep", "init");
			Thread.sleep(30000);
			TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
			Class<?> classObject;
			classObject = Class.forName(telephonyManager.getClass().getName());
			Method methodObject = classObject.getDeclaredMethod("getITelephony");
			methodObject.setAccessible(true);
			ITelephony telephonyService = (ITelephony) methodObject.invoke(telephonyManager);
			telephonyService.endCall();
			Log.i("sleep", "callended");
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
		thresholdLevel = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		TextView tv = (TextView) MotionDetectionActivity.this.findViewById(R.id.percent_textview);
		String label = getResources().getString(R.string.current_threshold);
		int value = seekbar.getProgress();
		tv.setText(label + value + "%");
		tv.setTextColor(Color.WHITE);
		showThreshold = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		TextView tv = (TextView) MotionDetectionActivity.this.findViewById(R.id.percent_textview);
		String label = getResources().getString(R.string.current_motion);
		int value = motionLevel;
		tv.setText(label + value + "%");
		tv.setTextColor(motionColor);
		showThreshold = false;
	}

	private void setArmed(boolean value) {
		if (value) {
			armed = true;
			TextView tv = (TextView) MotionDetectionActivity.this.findViewById(R.id.percent_textview);
			motionColor = Color.RED;
			tv.setTextColor(motionColor);
			Button armButton = (Button) findViewById(R.id.arm_button);
			armButton.setText(getResources().getString(R.string.disarm_label));
			String deviceArmed = getResources().getString(R.string.device_armed);
			Toast.makeText(getApplicationContext(), deviceArmed, Toast.LENGTH_SHORT).show();
		} else {
			armed = false;
			TextView tv = (TextView) MotionDetectionActivity.this.findViewById(R.id.percent_textview);
			motionColor = Color.GRAY;
			tv.setTextColor(motionColor);
			Button armButton = (Button) findViewById(R.id.arm_button);
			armButton.setText(getResources().getString(R.string.arm_label));
			String deviceDisarmed = getResources().getString(R.string.device_disarmed);
			Toast.makeText(getApplicationContext(), deviceDisarmed, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Bundle remoteBundle = intent.getExtras();
		boolean setArmed = false;
		if (!armed) {
			setArmed = true;
		}
		if (remoteBundle != null) {
			String remoteArm = remoteBundle.getString("remote");
			if (remoteArm != null) {
				setArmed(setArmed);
			}
		}
		super.onNewIntent(intent);
	}
}