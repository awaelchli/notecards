package ch.waelchli.model;

import java.util.ArrayList;
import java.util.List;

import ch.waelchli.database.Schema;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;

public class DatabaseAccessor extends AbstractDatabaseAccessor {

	public DatabaseAccessor(Context c) {
		super(c);
	}

	/**
	 * Reads the string value from a table entry defined by the given row id and column name.
	 * 
	 * @param table
	 *            The name of the table from which will be read.
	 * @param column
	 *            The name of the column must exist and hold the type string.
	 * @param id
	 *            The id of the row the string should be read from.
	 * @return Returns null if the row with the given id does not exist in the table.
	 */
	protected String readString(String table, String column, long id) {
		String[] columns = { column };
		String selection = BaseColumns._ID + "= ?";
		String[] selectionArgs = { Long.toString(id) };

		Cursor c = database.query(table, columns, selection, selectionArgs, null, null, null);

		String result;
		try {
			c.moveToFirst();
			result = c.getString(c.getColumnIndex(column));
		} finally {
			if (c != null)
				c.close();
		}

		return result;
	}

	/**
	 * Reads the integer value from a table entry defined by the given row id and column name.
	 * 
	 * @param table
	 *            The name of the table from which will be read.
	 * @param column
	 *            The name of the column must exist and hold the type integer.
	 * @param id
	 *            The id of the row the integer should be read from.
	 * @return Returns null if the row with the given id does not exist in the table.
	 */
	protected int readInteger(String table, String column, long id) {
		String[] columns = { column };
		String selection = BaseColumns._ID + "= ?";
		String[] selectionArgs = { Long.toString(id) };

		Cursor c = database.query(table, columns, selection, selectionArgs, null, null, null);

		int result;
		try {
			c.moveToFirst();
			result = c.getInt(c.getColumnIndex(column));
		} finally {
			if (c != null)
				c.close();
		}
		return result;
	}

	/**
	 * Updates the string value in a table entry defined by the given row id and column name.
	 * 
	 * @param table
	 *            The name of the table which will be updated.
	 * @param column
	 *            The name of the column must exist and hold the type string.
	 * @param id
	 *            The id of the row in which the the value should be updated.
	 * @param newString
	 *            The new string value to be stored. This should only be null if the table constraints are set accordingly.
	 * @return Returns false if the row with the given id does not exist in the table and true otherwise.
	 */
	protected boolean updateString(String table, String column, long id, String newString) {
		ContentValues values = new ContentValues();
		values.put(column, newString);
		String whereClause = BaseColumns._ID + " = ?";
		String[] whereArgs = { Long.toString(id) };

		int rows = database.update(table, values, whereClause, whereArgs);
		return rows == 1;
	}

	/**
	 * Updates the integer value in a table entry defined by the given row id and column name.
	 * 
	 * @param table
	 *            The name of the table which will be updated.
	 * @param column
	 *            The name of the column must exist and hold the type integer.
	 * @param id
	 *            The id of the row in which the the value should be updated.
	 * @param newInteger
	 *            The new integer value to be stored. This should only be null if the table constraints are set accordingly.
	 * @return Returns false if the row with the given id does not exist in the table and true otherwise.
	 */
	protected boolean updateInteger(String table, String column, long id, int newInteger) {
		ContentValues values = new ContentValues();
		values.put(column, newInteger);
		String whereClause = BaseColumns._ID + " = ?";
		String[] whereArgs = { Long.toString(id) };

		int rows = database.update(table, values, whereClause, whereArgs);
		return rows == 1;
	}

	/**
	 * Loads a collection from the table specified in {@link Schema.Collection}.
	 * 
	 * @param id
	 *            The id of the collection to be loaded.
	 * @return null if the id was not found in the table.
	 */
	public Collection loadCollection(long id) {
		String selection = Schema.Collection._ID + " = ?";
		String[] selectionArgs = { Long.toString(id) };

		Cursor c = database.query(Schema.Collection.TABLE_NAME, null, selection, selectionArgs, null, null, null);

		if (!c.moveToFirst()) {
			c.close();
			return null;
		}
		c.close();

		return new Collection(this, id);
	}

	/**
	 * Loads all collections stored in the {@link Schema.Collection} table.
	 * 
	 * @return A list of all collections in the database.
	 */
	public List<Collection> loadAllCollections() {
		String[] columns = { Schema.Collection._ID };
		Cursor c = database.query(Schema.Collection.TABLE_NAME, columns, null, null, null, null, null);
		List<Collection> list = new ArrayList<Collection>();

		try {

			while (c.moveToNext()) {
				int index = c.getColumnIndex(Schema.Collection._ID);
				list.add(new Collection(this, c.getLong(index)));
			}

		} finally {
			if (c != null)
				c.close();
		}

		return list;
	}

