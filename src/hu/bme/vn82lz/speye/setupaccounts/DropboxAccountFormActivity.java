package hu.bme.vn82lz.speye.setupaccounts;

import hu.bme.vn82lz.speye.R;
import hu.bme.vn82lz.speye.db.DatabaseHelper;
import hu.bme.vn82lz.speye.entities.DropboxAccount;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class DropboxAccountFormActivity extends Activity {

	private static final String APP_KEY = "gc6amplx4vxxqqb";
	private static final String APP_SECRET = "bspr0ebu0qqr0sm";
	private static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<AndroidAuthSession> mDBApi;

	private DatabaseHelper dbHelper;
	private boolean freshAccount;

	private String key;
	private String secret;

	boolean linked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		key = "";
		secret = "";
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		setContentView(R.layout.dropbox_account_form);
		dbHelper = new DatabaseHelper(this);
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
				R.layout.dropbox_account_form_action_bar, null);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarLayout);

		Button link = (Button) findViewById(R.id.dropbox_account_link);
		link.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDBApi.getSession().startAuthentication(DropboxAccountFormActivity.this);

			}
		});

		Button save = (Button) findViewById(R.id.dropbox_account_save);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				EditText name = (EditText) findViewById(R.id.dropbox_account_name);
				Switch enabled = (Switch) findViewById(R.id.dropbox_account_switch);

				String nameText = name.getText().toString();
				boolean checked = enabled.isChecked();
				boolean linked = (!key.equals("") && !secret.equals(""));

				if (nameText.equals("")) {
					String msg = getResources().getString(R.string.all_fields_required);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				} else if (!linked) {
					String msg = getResources().getString(R.string.linking_required);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				} else {
					if (freshAccount) {
						freshAccount = false;
						new InsertTask().execute(new DropboxAccount(nameText, key, secret, checked));
					} else {
						long id = getIntent().getExtras().getLong("dropboxAccountId");
						new UpdateTask().execute(new DropboxAccount((int) id, nameText, key, secret, checked));
					}
					finish();
				}
			}
		});

		Button delete = (Button) findViewById(R.id.dropbox_account_delete);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				long id = getIntent().getExtras().getLong("dropboxAccountId");
				new DeleteTask().execute(id);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		Button button = (Button) findViewById(R.id.dropbox_account_delete);

		// if this account is already created
		if (bundle != null) {
			freshAccount = false;
			long dropboxAccountId = bundle.getLong("dropboxAccountId");
			new SelectTask().execute(dropboxAccountId);
			button.setVisibility(View.VISIBLE);
		} else {
			freshAccount = true;
			button.setVisibility(View.GONE);
		}
		Button link = (Button) findViewById(R.id.dropbox_account_link);
		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// Required to complete auth, sets the access token on the
				// session
				mDBApi.getSession().finishAuthentication();

				AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
				key = tokens.key;
				secret = tokens.secret;

				link.setEnabled(false);
				String alreadyLinked = getResources().getString(R.string.already_linked);
				link.setText(alreadyLinked);
				mDBApi.getSession().unlink();
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		} else {
			if (freshAccount) {
				String linkText = getResources().getString(R.string.link_dropbox_account);
				link.setText(linkText);
			} else {
				String linkText = getResources().getString(R.string.relink_dropbox_account);
				link.setText(linkText);
			}
			link.setEnabled(true);

		}
	}

	private class UpdateTask extends AsyncTask<DropboxAccount, Void, Void> {

		@Override
		protected Void doInBackground(DropboxAccount... params) {
			DropboxAccount account = params[0];
			dbHelper.updateDropboxAccount(account);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String msg = getResources().getString(R.string.dropbox_account_updated);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}
	}

	private class SelectTask extends AsyncTask<Long, Void, DropboxAccount> {

		@Override
		protected DropboxAccount doInBackground(Long... params) {
			long id = params[0];
			DropboxAccount account = dbHelper.getDropboxAccount((int) id);
			return account;
		}

		@Override
		protected void onPostExecute(DropboxAccount result) {
			super.onPostExecute(result);
			EditText name = (EditText) findViewById(R.id.dropbox_account_name);
			name.setText(result.getName());

			Switch sw = (Switch) findViewById(R.id.dropbox_account_switch);
			sw.setChecked(result.isEnabled());
		}

	}

	private class DeleteTask extends AsyncTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			long id = params[0];
			dbHelper.deleteDropboxAccount(id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String msg = getResources().getString(R.string.dropbox_account_deleted);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
			mDBApi.getSession().unlink();
		}

	}

	private class InsertTask extends AsyncTask<DropboxAccount, Void, Void> {

		@Override
		protected Void doInBackground(DropboxAccount... data) {
			DropboxAccount account = data[0];
			dbHelper.createDropboxAccount(account);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String msg = getResources().getString(R.string.dropbox_account_saved);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
