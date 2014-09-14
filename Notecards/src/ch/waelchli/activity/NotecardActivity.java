package ch.waelchli.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import ch.waelchli.drawer.NavigationDrawerFragment;
import ch.waelchli.drawer.NavigationDrawerFragment.NavigationDrawerCallbacks;
import ch.waelchli.fragment.CollectionFragment;
import ch.waelchli.fragment.NotecardPagerFragment;
import ch.waelchli.model.Collection;
import ch.waelchli.notecard.R;

public class NotecardActivity extends FragmentActivity implements NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment navigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notecard);

		navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		title = getTitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if (!navigationDrawerFragment.isDrawerOpen()) {
			restoreActionBar();
			return true;
		}
		return true;
	}

	@Override
	public void onDrawerCollectionSelected(Collection collection) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, NotecardPagerFragment.newInstance(collection.getId())).commit();

		title = collection.getName();
	}

	@Override
	public void onDrawerNavigationChange(int position) {
		switch (position) {
		case NavigationDrawerFragment.MENU_POSITION_COLLECTIONS:
			attachCollectionsOverview();
			break;
		case NavigationDrawerFragment.MENU_POSITION_TRASH:
			break;
		case NavigationDrawerFragment.MENU_POSITION_SETTINGS:
			break;
		}
	}

	private void attachCollectionsOverview() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, new CollectionFragment()).commit();
		setTitle(R.string.title_collections);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}
}