	/**
	 * Loads the notecard at the given position in the collection.
	 * 
	 * @param position
	 *            The zero-based position of the notecard in the collection.
	 * @param collection_id
	 *            The id of the collection the notecard is part of.
	 * @return null if the id was not found or the position is out of bounds.
	 */
	public Notecard loadNotecardInPosition(int position, long collection_id) {
		String[] columns = { Schema.Notecard._ID };
		String selection = Schema.Notecard.COLUMN_ID_COLLECTION + " = ? AND " + Schema.Notecard.COLUMN_POSITION + " = ?";
		String[] selectionArgs = { Long.toString(collection_id), Integer.toString(position) };

		Cursor c = database.query(Schema.Notecard.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

		Notecard notecard;
		try {
			c.moveToFirst();
			long id = c.getLong(c.getColumnIndex(Schema.Notecard._ID));
			notecard = loadNotecard(id);
		} finally {
			c.close();
		}

		return notecard;
	}

	/**
	 * Loads all notecards from a collection, ordered by their position.
	 * 
	 * @param collection_id
	 *            The id of the collection
	 * @return A list of all notecards stored in the collection. The list is empty if there are no notecards in this collection or the
	 *         collection with the given id does not exist.
	 */
	public List<Notecard> loadAllFromCollection(long collection_id) {
		List<Notecard> list = new ArrayList<Notecard>();

		String[] columns = { Schema.Notecard._ID };
		String selection = Schema.Notecard.COLUMN_ID_COLLECTION + " = ?";
		String[] selectionArgs = { Long.toString(collection_id) };
		String orderBy = Schema.Notecard.COLUMN_POSITION + " ASC";

		Cursor c = database.query(Schema.Notecard.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy);

		try {

			while (c.moveToNext()) {
				long id = c.getLong(c.getColumnIndex(Schema.Notecard._ID));
				Notecard notecard = new Notecard(id, this);
				list.add(notecard);
			}

		} finally {
			if (c != null)
				c.close();
		}

		return list;
	}

	/**
	 * Invokes {@link #loadAllFromCollection(long)} with the id of the given collection.
	 */
	public List<Notecard> loadAllFromCollection(Collection collection) {
		return loadAllFromCollection(collection.getId());
	}

	/**
	 * Deletes the collection with the given id.
	 * 
	 * @param id
	 *            The id of the collection to be deleted.
	 * @return true if the collection was found and removed, false otherwise.
	 */
	public boolean deleteCollection(long id) {
		String whereClause = Schema.Collection._ID + " = ?";
		String[] whereArgs = { Long.toString(id) };

		int rows = database.delete(Schema.Collection.TABLE_NAME, whereClause, whereArgs);
		return rows == 1;
	}

	/**
	 * Invokes {@link #deleteCollection(long)} with the id of the collection.
	 */
	public boolean deleteCollection(Collection collection) {
		return deleteCollection(collection.getId());
	}

	/**
	 * Loads a notecard from the table specified in {@link Schema.Notecard}.
	 * 
	 * @param id
	 *            The id of the notecard to be loaded.
	 * @return null if the id was not found in the table.
	 */
	public Notecard loadNotecard(long id) {
		String selection = Schema.Notecard._ID + " = ?";
		String[] selectionArgs = { Long.toString(id) };

		Cursor c = database.query(Schema.Notecard.TABLE_NAME, null, selection, selectionArgs, null, null, null);

		if (!c.moveToFirst()) {
			c.close();
			return null;
		}
		c.close();

		return new Notecard(id, this);
	}

	/**
	 * Inserts a new notecard into the database.
	 * 
	 * @param front
	 *            The text on the front of the notecard, cannot be null.
	 * @param back
	 *            The text on the back of the notecard, cannot be null.
	 * @return The id of the newly created row in the table, or -1 if the insertion failed.
	 */
	public long storeNotecard(String front, String back) {
		ContentValues values = new ContentValues();
		values.put(Schema.Notecard.COLUMN_FRONT, front);
		values.put(Schema.Notecard.COLUMN_BACK, back);

		return database.insert(Schema.Notecard.TABLE_NAME, null, values);
	}

	/**
	 * Deletes a notecard from the database.
	 * 
	 * @param notecard_id
	 *            The id of the notecard to be removed.
	 * @return true if the id was found and the notecard was removed, false otherwise.
	 */
	public boolean deleteNotecard(long notecard_id) {
		int position = readInteger(Schema.Notecard.TABLE_NAME, Schema.Notecard.COLUMN_POSITION, notecard_id);
		long collection_id = readInteger(Schema.Notecard.TABLE_NAME, Schema.Notecard.COLUMN_ID_COLLECTION, notecard_id);

		String whereClause = Schema.Notecard._ID + " = ?";
		String[] whereArgs = { Long.toString(notecard_id) };

		int rows = database.delete(Schema.Notecard.TABLE_NAME, whereClause, whereArgs);

		if (rows == 1) {
			updatePositionsOnRemove(collection_id, position);
			return true;
		}
		return false;
	}

	/**
	 * Invokes {@link #deleteNotecard(long)} with the id of the notecard.
	 * 
	 * @param notecard
	 *            The notecard to be deleted.
	 */
	public boolean deleteNotecard(Notecard notecard) {
		return deleteNotecard(notecard.getId());
	}

	/**
	 * Adds an existing notecard to a collection.
	 * 
	 * @param notecard_id
	 *            The id of the notecard to be added to the collection
	 * @param collection_id
	 *            The id of the collection the notecard will be added to.
	 * @return true if the notecard was added to the collection. False, if either the collection id or the notecard id was not found.
	 */
	public boolean addNotecard(long notecard_id, long collection_id) {
		int size = sizeOf(new Collection(this, collection_id));

		ContentValues values = new ContentValues();
		values.put(Schema.Notecard.COLUMN_ID_COLLECTION, collection_id);
		values.put(Schema.Notecard.COLUMN_POSITION, size);
		String whereClause = Schema.Notecard._ID + " = ?";
		String[] whereArgs = { Long.toString(notecard_id) };

		int rows = database.update(Schema.Notecard.TABLE_NAME, values, whereClause, whereArgs);
		return rows == 1;
	}

	/**
	 * Invokes {@link #addNotecard(long, long)} with the ids of the given notecard and collection.
	 * 
	 * @param notecard
	 *            The notecard to be added to the collection.
	 * @param collection
	 *            The collection the notecard will be added to.
	 */
	public boolean addNotecard(Notecard notecard, Collection collection) {
		return addNotecard(notecard.getId(), collection.getId());
	}

	/**
	 * Inserts a new collection into the {@link Schema.Collection} table.
	 * 
	 * @param name
	 *            The name of the new collection, cannot be null.
	 * @return The id of the newly created row, or -1 if the insertion failed.
	 */
	public long storeCollection(String name) {
		ContentValues values = new ContentValues();
		values.put(Schema.Collection.COLUMN_NAME, name);

		return database.insert(Schema.Collection.TABLE_NAME, null, values);
	}

	/**
	 * Removes a notecard from the collection, but does not remove the notecard from the database.
	 * 
	 * @param notecard_id
	 *            The id of the notecard to be removed.
	 * @return true, if the id was found and the notecard was removed from its collection, false otherwise.
	 */
	public boolean removeFromCollection(long notecard_id) {
		long collection_id = readInteger(Schema.Notecard.TABLE_NAME, Schema.Notecard.COLUMN_ID_COLLECTION, notecard_id);
		int position = readInteger(Schema.Notecard.TABLE_NAME, Schema.Notecard.COLUMN_POSITION, notecard_id);

		ContentValues values = new ContentValues();
		values.putNull(Schema.Notecard.COLUMN_ID_COLLECTION);
		values.putNull(Schema.Notecard.COLUMN_POSITION);
		String whereClause = Schema.Notecard._ID + " = ?";
		String[] whereArgs = { Long.toString(notecard_id) };

		int rows = database.update(Schema.Notecard.TABLE_NAME, values, whereClause, whereArgs);

		if (rows == 1) {
			updatePositionsOnRemove(collection_id, position);
			return true;
		}
		return false;

	}

	/**
	 * Invokes {@link #removeFromCollection(long)} with the id of the given notecard.
	 * 
	 * @param notecard
	 *            The notecard to be removed from its collection.
	 */
	public boolean removeFromCollection(Notecard notecard) {
		return removeFromCollection(notecard.getId());
	}

	/**
	 * Returns the number of notecards in the given collection.
	 * 
	 * @param collection
	 *            The collection, must not be null.
	 * @return The size of the collection, can be null if the id of the collection is not valid.
	 */
	public int sizeOf(Collection collection) {
		String selection = Schema.Notecard.COLUMN_ID_COLLECTION + " = ?";
		String[] selectionArgs = { Long.toString(collection.getId()) };

		Cursor c = database.query(Schema.Notecard.TABLE_NAME, null, selection, selectionArgs, null, null, null);
		int count;
		try {
			count = c.getCount();
		} finally {
			c.close();
		}

		return count;
	}

	private boolean updatePositionsOnRemove(long collection_id, int removedPosition) {

		List<Notecard> list = loadAllFromCollection(collection_id);

		if (list.size() == removedPosition) {
			/*
			 * The removed notecard was at the end of the list.
			 */
			return true;
		}

		List<Notecard> toFix = list.subList(removedPosition, list.size());
		try {
			database.beginTransaction();
			for (Notecard card : toFix) {

				boolean success = updateInteger(Schema.Notecard.TABLE_NAME, Schema.Notecard.COLUMN_POSITION, card.getId(),
						card.getPosition() - 1);

				if (!success) {
					return false;
				}
			}
			database.setTransactionSuccessful();
		} catch (SQLiteException e) {
			return false;
		} finally {
			database.endTransaction();
		}

		return true;
	}
}
