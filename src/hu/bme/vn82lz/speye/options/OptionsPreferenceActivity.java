package hu.bme.vn82lz.speye.options;

import hu.bme.vn82lz.speye.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OptionsPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.options_preferences);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
