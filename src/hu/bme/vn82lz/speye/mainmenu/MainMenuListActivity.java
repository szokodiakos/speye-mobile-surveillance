package hu.bme.vn82lz.speye.mainmenu;

import hu.bme.vn82lz.speye.R;
import hu.bme.vn82lz.speye.motiondetection.MotionDetectionActivity;
import hu.bme.vn82lz.speye.options.OptionsPreferenceActivity;
import hu.bme.vn82lz.speye.setupaccounts.SetupAlertPreferenceActivity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenuListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<MainMenuListItem> listItems = new ArrayList<MainMenuListItem>();
		listItems.add(new MainMenuListItem(0, R.drawable.ic_action_video, R.string.start_surveillance));
		listItems.add(new MainMenuListItem(1, R.drawable.ic_action_volume_on, R.string.setup_alert_accounts));
		// listItems.add(new MainMenuListItem(2, R.drawable.ic_action_settings,
		// R.string.settings));
		MainMenuListAdapter la = new MainMenuListAdapter(listItems);
		setListAdapter(la);

		// Elem kattintás eseményre feliratkozás - description megjelenítése
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				MainMenuListItem listItem = (MainMenuListItem) getListAdapter().getItem(position);
				Intent i;
				switch (listItem.getId()) {
				case (0):
					i = new Intent(MainMenuListActivity.this, MotionDetectionActivity.class);
					startActivity(i);
					break;
				case (1):
					i = new Intent(MainMenuListActivity.this, SetupAlertPreferenceActivity.class);
					startActivity(i);
					break;
				case (2):
					// i = new Intent(MainMenuListActivity.this,
					// OptionsPreferenceActivity.class);
					// startActivity(i);
					break;
				}
			}
		});
	}

	@Override
	protected void onResume() {

		super.onResume();
	}
}
