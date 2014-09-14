package ch.waelchli.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import ch.waelchli.model.Collection;
import ch.waelchli.model.DatabaseAccessor;
import ch.waelchli.notecard.R;

public class CollectionFragment extends Fragment implements CollectionDialogFragment.DialogListener {

	/**
	 * 
	 */
	private static final String NEW_COLLECTION_DIALOG = "new collection";
	private static final int REQUEST_NEW_COLLECTION_DIALOG = 100;
	/**
	 * 
	 */
	private ViewGroup collection_list;
	/**
	 * 
	 */
	private View hintView;
	/**
	 * 
	 */
	private DatabaseAccessor accessor;

	@Override
	public void onAttach(Activity activity) {
		accessor = new DatabaseAccessor(activity);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_collection, container, false);

		collection_list = (ViewGroup) rootView.findViewById(R.id.collection_list);
		hintView = (View) rootView.findViewById(android.R.id.empty);

		populateList();
		showHintIfNeeded();

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*
		 * Indicate that this fragment would like to influence the set of actions in the action bar.
		 */
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.collection, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_add_item:
			showDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showDialog() {
		DialogFragment dialog = new CollectionDialogFragment();
		dialog.setTargetFragment(this, REQUEST_NEW_COLLECTION_DIALOG);
		dialog.show(getChildFragmentManager(), NEW_COLLECTION_DIALOG);
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		EditText edit = (EditText) dialog.getDialog().findViewById(R.id.new_collection_name);
		String name = edit.getText().toString().trim();

		Collection newCollection = Collection.create(name, accessor);

		addListItem(name, newCollection.getId());
		hideHint();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	}

	private void addListItem(String title, long id_tag) {

		final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.collection_list_item, collection_list,
				false);
		((TextView) newView.findViewById(R.id.collection_name)).setText(title);

		newView.setTag(id_tag);
		collection_list.addView(newView, 0);
	}

	private void populateList() {
		List<Collection> collections = accessor.loadAllCollections();

		for (Collection c : collections) {
			addListItem(c.getName(), c.getId());
		}
	}

	private void showHint() {
		hintView.setVisibility(View.VISIBLE);
	}

	private void hideHint() {
		hintView.setVisibility(View.GONE);
	}

	/*
	 * If there are no rows remaining, show the empty view with a hint.
	 */
	private void showHintIfNeeded() {
		if (collection_list.getChildCount() == 0) {
			showHint();
		} else {
			hideHint();
		}
	}

	/**
	 * Gets invoked when the delete button of a list item gets clicked.
	 */
	public void onDeleteListItem(View view) {
		// View parent = (View) view.getParent();
		// accessor.deleteCollection((Long) parent.getTag());
		// collection_list.removeView(parent);
		// showHintIfNeeded();
	}

	public void onItemClicked(View view) {
		// Intent intent = new Intent(this, NotecardActivity.class);
		// View parent = (View) view.getParent();
		// intent.putExtra(EXTRA_SELECTED_ID, (Long) parent.getTag());
		// startActivity(intent);
	}
}
