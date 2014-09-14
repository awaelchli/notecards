package ch.waelchli.drawer;

import ch.waelchli.model.Collection;

public class DrawerCollectionItem implements DrawerListItem {

	public static final int TYPE = 2;

	private Collection collection;

	private DrawerCollectionItem() {
	}

	public static DrawerCollectionItem create(Collection collection) {
		DrawerCollectionItem item = new DrawerCollectionItem();
		item.setCollection(collection);
		return item;
	}

	@Override
	public String getLabel() {
		return collection.getName();
	}

	@Override
	public int getType() {
		return TYPE;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Collection getCollection() {
		return collection;
	}

	private void setCollection(Collection collection) {
		this.collection = collection;
	}
}