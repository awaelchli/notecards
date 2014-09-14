package ch.waelchli.pager;

import ch.waelchli.pager.adapter.CardFacePagerAdapter;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CardFaceViewPager extends ViewPager {

	public CardFaceViewPager(Context context) {
		super(context);
	}

	public CardFaceViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		/*
		 * Never allow swiping to switch between faces.
		 */
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/*
		 * Never allow swiping to switch between faces.
		 */
		return false;
	}
	
	public void switchFace(){
		int face = getCurrentItem();
		if(face == CardFacePagerAdapter.POSITION_FRONT_FACE){
			setCurrentItem(CardFacePagerAdapter.POSITION_BACK_FACE);
		} else {
			setCurrentItem(CardFacePagerAdapter.POSITION_FRONT_FACE);
		}
	}

}
