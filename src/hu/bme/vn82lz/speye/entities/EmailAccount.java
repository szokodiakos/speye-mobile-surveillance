package hu.bme.vn82lz.speye.entities;

public class EmailAccount {

	private int _id;
	private String address;
	private String name;
	private boolean enabled;

	public EmailAccount(int _id, String address, String name, boolean enabled) {
		super();
		this._id = _id;
		this.address = address;
		this.name = name;
		this.enabled = enabled;
	}

	public EmailAccount(String address, String name, boolean enabled) {
		super();
		this.address = address;
		this.name = name;
		this.enabled = enabled;
	}

	public EmailAccount() {
		super();
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
