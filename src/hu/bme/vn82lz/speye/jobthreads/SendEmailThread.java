package hu.bme.vn82lz.speye.jobthreads;

import hu.bme.vn82lz.speye.alarm.Alarm;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Base64;

public class SendEmailThread extends Thread {
	private String emailAddress;
	private String user;
	private Alarm alarm;
	private String emailAlarmMessage;
	private String emailAlarmHeader;

	public SendEmailThread(String emailAddress, String user, Alarm alarm, String emailAlarmMessage,
			String emailAlarmHeader) {
		super();
		this.emailAddress = emailAddress;
		this.user = user;
		this.alarm = alarm;
		this.emailAlarmMessage = emailAlarmMessage;
		this.emailAlarmHeader = emailAlarmHeader;
	}

	public void run() {
		Bitmap bm = null;
		ByteArrayOutputStream baos = null;
		byte[] b = null;
		String encodedImage = null;
		if (alarm != null) {
			bm = alarm.getPictureOfAlarm();
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			b = baos.toByteArray();
			encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		}
		String alarmMessage = emailAlarmMessage;
		String alarmHeader = emailAlarmHeader;
		JSONObject rootObj = new JSONObject();
		try {
			rootObj.put("key", "aNlYxQxSlDMoQptEGHnFtQ");
			JSONObject messageObj = new JSONObject();
			messageObj.put("html", "<h1>" + alarmHeader + "</h1>" + alarmMessage);
			messageObj.put("subject", alarmHeader);
			messageObj.put("from_email", "speye.mobile.surveillance@gmail.com");
			messageObj.put("from_name", "Speye");
			JSONArray toArray = new JSONArray();
			JSONObject toObj = new JSONObject();
			toObj.put("email", emailAddress);
			toObj.put("name", user);
			toArray.put(toObj);
			if (alarm != null) {
				JSONArray attachments = new JSONArray();
				JSONObject attachment = new JSONObject();
				attachment.put("type", "image/png");
				attachment.put("name", "speye" + alarm.getDateOfAlarm() + ".png");
				attachment.put("content", encodedImage);
				attachments.put(attachment);
				messageObj.put("attachments", attachments);
			}
			messageObj.put("to", toArray);
			rootObj.put("message", messageObj);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("https://mandrillapp.com/api/1.0/messages/send.json");
		try {
			StringEntity se = new StringEntity(rootObj.toString());
			httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
			httpPost.setEntity(se);
			HttpResponse resp = httpClient.execute(httpPost);
			resp.getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}
