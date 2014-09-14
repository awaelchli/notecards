package ch.waelchli.database;

import android.provider.BaseColumns;

public final class Schema {

	public static final int DATABASE_VERSION = 12;
	public static final String DATABASE_NAME = "database.db";

	public static final String[] TABLES = { Collection.TABLE_NAME, Notecard.TABLE_NAME };
	public static final String[] CREATE_TABLES = { Collection.CREATE_TABLE, Notecard.CREATE_TABLE };

	
	private static final String COMMA = ",";

	/*
	 * Do not allow to instantiate this class.
	 */
	private Schema() {
	}

	public static abstract class Collection implements BaseColumns {

		public static final String TABLE_NAME = "Collection";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_POS_LEFT_OFF = "pos_left_off";

		public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY" + COMMA + COLUMN_NAME
				+ " TEXT" + COMMA + COLUMN_POS_LEFT_OFF + " INTEGER DEFAULT 0" + ")";

		private Collection() {
		}
	}

	public static abstract class Notecard implements BaseColumns {

		public static final String TABLE_NAME = "Notecard";
		public static final String COLUMN_FRONT = "front";
		public static final String COLUMN_BACK = "back";
		public static final String COLUMN_ID_COLLECTION = "id_collection";
		public static final String COLUMN_POSITION = "position";
		public static final String COLUMN_IMAGE_FRONT = "image_front";
		public static final String COLUMN_IMAGE_BACK = "image_back";

		public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY" + COMMA + COLUMN_FRONT
				+ " TEXT" + COMMA + COLUMN_BACK + " TEXT" + COMMA + COLUMN_ID_COLLECTION + " INTEGER DEFAULT NULL" + COMMA
				+ COLUMN_POSITION + " INTEGER DEFAULT NULL" + COMMA + COLUMN_IMAGE_FRONT + " TEXT" + COMMA + COLUMN_IMAGE_BACK + " TEXT"
				+ COMMA + "FOREIGN KEY (" + COLUMN_ID_COLLECTION + ")" + " REFERENCES " + Collection.TABLE_NAME + "(" + Collection._ID
				+ ") ON DELETE CASCADE)";

		private Notecard() {
		}
	}
}