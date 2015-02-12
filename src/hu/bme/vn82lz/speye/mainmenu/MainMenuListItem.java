package hu.bme.vn82lz.speye.mainmenu;

public class MainMenuListItem {

	private int id;
	private int iconFile;
	private int name;

	public MainMenuListItem(int id, int iconFile, int name) {

		this.id = id;
		this.iconFile = iconFile;
		this.name = name;

	}

	public int getId() {
		return id;
	}

	public int getIconFile() {
		return iconFile;
	}

	public int getName() {
		return name;
	}

}
