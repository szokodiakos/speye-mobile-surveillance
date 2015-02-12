package hu.bme.vn82lz.speye.db;

import hu.bme.vn82lz.speye.entities.CallAccount;
import hu.bme.vn82lz.speye.entities.DropboxAccount;
import hu.bme.vn82lz.speye.entities.EmailAccount;
import hu.bme.vn82lz.speye.entities.SmsAccount;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static String DATABASE_HELPER_TAG = "DATABASEHELPER";
	private static int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "accounts";

	private static String TABLE_CALL_ACCOUNTS = "callaccounts";
	private static String TABLE_SMS_ACCOUNTS = "smsaccounts";
	private static String TABLE_EMAIL_ACCOUNTS = "emailaccounts";
	private static String TABLE_DROPBOX_ACCOUNTS = "dropboxaccounts";

	private static String KEY_ID = "_id";
	private static String KEY_NUMBER = "number";
	private static String KEY_NAME = "name";
	private static String KEY_ENABLED = "enabled";

	private static String KEY_ADDRESS = "address";

	private static String KEY_KEY = "key";
	private static String KEY_SECRET = "secret";

	private static String CREATE_CALL_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_CALL_ACCOUNTS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY," + KEY_NUMBER + " TEXT," + KEY_NAME + " TEXT," + KEY_ENABLED + " INTEGER)";

	private static String CREATE_SMS_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_SMS_ACCOUNTS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY," + KEY_NUMBER + " TEXT," + KEY_NAME + " TEXT," + KEY_ENABLED + " INTEGER)";

	private static String CREATE_EMAIL_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_EMAIL_ACCOUNTS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY," + KEY_ADDRESS + " TEXT," + KEY_NAME + " TEXT," + KEY_ENABLED + " INTEGER)";

	private static String CREATE_DROPBOX_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_DROPBOX_ACCOUNTS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY," + KEY_KEY + " TEXT," + KEY_SECRET + " TEXT," + KEY_NAME + " TEXT," + KEY_ENABLED
			+ " INTEGER)";

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CALL_ACCOUNTS_TABLE);
		db.execSQL(CREATE_SMS_ACCOUNTS_TABLE);
		db.execSQL(CREATE_EMAIL_ACCOUNTS_TABLE);
		db.execSQL(CREATE_DROPBOX_ACCOUNTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_ACCOUNTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_ACCOUNTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL_ACCOUNTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DROPBOX_ACCOUNTS);
		onCreate(db);
	}

	// CALL
	// C
	public long createCallAccount(CallAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, account.getName());
		cv.put(KEY_NUMBER, account.getNumber());
		if (account.isEnabled()) {
			cv.put(KEY_ENABLED, 1);
		} else {
			cv.put(KEY_ENABLED, 0);
		}
		long accountId = db.insert(TABLE_CALL_ACCOUNTS, null, cv);
		return accountId;
	}

	// R
	public CallAccount getCallAccount(int _id) {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CALL_ACCOUNTS + " WHERE " + KEY_ID + " = " + _id;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null) {
			c.moveToFirst();
		}
		CallAccount account = new CallAccount();
		account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
		account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		account.setNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));
		if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
			account.setEnabled(false);
		} else {
			account.setEnabled(true);
		}
		return account;
	}

	// R ALL
	public List<CallAccount> getAllCallAccounts() {
		List<CallAccount> accounts = new ArrayList<CallAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CALL_ACCOUNTS;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				CallAccount account = new CallAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// R enabled
	public List<CallAccount> getEnabledCallAccounts() {
		List<CallAccount> accounts = new ArrayList<CallAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CALL_ACCOUNTS + " WHERE enabled == 1";
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				CallAccount account = new CallAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// U
	public int updateCallAccount(CallAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, account.getName());
		values.put(KEY_NUMBER, account.getNumber());
		values.put(KEY_ENABLED, account.isEnabled());
		return db.update(TABLE_CALL_ACCOUNTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(account.get_id()) });
	}

	// D
	public void deleteCallAccount(long _id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_CALL_ACCOUNTS, KEY_ID + " = ?", new String[] { String.valueOf(_id) });
	}

	// SMS
	// C
	public long createSmsAccount(SmsAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, account.getName());
		cv.put(KEY_NUMBER, account.getNumber());
		if (account.isEnabled()) {
			cv.put(KEY_ENABLED, 1);
		} else {
			cv.put(KEY_ENABLED, 0);
		}
		long accountId = db.insert(TABLE_SMS_ACCOUNTS, null, cv);
		return accountId;
	}

	// R
	public SmsAccount getSmsAccount(int _id) {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_SMS_ACCOUNTS + " WHERE " + KEY_ID + " = " + _id;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null) {
			c.moveToFirst();
		}
		SmsAccount account = new SmsAccount();
		account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
		account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		account.setNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));
		if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
			account.setEnabled(false);
		} else {
			account.setEnabled(true);
		}
		return account;
	}

	// R ALL
	public List<SmsAccount> getAllSmsAccounts() {
		List<SmsAccount> accounts = new ArrayList<SmsAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_SMS_ACCOUNTS;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				SmsAccount account = new SmsAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// R enabled
	public List<SmsAccount> getEnabledSmsAccounts() {
		List<SmsAccount> accounts = new ArrayList<SmsAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_SMS_ACCOUNTS + " WHERE enabled == 1";
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				SmsAccount account = new SmsAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// U
	public int updateSmsAccount(SmsAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, account.getName());
		values.put(KEY_NUMBER, account.getNumber());
		values.put(KEY_ENABLED, account.isEnabled());
		return db
				.update(TABLE_SMS_ACCOUNTS, values, KEY_ID + " = ?", new String[] { String.valueOf(account.get_id()) });
	}

	// D
	public void deleteSmsAccount(long _id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_SMS_ACCOUNTS, KEY_ID + " = ?", new String[] { String.valueOf(_id) });
	}

	// EMAIL
	// C
	public long createEmailAccount(EmailAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, account.getName());
		cv.put(KEY_ADDRESS, account.getAddress());
		if (account.isEnabled()) {
			cv.put(KEY_ENABLED, 1);
		} else {
			cv.put(KEY_ENABLED, 0);
		}
		long accountId = db.insert(TABLE_EMAIL_ACCOUNTS, null, cv);
		return accountId;
	}

	// R
	public EmailAccount getEmailAccount(int _id) {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_EMAIL_ACCOUNTS + " WHERE " + KEY_ID + " = " + _id;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null) {
			c.moveToFirst();
		}
		EmailAccount account = new EmailAccount();
		account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
		account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		account.setAddress(c.getString(c.getColumnIndex(KEY_ADDRESS)));
		if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
			account.setEnabled(false);
		} else {
			account.setEnabled(true);
		}
		return account;
	}

	// R ALL
	public List<EmailAccount> getAllEmailAccounts() {
		List<EmailAccount> accounts = new ArrayList<EmailAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_EMAIL_ACCOUNTS;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				EmailAccount account = new EmailAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setAddress(c.getString(c.getColumnIndex(KEY_ADDRESS)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// R enabled
	public List<EmailAccount> getEnabledEmailAccounts() {
		List<EmailAccount> accounts = new ArrayList<EmailAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_EMAIL_ACCOUNTS + " WHERE enabled == 1";
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				EmailAccount account = new EmailAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setAddress(c.getString(c.getColumnIndex(KEY_ADDRESS)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// U
	public int updateEmailAccount(EmailAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, account.getName());
		values.put(KEY_ADDRESS, account.getAddress());
		values.put(KEY_ENABLED, account.isEnabled());
		return db.update(TABLE_EMAIL_ACCOUNTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(account.get_id()) });
	}

	// D
	public void deleteEmailAccount(long _id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_EMAIL_ACCOUNTS, KEY_ID + " = ?", new String[] { String.valueOf(_id) });
	}

	// DROPBOX
	// C
	public long createDropboxAccount(DropboxAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, account.getName());
		cv.put(KEY_KEY, account.getKey());
		cv.put(KEY_SECRET, account.getSecret());
		if (account.isEnabled()) {
			cv.put(KEY_ENABLED, 1);
		} else {
			cv.put(KEY_ENABLED, 0);
		}
		long accountId = db.insert(TABLE_DROPBOX_ACCOUNTS, null, cv);
		return accountId;
	}

	// R
	public DropboxAccount getDropboxAccount(int _id) {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_DROPBOX_ACCOUNTS + " WHERE " + KEY_ID + " = " + _id;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null) {
			c.moveToFirst();
		}
		DropboxAccount account = new DropboxAccount();
		account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
		account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		account.setKey(c.getString(c.getColumnIndex(KEY_KEY)));
		account.setSecret(c.getString(c.getColumnIndex(KEY_SECRET)));
		if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
			account.setEnabled(false);
		} else {
			account.setEnabled(true);
		}
		return account;
	}

	// R ALL
	public List<DropboxAccount> getAllDropboxAccounts() {
		List<DropboxAccount> accounts = new ArrayList<DropboxAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_DROPBOX_ACCOUNTS;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				DropboxAccount account = new DropboxAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setKey(c.getString(c.getColumnIndex(KEY_KEY)));
				account.setSecret(c.getString(c.getColumnIndex(KEY_SECRET)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// R enabled
	public List<DropboxAccount> getEnabledDropboxAccounts() {
		List<DropboxAccount> accounts = new ArrayList<DropboxAccount>();
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_DROPBOX_ACCOUNTS + " WHERE enabled == 1";
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				DropboxAccount account = new DropboxAccount();
				account.set_id(c.getInt(c.getColumnIndex(KEY_ID)));
				account.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				account.setKey(c.getString(c.getColumnIndex(KEY_KEY)));
				account.setSecret(c.getString(c.getColumnIndex(KEY_SECRET)));
				if (c.getInt(c.getColumnIndex(KEY_ENABLED)) == 0) {
					account.setEnabled(false);
				} else {
					account.setEnabled(true);
				}
				accounts.add(account);
			} while (c.moveToNext());
		}
		return accounts;
	}

	// U
	public int updateDropboxAccount(DropboxAccount account) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, account.getName());
		values.put(KEY_KEY, account.getKey());
		values.put(KEY_SECRET, account.getSecret());
		values.put(KEY_ENABLED, account.isEnabled());
		return db.update(TABLE_DROPBOX_ACCOUNTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(account.get_id()) });
	}

	// D
	public void deleteDropboxAccount(long _id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_DROPBOX_ACCOUNTS, KEY_ID + " = ?", new String[] { String.valueOf(_id) });
	}

}
