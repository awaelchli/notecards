package ch.waelchli.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import ch.waelchli.notecard.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class ImageAccessor {

	private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
	private static final String JPG_EXTENSION = ".jpg";
	private static final String IMG_PREFIX = "IMG_";

	private static ImageAccessor instance;

	private final Context context;

	private ImageAccessor(Context c) {
		context = c;
	}

	public static ImageAccessor getInstance(Context context) {
		if (instance == null) {
			instance = new ImageAccessor(context);
		}
		return instance;
	}

	/**
	 * Create a File for saving the image
	 */
	@SuppressLint("SimpleDateFormat")
	public File getOutputImageFile() {
		String dirName = context.getResources().getString(R.string.app_name);
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dirName);

		/*
		 * Create the storage directory if it does not exist.
		 */
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(context.getPackageName(), "failed to create directory: " + dirName);
				return null;
			}
		}

		/*
		 * Create a media file name based on date and time.
		 */
		String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
		File imageFile = new File(mediaStorageDir.getPath() + File.separator + IMG_PREFIX + timeStamp + JPG_EXTENSION);

		return imageFile;
	}

	public Uri getOutputImageFileUri() {
		return Uri.fromFile(getOutputImageFile());
	}

	public static boolean deleteImageFile(String filePath) {
		if (filePath == null)
			return false;

		File file = new File(filePath);
		return file.delete();
	}
}
