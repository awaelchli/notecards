package ch.waelchli.drawer;

public class DrawerMenuItem implements DrawerListItem {

	public static final int TYPE = 1;

	private String label;
	private int icon;

	private DrawerMenuItem() {
	}

	public static DrawerMenuItem create(String label, int icon) {
		DrawerMenuItem item = new DrawerMenuItem();
		item.setLabel(label);
		item.setIcon(icon);
		return item;
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
		return true;
	}

	public int getIcon() {
		return icon;
	}

	private void setLabel(String label) {
		this.label = label;
	}

	private void setIcon(int icon) {
		this.icon = icon;
	}
}
