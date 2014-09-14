package ch.waelchli.database;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class DbHelper extends SQLiteOpenHelper {

	private static DbHelper instance;

	private DbHelper(Context c) {
		super(c, Schema.DATABASE_NAME, null, Schema.DATABASE_VERSION);
	}

	public static DbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String query : Schema.CREATE_TABLES) {
			db.execSQL(query);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String table : Schema.TABLES) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			/*
			 * Enable foreign key constraints for devices with api < jelly bean
			 */
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onConfigure(SQLiteDatabase db) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			db.setForeignKeyConstraintsEnabled(true);
		}

		super.onConfigure(db);
	}
}
