package ch.waelchli.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ch.waelchli.broadcast.NotecardBroadcast;
import ch.waelchli.fragment.notecard.NotecardFragment;
import ch.waelchli.model.Collection;
import ch.waelchli.model.DatabaseAccessor;
import ch.waelchli.model.Notecard;
import ch.waelchli.notecard.R;
import ch.waelchli.pager.NotecardViewPager;
import ch.waelchli.pager.adapter.NotecardPagerAdapter;
import ch.waelchli.pager.transformer.DepthPageTransformer;

public class NotecardPagerFragment extends Fragment implements NotecardFragment.OnCardInteractionListener {

	private static final String ARG_COLLECTION_ID = "collection id";

	/**
	 * The collection this fragment is displaying.
	 */
	private Collection collection;
	/**
	 * 
	 */
	private NotecardPagerAdapter pagerAdapter;
	/**
	 * The ViewPager displayed in this fragment.
	 */
	private NotecardViewPager viewPager;

	/**
	 * Returns a new instance of this fragment for the collection id.
	 */
	public static NotecardPagerFragment newInstance(long collection_id) {
		NotecardPagerFragment fragment = new NotecardPagerFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_COLLECTION_ID, collection_id);
		fragment.setArguments(args);
		return fragment;
	}

	public NotecardPagerFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		DatabaseAccessor acc = new DatabaseAccessor(activity);
		collection = acc.loadCollection(getArguments().getLong(ARG_COLLECTION_ID));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.notecard_pager, container, false);
		viewPager = (NotecardViewPager) rootView.findViewById(R.id.pager);
		setUpPager();
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
	public void onResume() {
		viewPager.setCurrentItem(collection.getPositionLeftOff());
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notecard, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		boolean isNextEnabled = viewPager.getCurrentItem() < pagerAdapter.getCount() - 1;
		boolean isPrevEnabled = viewPager.getCurrentItem() > 0;

		MenuItem next = menu.findItem(R.id.action_next_page);
		MenuItem prev = menu.findItem(R.id.action_previous_page);

		next.setEnabled(isNextEnabled);
		prev.setEnabled(isPrevEnabled);

		int enabled = getResources().getInteger(R.integer.alpha_action_enabled);
		int disabled = getResources().getInteger(R.integer.alpha_action_disabled);
		next.getIcon().setAlpha(isNextEnabled ? enabled : disabled);
		prev.getIcon().setAlpha(isPrevEnabled ? enabled : disabled);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_next_page:
			viewPager.next();
			return true;
		case R.id.action_previous_page:
			viewPager.previous();
			return true;
		case R.id.action_new_notecard:
			addNewCard();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		collection.setPositionLeftOff(viewPager.getCurrentItem());
		super.onPause();
	}

	private void setUpPager() {
		pagerAdapter = new NotecardPagerAdapter(getChildFragmentManager(), collection);

		viewPager.setAdapter(pagerAdapter);
		viewPager.setPageTransformer(true, new DepthPageTransformer());
		viewPager.setOffscreenPageLimit(NotecardViewPager.OFFSCREEN_LIMIT);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				NotecardBroadcast broadcast = new NotecardBroadcast(getActivity());
				broadcast.sendPositionSelected(position);
				/*
				 * Recreate the options menu since the navigation buttons depend on which page is selected.
				 */
				getActivity().invalidateOptionsMenu();
				super.onPageSelected(position);
			}
		});
	}

	private void addNewCard() {
		Notecard notecard = Notecard.create("", "", new DatabaseAccessor(getActivity()));
		collection.add(notecard);
		pagerAdapter.notifyDataSetChanged();
		viewPager.setCurrentItem(pagerAdapter.getCount() - 1);
	}

	@Override
	public void onRemove(Notecard notecard) {
		/*
		 * Deletes the images from this notecard TODO: delete images only when trash gets emptied.
		 */
		notecard.setFrontImage(null);
		notecard.setBackImage(null);

		collection.remove(notecard);
		pagerAdapter.notifyDataSetChanged();
		Toast.makeText(getActivity(), R.string.message_notecard_deleted, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		List<Fragment> fragments = getChildFragmentManager().getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) {
//				if (fragment.equals(pagerAdapter.getItem(viewPager.getCurrentItem()))) {
					fragment.onActivityResult(requestCode, resultCode, data);
//				}
			}
		}
	}
}