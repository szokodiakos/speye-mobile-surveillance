package hu.bme.vn82lz.speye.setupaccounts;

import hu.bme.vn82lz.speye.R;
import hu.bme.vn82lz.speye.db.DatabaseHelper;
import hu.bme.vn82lz.speye.entities.CallAccount;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class CallAccountFormActivity extends Activity {

	private DatabaseHelper dbHelper;
	private boolean freshAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_account_form);
		dbHelper = new DatabaseHelper(this);
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
				R.layout.call_account_form_action_bar, null);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarLayout);

		Button save = (Button) findViewById(R.id.call_account_save);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				EditText name = (EditText) findViewById(R.id.call_account_name);
				EditText number = (EditText) findViewById(R.id.call_account_number);
				Switch enabled = (Switch) findViewById(R.id.call_account_switch);

				String nameText = name.getText().toString();
				String numberText = number.getText().toString();
				boolean checked = enabled.isChecked();

				if (nameText.equals("") || numberText.equals("")) {
					String msg = getResources().getString(R.string.all_fields_required);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				} else {
					if (numberText.startsWith("06")) {
						numberText = "+36" + numberText.substring(2);
					}
					if (freshAccount) {
						new InsertTask().execute(new CallAccount(numberText, nameText, checked));
					} else {
						long id = getIntent().getExtras().getLong("callAccountId");
						new UpdateTask().execute(new CallAccount((int) id, numberText, nameText, checked));
					}
					finish();
				}
			}
		});

		Button delete = (Button) findViewById(R.id.call_account_delete);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				long id = getIntent().getExtras().getLong("callAccountId");
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
		Button button = (Button) findViewById(R.id.call_account_delete);

		// if this account is already created
		if (bundle != null) {
			freshAccount = false;
			long callAccountId = bundle.getLong("callAccountId");
			new SelectTask().execute(callAccountId);
			button.setVisibility(View.VISIBLE);
		}

		else {
			freshAccount = true;
			button.setVisibility(View.GONE);
		}
	}

	private class UpdateTask extends AsyncTask<CallAccount, Void, Void> {

		@Override
		protected Void doInBackground(CallAccount... params) {
			CallAccount account = params[0];
			dbHelper.updateCallAccount(account);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String msg = getResources().getString(R.string.call_account_updated);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

	}

	private class SelectTask extends AsyncTask<Long, Void, CallAccount> {

		@Override
		protected CallAccount doInBackground(Long... params) {
			long id = params[0];
			CallAccount account = dbHelper.getCallAccount((int) id);
			return account;
		}

		@Override
		protected void onPostExecute(CallAccount result) {
			super.onPostExecute(result);
			EditText name = (EditText) findViewById(R.id.call_account_name);
			name.setText(result.getName());

			EditText number = (EditText) findViewById(R.id.call_account_number);
			number.setText(result.getNumber());

			Switch sw = (Switch) findViewById(R.id.call_account_switch);
			sw.setChecked(result.isEnabled());
		}
	}

	private class DeleteTask extends AsyncTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			long id = params[0];
			dbHelper.deleteCallAccount(id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String msg = getResources().getString(R.string.call_account_deleted);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

	}

	private class InsertTask extends AsyncTask<CallAccount, Void, Void> {

		@Override
		protected Void doInBackground(CallAccount... data) {
			CallAccount account = data[0];
			dbHelper.createCallAccount(account);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String msg = getResources().getString(R.string.call_account_saved);
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
