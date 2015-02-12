package hu.bme.vn82lz.speye.setupaccounts;

import hu.bme.vn82lz.speye.R;
import hu.bme.vn82lz.speye.db.DatabaseHelper;
import hu.bme.vn82lz.speye.entities.CallAccount;
import hu.bme.vn82lz.speye.entities.DropboxAccount;
import hu.bme.vn82lz.speye.entities.EmailAccount;
import hu.bme.vn82lz.speye.entities.SmsAccount;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

public class SetupAlertPreferenceActivity extends PreferenceActivity {

	DatabaseHelper dbhelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		dbhelper = new DatabaseHelper(getApplicationContext());
		addPreferencesFromResource(R.xml.mypreferences);
	}

	@Override
	protected void onResume() {
		new RefreshFromDatabaseTask().execute();
		super.onResume();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}

	private void loadCallAccounts() {
		PreferenceCategory callAccountCategory = (PreferenceCategory) findPreference("call_category");

		callAccountCategory.setOrderingAsAdded(true);
		callAccountCategory.removeAll();
		List<CallAccount> callAccounts = dbhelper.getAllCallAccounts();
		for (int i = 0; i < callAccounts.size(); i++) {
			Preference callAccountPreference = new Preference(SetupAlertPreferenceActivity.this);
			callAccountPreference.setIcon(R.drawable.ic_action_call);
			callAccountPreference.setTitle(callAccounts.get(i).getName());
			callAccountPreference.setKey("callAccountId" + callAccounts.get(i).get_id());
			callAccountPreference.setOrder(0);
			callAccountCategory.addPreference(callAccountPreference);
			callAccountPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent i = new Intent(SetupAlertPreferenceActivity.this, CallAccountFormActivity.class);
					i.putExtra("callAccountId", Long.parseLong(preference.getKey().substring("callAccountId".length())));
					startActivity(i);
					return true;
				}
			});
		}
		Preference addCallAccount = new Preference(SetupAlertPreferenceActivity.this);
		addCallAccount.setIcon(R.drawable.ic_action_new);
		addCallAccount.setTitle(R.string.add_account);
		addCallAccount.setKey("add_call_accounts");
		addCallAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SetupAlertPreferenceActivity.this, CallAccountFormActivity.class);
				startActivity(i);
				return true;
			}
		});
		callAccountCategory.addPreference(addCallAccount);
	}

	private void loadSmsAccounts() {
		PreferenceCategory smsAccountCategory = (PreferenceCategory) findPreference("sms_category");
		smsAccountCategory.setOrderingAsAdded(true);
		smsAccountCategory.removeAll();
		List<SmsAccount> smsAccounts = dbhelper.getAllSmsAccounts();
		for (int i = 0; i < smsAccounts.size(); i++) {
			Preference smsAccountPreference = new Preference(SetupAlertPreferenceActivity.this);
			smsAccountPreference.setIcon(R.drawable.ic_action_chat);
			smsAccountPreference.setTitle(smsAccounts.get(i).getName());
			smsAccountPreference.setKey("smsAccountId" + smsAccounts.get(i).get_id());
			smsAccountPreference.setOrder(0);
			smsAccountCategory.addPreference(smsAccountPreference);
			smsAccountPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent i = new Intent(SetupAlertPreferenceActivity.this, SmsAccountFormActivity.class);
					i.putExtra("smsAccountId", Long.parseLong(preference.getKey().substring("smsAccountId".length())));
					startActivity(i);
					return true;
				}
			});
		}
		Preference addSmsAccount = new Preference(SetupAlertPreferenceActivity.this);
		addSmsAccount.setIcon(R.drawable.ic_action_new);
		addSmsAccount.setTitle(R.string.add_account);
		addSmsAccount.setKey("add_sms_accounts");
		addSmsAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SetupAlertPreferenceActivity.this, SmsAccountFormActivity.class);
				startActivity(i);
				return true;
			}
		});
		smsAccountCategory.addPreference(addSmsAccount);
	}

	private void loadEmailAccounts() {
		PreferenceCategory emailAccountCategory = (PreferenceCategory) findPreference("email_category");
		emailAccountCategory.setOrderingAsAdded(true);
		emailAccountCategory.removeAll();
		List<EmailAccount> emailAccounts = dbhelper.getAllEmailAccounts();
		for (int i = 0; i < emailAccounts.size(); i++) {
			Preference emailAccountPreference = new Preference(SetupAlertPreferenceActivity.this);
			emailAccountPreference.setIcon(R.drawable.ic_action_unread);
			emailAccountPreference.setTitle(emailAccounts.get(i).getName());
			emailAccountPreference.setKey("emailAccountId" + emailAccounts.get(i).get_id());
			emailAccountPreference.setOrder(0);
			emailAccountCategory.addPreference(emailAccountPreference);
			emailAccountPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent i = new Intent(SetupAlertPreferenceActivity.this, EmailAccountFormActivity.class);
					i.putExtra("emailAccountId",
							Long.parseLong(preference.getKey().substring("emailAccountId".length())));
					startActivity(i);
					return true;
				}
			});
		}
		Preference addEmailAccount = new Preference(SetupAlertPreferenceActivity.this);
		addEmailAccount.setIcon(R.drawable.ic_action_new);
		addEmailAccount.setTitle(R.string.add_account);
		addEmailAccount.setKey("add_email_accounts");
		addEmailAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SetupAlertPreferenceActivity.this, EmailAccountFormActivity.class);
				startActivity(i);
				return true;
			}
		});
		emailAccountCategory.addPreference(addEmailAccount);
	}

	private void loadDropboxAccounts() {
		PreferenceCategory dropboxAccountCategory = (PreferenceCategory) findPreference("dropbox_category");
		dropboxAccountCategory.setOrderingAsAdded(true);
		dropboxAccountCategory.removeAll();
		List<DropboxAccount> dropboxAccounts = dbhelper.getAllDropboxAccounts();
		for (int i = 0; i < dropboxAccounts.size(); i++) {
			Preference dropboxAccountPreference = new Preference(SetupAlertPreferenceActivity.this);
			dropboxAccountPreference.setIcon(R.drawable.ic_dropbox);
			dropboxAccountPreference.setTitle(dropboxAccounts.get(i).getName());
			dropboxAccountPreference.setKey("dropboxAccountId" + dropboxAccounts.get(i).get_id());
			dropboxAccountPreference.setOrder(0);
			dropboxAccountCategory.addPreference(dropboxAccountPreference);
			dropboxAccountPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent i = new Intent(SetupAlertPreferenceActivity.this, DropboxAccountFormActivity.class);
					i.putExtra("dropboxAccountId",
							Long.parseLong(preference.getKey().substring("dropboxAccountId".length())));
					startActivity(i);
					return true;
				}
			});
		}
		Preference addDropboxAccount = new Preference(SetupAlertPreferenceActivity.this);
		addDropboxAccount.setIcon(R.drawable.ic_action_new);
		addDropboxAccount.setTitle(R.string.add_account);
		addDropboxAccount.setKey("add_dropbox_accounts");
		addDropboxAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SetupAlertPreferenceActivity.this, DropboxAccountFormActivity.class);
				startActivity(i);
				return true;
			}
		});
		dropboxAccountCategory.addPreference(addDropboxAccount);
	}

	private class RefreshFromDatabaseTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			loadCallAccounts();
			loadSmsAccounts();
			loadEmailAccounts();
			loadDropboxAccounts();
			return null;
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
