package com.rickbutton.rubydroid;

public class Log {
	public static final String TAG = "RUBYDROID";

	public static void d(String message) {
		android.util.Log.d(TAG, message);
	}

	public static void i(String message) {
		android.util.Log.i(TAG, message);
	}

	public static void e(String message) {
		android.util.Log.e(TAG, message);
	}

	public static void e(String message, Throwable t) {
		android.util.Log.e(TAG, message, t);
	}

}
