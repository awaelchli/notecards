package ch.waelchli.model;

import ch.waelchli.database.DbHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDatabaseAccessor {

	protected final Context context;
	protected DbHelper helper;
	protected SQLiteDatabase database;
	private boolean isOpen;

	public AbstractDatabaseAccessor(Context c) {
		context = c;
		open();
	}

	public void open() {
		helper = DbHelper.getInstance(context);
		database = helper.getWritableDatabase();
		isOpen = true;
	}

	public void close() {
		if (isOpen) {
			helper.close();
			isOpen = false;
		}
	}
	
	public boolean isOpen(){
		return isOpen;
	}
}
