package ch.waelchli.fragment.notecard;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.waelchli.model.DatabaseAccessor;
import ch.waelchli.model.Notecard;
import ch.waelchli.notecard.R;
import ch.waelchli.pager.CardFaceViewPager;
import ch.waelchli.pager.adapter.CardFacePagerAdapter;
import ch.waelchli.pager.transformer.FlipFaceTransformer;

public class NotecardFragment extends Fragment {

	public static final String EXTRA_NOTECARD_ID = "ch.waelchli.notecard.NOTECARD_ID";

	private CardFaceViewPager viewPager;
	private Notecard notecard;

	public static NotecardFragment newInstance(Notecard notecard) {
		NotecardFragment fragment = new NotecardFragment();
		Bundle args = new Bundle();
		args.putLong(NotecardFragment.EXTRA_NOTECARD_ID, notecard.getId());
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		DatabaseAccessor accessor = new DatabaseAccessor(activity);
		notecard = accessor.loadNotecard(getArguments().getLong(EXTRA_NOTECARD_ID));
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_card, container, false);

		viewPager = (CardFaceViewPager) rootView.findViewById(R.id.card_face_pager);
		final CardFacePagerAdapter pagerAdapter = new CardFacePagerAdapter(getChildFragmentManager(), getArguments());

		viewPager.setAdapter(pagerAdapter);
		viewPager.setPageTransformer(true, new FlipFaceTransformer());
		return rootView;
	}

	protected void flip() {
		viewPager.switchFace();
	}

	protected void remove() {
		try {
			OnCardInteractionListener parent = (OnCardInteractionListener) getParentFragment();
			parent.onRemove(notecard);
		} catch (ClassCastException e) {
			throw new ClassCastException("Parent fragment must implement " + OnCardInteractionListener.class.getSimpleName() + ".");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		List<Fragment> fragments = getChildFragmentManager().getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	public static interface OnCardInteractionListener {

		public void onRemove(Notecard notecard);
	}
}
