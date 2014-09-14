package ch.waelchli.drawer;

import java.util.List;

import ch.waelchli.notecard.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends ArrayAdapter<DrawerListItem> {

	private static final int VIEW_TYPE_COUNT = 3;

	private final LayoutInflater inflater;

	public DrawerListAdapter(Context context, int resource, List<DrawerListItem> objects) {
		super(context, resource, objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		DrawerListItem item = getItem(position);
		switch (item.getType()) {
		case DrawerSectionItem.TYPE:
			view = getSectionView(convertView, parent, item);
			break;
		case DrawerMenuItem.TYPE:
			view = getItemView(convertView, parent, item);
			break;
		case DrawerCollectionItem.TYPE:
			view = getCollectionItemView(convertView, parent, item);
			break;
		}

		return view;
	}

	private View getItemView(View convertView, ViewGroup parent, DrawerListItem item) {
		DrawerMenuItem menuItem = (DrawerMenuItem) item;
		NavigationItemHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_menu_item, parent, false);
			TextView labelView = (TextView) convertView.findViewById(R.id.menu_item_label);
			ImageView iconView = (ImageView) convertView.findViewById(R.id.menu_item_icon);

			holder = new NavigationItemHolder();
			holder.labelView = labelView;
			holder.iconView = iconView;

			convertView.setTag(holder);
		}

		if (holder == null) {
			holder = (NavigationItemHolder) convertView.getTag();
		}

		holder.labelView.setText(menuItem.getLabel());
		holder.iconView.setImageResource(menuItem.getIcon());

		return convertView;
	}

	private View getSectionView(View convertView, ViewGroup parent, DrawerListItem item) {
		DrawerSectionItem section = (DrawerSectionItem) item;
		SimpleHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_section_item, parent, false);
			TextView labelView = (TextView) convertView.findViewById(R.id.section_header_label);

			holder = new SimpleHolder();
			holder.labelView = labelView;
			convertView.setTag(holder);
		}

		if (holder == null) {
			holder = (SimpleHolder) convertView.getTag();
		}

		holder.labelView.setText(section.getLabel());

		return convertView;
	}

	private View getCollectionItemView(View convertView, ViewGroup parent, DrawerListItem item) {
		DrawerCollectionItem collectionItem = (DrawerCollectionItem) item;
		SimpleHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_collection_item, parent, false);
			TextView labelView = (TextView) convertView.findViewById(R.id.collection_item_label);

			holder = new SimpleHolder();
			holder.labelView = labelView;
			convertView.setTag(holder);
		}

		if (holder == null) {
			holder = (SimpleHolder) convertView.getTag();
		}

		holder.labelView.setText(collectionItem.getLabel());

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType();
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).isEnabled();
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	private static class NavigationItemHolder {
		private TextView labelView;
		private ImageView iconView;
	}

	private class SimpleHolder {
		private TextView labelView;
	}
}
