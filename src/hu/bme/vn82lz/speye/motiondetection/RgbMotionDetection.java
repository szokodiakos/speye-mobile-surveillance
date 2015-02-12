package hu.bme.vn82lz.speye.motiondetection;

import android.graphics.Color;

public class RgbMotionDetection {

	// Specific settings
	private static final int mPixelThreshold = 50; // Difference in pixel (RGB)
	private static final int mThreshold = 100; // Number of different pixels
												// (RGB)
	private static int[] mPrevious = null;
	private static int mPreviousWidth = 0;
	private static int mPreviousHeight = 0;
	private float difference;

	private int upperLeftX = 99999999;
	private int upperLeftY = 99999999;
	private int lowerRightX = 0;
	private int lowerRightY = 0;

	public int[] getPrevious() {
		return ((mPrevious != null) ? mPrevious.clone() : null);
	}

	protected boolean isDifferent(int[] first, int width, int height) {
		if (first == null)
			throw new NullPointerException();
		if (mPrevious == null)
			return false;
		if (first.length != mPrevious.length)
			return true;
		if (mPreviousWidth != width || mPreviousHeight != height)
			return true;

		int totDifferentPixels = 0;

		upperLeftX = 99999999;
		upperLeftY = 99999999;
		lowerRightX = 0;
		lowerRightY = 0;

		for (int i = 0, ij = 0; i < height; i++) {
			for (int j = 0; j < width; j++, ij++) {
				int pix = (0xff & ((int) first[ij]));
				int otherPix = (0xff & ((int) mPrevious[ij]));

				if (pix < 0)
					pix = 0;
				if (pix > 255)
					pix = 255;
				if (otherPix < 0)
					otherPix = 0;
				if (otherPix > 255)
					otherPix = 255;

				if (Math.abs(pix - otherPix) >= mPixelThreshold) {
					totDifferentPixels++;
					if (upperLeftX > j) {
						upperLeftX = j;
					} else if (lowerRightX < j) {
						lowerRightX = j;
					}
					if (upperLeftY > i) {
						upperLeftY = i;
					} else if (lowerRightY < i) {
						lowerRightY = i;
					}
					// first[ij] = Color.RED;
				}
			}
		}

		for (int i = upperLeftY * width + upperLeftX; i < upperLeftY * width + lowerRightX; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i - j * width) > 0 && (i - j * width) < first.length) {
					first[i - j * width] = Color.RED;
				}
			}
		}
		for (int i = lowerRightY * width + upperLeftX; i < lowerRightY * width + lowerRightX; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i + j * width) > 0 && (i + j * width) < first.length) {
					first[i + j * width] = Color.RED;
				}
			}
		}
		for (int i = upperLeftY * width + upperLeftX; i < lowerRightY * width + upperLeftX; i = i + width) {
			for (int j = 0; j < 2; j++) {
				if ((i + j * width) > 0 && (i + j * width) < first.length) {
					first[i + j] = Color.RED;
				}
			}
		}
		for (int i = upperLeftY * width + lowerRightX; i < lowerRightY * width + lowerRightX; i = i + width) {
			for (int j = 0; j < 2; j++) {
				if ((i - j * width) > 0 && (i - j * width) < first.length) {
					first[i - j] = Color.RED;
				}
			}
		}

		if (totDifferentPixels <= 0)
			totDifferentPixels = 1;
		boolean different = totDifferentPixels > mThreshold;
		difference = ((float) totDifferentPixels) / ((float) width * (float) height);
		return different;
	}

	public boolean detect(int[] rgb, int width, int height) {
		if (rgb == null)
			throw new NullPointerException();
		int[] original = rgb.clone();
		if (mPrevious == null) {
			mPrevious = original;
			mPreviousWidth = width;
			mPreviousHeight = height;
			return false;
		}
		boolean motionDetected = isDifferent(rgb, width, height);
		mPrevious = original;
		mPreviousWidth = width;
		mPreviousHeight = height;

		return motionDetected;
	}

	public float getDifference() {
		return difference;
	}
}
