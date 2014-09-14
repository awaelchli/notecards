package ch.waelchli.fragment.notecard;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ch.waelchli.broadcast.NotecardBroadcast;
import ch.waelchli.model.DatabaseAccessor;
import ch.waelchli.model.Notecard;
import ch.waelchli.notecard.R;
import ch.waelchli.task.BitmapWorkerTask;
import ch.waelchli.util.ImageAccessor;

public abstract class NotecardFaceFragment extends Fragment implements OnClickListener, TextWatcher, OnGlobalLayoutListener {

	private static final String EXTRA_IMAGE_URI = "ch.waelchli.fragment.EXTRA_IMAGE_URI";
	private static final int REQUEST_IMAGE_CAPTURE = 1;

	protected Notecard notecard;
	protected ImageView imageView;
	private ImageButton captureButton;
	private BroadcastReceiver broadcastReceiver;

	private String tempImageUri;

	@Override
	public void onAttach(Activity activity) {
		DatabaseAccessor acc = new DatabaseAccessor(activity);
		Bundle args = getArguments();
		notecard = acc.loadNotecard(args.getLong(NotecardFragment.EXTRA_NOTECARD_ID));
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tempImageUri = savedInstanceState.getString(EXTRA_IMAGE_URI);
		} else {
			tempImageUri = getImageUri();
		}

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int position = intent.getIntExtra(NotecardBroadcast.EXTRA_NOTECARD_POSITION, -1);
				if (notecard.getPosition() == position) {
					updateImage();
				} else {
					imageView.setImageBitmap(null);
				}
			}
		};
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_card_face, container, false);

		rootView.findViewById(R.id.button_flip_card).setOnClickListener(this);
		rootView.findViewById(R.id.button_remove_card).setOnClickListener(this);

		EditText edit = (EditText) rootView.findViewById(R.id.card_text);
		edit.addTextChangedListener(this);
		displayText(edit);

		TextView pageNumberView = (TextView) rootView.findViewById(R.id.notecard_page_number);
		displayIndex(pageNumberView);

		captureButton = (ImageButton) rootView.findViewById(R.id.button_open_camera);
		captureButton.setOnClickListener(this);

		imageView = (ImageView) rootView.findViewById(R.id.card_image);
		imageView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		return rootView;
	}

	@Override
	public void onResume() {
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
				new IntentFilter(NotecardBroadcast.INTENT_NOTECARD_SELECTION));

		if (imageView.getWidth() > 0 && imageView.getHeight() > 0) {
			updateImage();
		}
		super.onResume();
	}

	@Override
	public void onClick(View view) {

		NotecardFragment parent = (NotecardFragment) getParentFragment();

		switch (view.getId()) {
		case R.id.button_flip_card:
			parent.flip();
			break;
		case R.id.button_remove_card:
			parent.remove();
			break;
		case R.id.button_open_camera:
			takePicture();
			break;
		}
	}

	@Override
	public void onGlobalLayout() {
		imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		updateImage();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NotecardFaceFragment.REQUEST_IMAGE_CAPTURE) {

			if (resultCode == Activity.RESULT_OK) {
				setImageUri(tempImageUri);
			}

			if (resultCode == Activity.RESULT_CANCELED) {
				/*
				 * Reset the uri to the old one.
				 */
				tempImageUri = getImageUri();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		cancelPotentialBitmapWork();
		imageView.setImageBitmap(null);
		outState.putString(EXTRA_IMAGE_URI, tempImageUri);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	private void displayIndex(TextView pageNumberView) {
		int currentIndex = notecard.getPosition() + 1;
		int total = notecard.getCollection().size();
		String delim = getResources().getString(R.string.page_index_delimeter);
		pageNumberView.setText(currentIndex + delim + total);
	}

	protected abstract void displayText(EditText edit);

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	protected abstract void setImageUri(String stringUri);

	protected abstract String getImageUri();

	private void takePicture() {

		if (!hasCameraHardware()) {
			/*
			 * This device has no camera installed.
			 */
			Toast.makeText(getActivity(), R.string.message_no_camera, Toast.LENGTH_SHORT).show();
			return;
		}

		if (!isSDCardMounted()) {
			/*
			 * No SD card available to store the pictures.
			 */
			Toast.makeText(getActivity(), R.string.message_no_sdcard, Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
			/*
			 * No camera app installed that can take the image.
			 */
			Toast.makeText(getActivity(), R.string.message_no_camera_app, Toast.LENGTH_SHORT).show();
			return;
		}

		Uri fileUri = ImageAccessor.getInstance(getActivity()).getOutputImageFileUri();
		tempImageUri = fileUri.getPath();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		getParentFragment().getParentFragment().startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	}

	private boolean hasCameraHardware() {
		return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	private boolean isSDCardMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private WeakReference<BitmapWorkerTask> currentBitmapTask;

	private void updateImage() {
		if (!isBitmapWorkInProgress()) {
			BitmapWorkerTask task = new BitmapWorkerTask(imageView, null);
			currentBitmapTask = new WeakReference<BitmapWorkerTask>(task);
			task.execute(getImageUri());
		}
	}

	private boolean cancelPotentialBitmapWork() {
		if (currentBitmapTask != null && currentBitmapTask.get() != null) {
			currentBitmapTask.get().cancel(true);
			currentBitmapTask.clear();
			return true;
		}
		return false;
	}

	private boolean isBitmapWorkInProgress() {
		if (currentBitmapTask == null) {
			return false;
		}
		BitmapWorkerTask task = currentBitmapTask.get();
		return task != null && task.getStatus().equals(AsyncTask.Status.RUNNING);
	}
}
