package hu.bme.vn82lz.speye.entities;

public class DropboxAccount {
	private int _id;
	private String name;
	private String key;
	private String secret;
	private boolean enabled;

	public DropboxAccount() {
		super();
	}

	public DropboxAccount(String name, String key, String secret, boolean enabled) {
		super();
		this.name = name;
		this.key = key;
		this.secret = secret;
		this.enabled = enabled;
	}

	public DropboxAccount(int _id, String name, String key, String secret, boolean enabled) {
		super();
		this._id = _id;
		this.name = name;
		this.key = key;
		this.secret = secret;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
