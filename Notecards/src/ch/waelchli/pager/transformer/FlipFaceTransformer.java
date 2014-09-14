package ch.waelchli.pager.transformer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class FlipFaceTransformer implements PageTransformer {

	@Override
	public void transformPage(View view, float position) {

		int pageWidth = view.getWidth();

		if (position < -1) {
			view.setAlpha(0);
		} else if (position < -0.5) {
			view.setAlpha(0);
		} else if (position < 0) {
			view.setAlpha(1);
			view.setRotationY(position * 180);
			view.setTranslationX(-position * pageWidth);
		} else if (position < 0.5) {
			view.setAlpha(1);
			view.setRotationY(position * 180);
			view.setTranslationX(-position * pageWidth);
		} else if (position <= 1) {
			view.setAlpha(0);
		} else {
			view.setAlpha(0);
		}
	}

}
