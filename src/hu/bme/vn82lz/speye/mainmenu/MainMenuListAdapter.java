package hu.bme.vn82lz.speye.mainmenu;

import hu.bme.vn82lz.speye.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuListAdapter extends BaseAdapter {

	ArrayList<MainMenuListItem> listItems;

	public MainMenuListAdapter(ArrayList<MainMenuListItem> listItems) {
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listItems.get(arg0).getId();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		final MainMenuListItem listItem = listItems.get(position);

		LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.main_menu_list_item_row, null);

		ImageView imageViewIcon = (ImageView) itemView.findViewById(R.id.listItemImage);

		imageViewIcon.setImageResource(listItem.getIconFile());

		TextView textViewTitle = (TextView) itemView.findViewById(R.id.listItemText);
		textViewTitle.setText(listItem.getName());
		return itemView;
	}

}
