package hu.bme.vn82lz.speye.entities;

public class CallAccount {

	private int _id;
	private String number;
	private String name;
	private boolean enabled;

	public CallAccount(int _id, String number, String name, boolean enabled) {
		super();
		this._id = _id;
		this.number = number;
		this.name = name;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public CallAccount() {
	}

	public CallAccount(String number, String name) {
		super();
		this.number = number;
		this.name = name;
	}

	public CallAccount(String number, String name, boolean enabled) {
		super();
		this.number = number;
		this.name = name;
		this.enabled = enabled;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
