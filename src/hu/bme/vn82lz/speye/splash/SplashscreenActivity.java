package hu.bme.vn82lz.speye.splash;

import hu.bme.vn82lz.speye.R;
import hu.bme.vn82lz.speye.mainmenu.MainMenuListActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashscreenActivity extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		// this.deleteDatabase("accounts");

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent i = new Intent(SplashscreenActivity.this, MainMenuListActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}
