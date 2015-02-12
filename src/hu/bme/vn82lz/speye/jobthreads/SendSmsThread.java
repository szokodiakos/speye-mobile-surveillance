package hu.bme.vn82lz.speye.jobthreads;

import android.telephony.SmsManager;

public class SendSmsThread extends Thread {
	private String number;
	private String smsAlarmMessage;

	public SendSmsThread(String number, String smsAlarmMessage) {
		super();
		this.number = number;
		this.smsAlarmMessage = smsAlarmMessage;
	}

	public void run() {
		SmsManager sm = SmsManager.getDefault();
		String alarmMessage = smsAlarmMessage;
		sm.sendTextMessage(number, null, alarmMessage, null, null);
	}

}
