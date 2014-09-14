package ch.waelchli.task;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

	/*
	 * Use a WeakReference to ensure the ImageView can be garbage collected
	 */
	private final WeakReference<ImageView> imageViewReference;
	private final int width;
	private final int height;

	public BitmapWorkerTask(ImageView imageView, LruCache<String, Bitmap> memoryCache) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		width = imageViewReference.get().getWidth();
		height = imageViewReference.get().getHeight();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		String filePath = params[0];
		Bitmap bitmap = null;
		if (filePath != null) {
			bitmap = decodeSampledBitmapFromFile(filePath, width, height);
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			imageView.setImageBitmap(bitmap);
		}
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	private static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
}
