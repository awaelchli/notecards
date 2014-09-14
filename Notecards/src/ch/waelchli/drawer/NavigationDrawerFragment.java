package ch.waelchli.drawer;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.waelchli.model.Collection;
import ch.waelchli.model.DatabaseAccessor;
import ch.waelchli.notecard.R;

/**
 * @see <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"> Design guidelines</a>
 */
public class NavigationDrawerFragment extends Fragment {

	public static final int MENU_POSITION_COLLECTIONS = 0;
	public static final int MENU_POSITION_TRASH = 1;
	public static final int MENU_POSITION_SETTINGS = 2;

	/**
	 * Remember the position of the selected collection.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_collection_from_drawer";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually expands it. This shared preference tracks
	 * this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * The callbacks that must be implemented by the activity this fragment gets attached to.
	 */
	private NavigationDrawerCallbacks navigationCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private View fragmentContainerView;

	private ListView listView;
	private DrawerListAdapter listAdapter;
	private List<DrawerListItem> menu;

	private int currentSelectedPosition = 0;
	private boolean fromSavedInstanceState;
	private boolean userLearnedDrawer;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			navigationCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement " + NavigationDrawerCallbacks.class.getSimpleName() + ".");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Read in the flag indicating whether or not the user has demonstrated awareness of the drawer.
		 */
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			fromSavedInstanceState = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		listView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		createMenu();

		listAdapter = new DrawerListAdapter(getActionBar().getThemedContext(), android.R.layout.simple_list_item_activated_1, menu) {

			@Override
			public void notifyDataSetChanged() {
				createMenu();
				super.notifyDataSetChanged();
			}

		};
		listView.setAdapter(listAdapter);

		selectItem(currentSelectedPosition);

		return listView;
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
		if (drawerLayout != null && isDrawerOpen()) {
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (isDrawerOpen()) {
			/*
			 * Hide the currently displayed actions in the actionbar.
			 */
			for (int i = 0; i < menu.size(); i++) {
				menu.getItem(i).setVisible(false);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		navigationCallbacks = null;
	}

	public boolean isDrawerOpen() {
		return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		fragmentContainerView = getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.drawable.ic_drawer, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				listAdapter.notifyDataSetChanged();
				super.onDrawerSlide(drawerView, slideOffset);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!userLearnedDrawer) {
					/*
					 * The user manually opened the drawer; store this flag to prevent auto-showing the navigation drawer automatically in
					 * the future.
					 */
					userLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				getActivity().invalidateOptionsMenu();
				listAdapter.notifyDataSetChanged();
			}
		};

		if (!userLearnedDrawer && !fromSavedInstanceState) {
			drawerLayout.openDrawer(fragmentContainerView);
		}

		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});

		drawerLayout.setDrawerListener(drawerToggle);
	}

	private void createMenu() {
		DatabaseAccessor acc = new DatabaseAccessor(getActivity());
		final List<Collection> collections = acc.loadAllCollections();

		if (menu == null) {
			menu = new ArrayList<DrawerListItem>();
		}
		menu.clear();
		menu.add(MENU_POSITION_COLLECTIONS,
				DrawerMenuItem.create(getString(R.string.drawer_menu_collections), R.drawable.ic_action_collection));
		menu.add(MENU_POSITION_TRASH, DrawerMenuItem.create(getString(R.string.drawer_menu_trash), R.drawable.ic_action_discard));
		menu.add(MENU_POSITION_SETTINGS, DrawerMenuItem.create(getString(R.string.drawer_menu_settings), R.drawable.ic_action_settings));
		menu.add(DrawerSectionItem.create(getString(R.string.section_header_collections)));

		for (Collection c : collections) {
			menu.add(DrawerCollectionItem.create(c));
		}
	}

	private void selectItem(int position) {
		currentSelectedPosition = position;

		if (listView != null) {
			listView.setItemChecked(position, true);
		}
		
		if (drawerLayout != null) {
			drawerLayout.closeDrawer(fragmentContainerView);
		}
		
		if (navigationCallbacks == null) {
			return;
		}

		DrawerListItem item = listAdapter.getItem(position);

		switch (item.getType()) {
		case DrawerCollectionItem.TYPE:
			Collection collection = ((DrawerCollectionItem) item).getCollection();
			navigationCallbacks.onDrawerCollectionSelected(collection);
			break;
		case DrawerMenuItem.TYPE:
			navigationCallbacks.onDrawerNavigationChange(position);
			break;
		}
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app 'context', rather than just what's in the
	 * current screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when a collection in the navigation drawer is selected.
		 */
		void onDrawerCollectionSelected(Collection collection);

		void onDrawerNavigationChange(int position);
	}
}
