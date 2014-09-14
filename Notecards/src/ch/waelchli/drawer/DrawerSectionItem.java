package ch.waelchli.drawer;

public class DrawerSectionItem implements DrawerListItem {

	public static final int TYPE = 0;
	private String label;

	private DrawerSectionItem() {

	}

	public static DrawerSectionItem create(String label) {
		DrawerSectionItem section = new DrawerSectionItem();
		section.setLabel(label);
		return section;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public int getType() {
		return TYPE;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	private void setLabel(String label) {
		this.label = label;
	}
}
