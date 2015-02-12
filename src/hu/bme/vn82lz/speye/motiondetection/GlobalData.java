package hu.bme.vn82lz.speye.motiondetection;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GlobalData {

	private GlobalData() {
	};

	private static final AtomicBoolean phoneInMotion = new AtomicBoolean(false);

	public static boolean isPhoneInMotion() {
		return phoneInMotion.get();
	}

	public static void setPhoneInMotion(boolean bool) {
		phoneInMotion.set(bool);
	}
}
