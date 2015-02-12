package hu.bme.vn82lz.speye.alarm;

import android.graphics.Bitmap;

public class Alarm {
	private String dateOfAlarm;
	private Bitmap pictureOfAlarm;

	public String getDateOfAlarm() {
		return dateOfAlarm;
	}

	public void setDateOfAlarm(String dateOfAlarm) {
		this.dateOfAlarm = dateOfAlarm;
	}

	public Bitmap getPictureOfAlarm() {
		return pictureOfAlarm;
	}

	public void setPictureOfAlarm(Bitmap pictureOfAlarm) {
		this.pictureOfAlarm = pictureOfAlarm;
	}

	public Alarm(String dateOfAlarm, Bitmap pictureOfAlarm) {
		super();
		this.dateOfAlarm = dateOfAlarm;
		this.pictureOfAlarm = pictureOfAlarm;
	}

}
