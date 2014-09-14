package ch.waelchli.model;

import java.util.List;

import ch.waelchli.database.Schema;

public class Collection {

	/**
	 * 
	 */
	private final long id;
	/**
	 * 
	 */
	private final DatabaseAccessor accessor;

	/**
	 * Creates a new Collection using the given CollectionAccessor.
	 * 
	 * @param name
	 *            The name of the Collection.
	 * @param acc
	 * @return
	 */
	public static Collection create(String name, DatabaseAccessor acc) {
		long id = acc.storeCollection(name);
		return new Collection(acc, id);
	}

	protected Collection(DatabaseAccessor acc, long collection_id) {
		accessor = acc;
		id = collection_id;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return readStringColumn(Schema.Collection.COLUMN_NAME);
	}

	/**
	 * @param notecard
	 *            The Notecard to be added must exist in the database.
	 */
	public void add(Notecard notecard) {
		accessor.addNotecard(notecard, this);
	}

	/**
	 * 
	 * @param notecard
	 *            The Notecard to be removed must exist in database and must be in this collection.
	 */
	public void remove(Notecard notecard) {
		accessor.removeFromCollection(notecard);
	}

	public Notecard get(int position) {
		return accessor.loadNotecardInPosition(position, getId());
	}

	public int size() {
		return accessor.sizeOf(this);
	}

	public List<Notecard> getAll() {
		return accessor.loadAllFromCollection(this);
	}

	public int getPositionLeftOff() {
		return readIntegerColumn(Schema.Collection.COLUMN_POS_LEFT_OFF);
	}

	public boolean setPositionLeftOff(int position) {
		return updateIntegerColumn(Schema.Collection.COLUMN_POS_LEFT_OFF, position);
	}

	private String readStringColumn(String column) {
		return accessor.readString(Schema.Collection.TABLE_NAME, column, getId());
	}

	private int readIntegerColumn(String column) {
		return accessor.readInteger(Schema.Collection.TABLE_NAME, column, getId());
	}

	private boolean updateIntegerColumn(String column, int newInteger) {
		return accessor.updateInteger(Schema.Collection.TABLE_NAME, column, getId(), newInteger);
	}

	@Override
	public String toString() {
		return accessor.readString(Schema.Collection.TABLE_NAME, Schema.Collection.COLUMN_NAME, getId());
	}
}
