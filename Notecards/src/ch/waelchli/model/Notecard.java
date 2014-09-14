package ch.waelchli.model;

import ch.waelchli.database.Schema;
import ch.waelchli.util.ImageAccessor;

public class Notecard {

	private final long id;
	private final DatabaseAccessor accessor;

	public static Notecard create(String front, String back, DatabaseAccessor acc) {
		long id = acc.storeNotecard(front, back);
		return new Notecard(id, acc);
	}

	protected Notecard(long notecard_id, DatabaseAccessor acc) {
		id = notecard_id;
		accessor = acc;
	}

	public long getId() {
		return id;
	}

	public String getFront() {
		return readStringColumn(Schema.Notecard.COLUMN_FRONT);
	}

	public String getBack() {
		return readStringColumn(Schema.Notecard.COLUMN_BACK);
	}

	public void setFront(String text) {
		updateStringColumn(Schema.Notecard.COLUMN_FRONT, text);
	}

	public void setBack(String text) {
		updateStringColumn(Schema.Notecard.COLUMN_BACK, text);
	}

	public int getPosition() {
		return readIntegerColumn(Schema.Notecard.COLUMN_POSITION);
	}

	public String getFrontImage() {
		return readStringColumn(Schema.Notecard.COLUMN_IMAGE_FRONT);
	}

	public String getBackImage() {
		return readStringColumn(Schema.Notecard.COLUMN_IMAGE_BACK);
	}

	public boolean setFrontImage(String uri) {
		if (uri != null && uri.equals(getFrontImage())) {
			/*
			 * The uri is the same and does not need to be updated/deleted.
			 */
			return true;
		}
		return deleteImageIfNotNull(getFrontImage()) && updateStringColumn(Schema.Notecard.COLUMN_IMAGE_FRONT, uri);
	}

	public boolean setBackImage(String uri) {
		if (uri != null && uri.equals(getBackImage())) {
			/*
			 * The uri is the same and does not need to be updated/deleted.
			 */
			return true;
		}
		return deleteImageIfNotNull(getBackImage()) && updateStringColumn(Schema.Notecard.COLUMN_IMAGE_BACK, uri);
	}

	/**
	 * @return The collection this notecard belongs to, null if it is not part of a collection.
	 */
	public Collection getCollection() {
		long collection_id = readIntegerColumn(Schema.Notecard.COLUMN_ID_COLLECTION);
		return accessor.loadCollection(collection_id);
	}

	private String readStringColumn(String column) {
		return accessor.readString(Schema.Notecard.TABLE_NAME, column, getId());
	}

	private int readIntegerColumn(String column) {
		return accessor.readInteger(Schema.Notecard.TABLE_NAME, column, getId());
	}

	private boolean updateStringColumn(String column, String newString) {
		return accessor.updateString(Schema.Notecard.TABLE_NAME, column, getId(), newString);
	}

	private static boolean deleteImageIfNotNull(String uri) {
		if (uri != null) {
			return ImageAccessor.deleteImageFile(uri);
		}
		return true;
	}
}
