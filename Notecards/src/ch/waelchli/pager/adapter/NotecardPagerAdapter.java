package ch.waelchli.pager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import ch.waelchli.fragment.notecard.NotecardFragment;
import ch.waelchli.model.Collection;
import ch.waelchli.model.Notecard;

public class NotecardPagerAdapter extends FixedFragmentStatePagerAdapter {

	private final Collection collection;

	public NotecardPagerAdapter(FragmentManager fm, Collection c) {
		super(fm);
		collection = c;
	}

	@Override
	public Fragment getItem(int position) {
		Notecard notecard = collection.get(position);
		return NotecardFragment.newInstance(notecard);
	}

	@Override
	public int getItemPosition(Object object) {
		/*
		 * Forces the re-instantiation of the item when notifyDatasetChanged is invoked.
		 */
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return collection.size();
	}

	@Override
	public String getTag(int position) {
		Notecard card = collection.get(position);
		return Long.toString(card.getId());
	}
}
