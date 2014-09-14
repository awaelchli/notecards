package ch.waelchli.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class NotecardViewPager extends ViewPager {

	public static final int OFFSCREEN_LIMIT = 5;

	public NotecardViewPager(Context context) {
		super(context);
	}

	public NotecardViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void next() {
		setCurrentItem(getCurrentItem() + 1);
	}

	public void previous() {
		if (getCurrentItem() > 0)
			setCurrentItem(getCurrentItem() - 1);
	}
}
