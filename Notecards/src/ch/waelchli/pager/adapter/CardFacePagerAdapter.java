package ch.waelchli.pager.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ch.waelchli.fragment.notecard.NotecardBackFragment;
import ch.waelchli.fragment.notecard.NotecardFaceFragment;
import ch.waelchli.fragment.notecard.NotecardFrontFragment;

public class CardFacePagerAdapter extends FragmentStatePagerAdapter {

	public static final int FACES = 2;
	public static final int POSITION_FRONT_FACE = 0;
	public static final int POSITION_BACK_FACE = 1;

	private final NotecardFaceFragment front, back;

	public CardFacePagerAdapter(FragmentManager fm, Bundle bundle) {
		super(fm);
		front = new NotecardFrontFragment();
		back = new NotecardBackFragment();
		front.setArguments(bundle);
		back.setArguments(bundle);
	}

	@Override
	public Fragment getItem(int position) {

		switch (position) {
		case POSITION_FRONT_FACE:
			return front;
		case POSITION_BACK_FACE:
			return back;
		}

		return null;
	}

	@Override
	public int getCount() {
		return FACES;
	}

}
