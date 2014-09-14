package ch.waelchli.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ch.waelchli.notecard.R;

public class CollectionDialogFragment extends DialogFragment {

	/**
	 * The fragment that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each
	 * method passes the DialogFragment in case the host needs to query it.
	 */
	public interface DialogListener {

		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	private DialogListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Verify that the host fragment implements the callback interface.
		 */
		try {
			listener = (DialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(getTargetFragment().toString() + " must implement DialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View container = inflater.inflate(R.layout.dialog_new_collection, null);
		builder.setView(container);
		builder.setMessage(R.string.dialog_new_collection);
		builder.setPositiveButton(R.string.add_collection, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				/*
				 * Overridden in the CustomOnClickListener below. This onClick method is still needed for the creation of the
				 * PositiveButton.
				 */
			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				listener.onDialogNegativeClick(CollectionDialogFragment.this);
			}
		});

		AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		final AlertDialog dialog = (AlertDialog) getDialog();

		if (dialog != null) {
			Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new CustomOnClickListener(dialog));
		}
	}

	private class CustomOnClickListener implements View.OnClickListener {

		private final Dialog dialog;

		public CustomOnClickListener(Dialog d) {
			dialog = d;
		}

		@Override
		public void onClick(View v) {
			EditText txt = (EditText) dialog.findViewById(R.id.new_collection_name);

			if (txt.getText().toString().trim().isEmpty()) {
				showWarning();
			} else {
				listener.onDialogPositiveClick(CollectionDialogFragment.this);
				dismiss();
			}
		}

		private void showWarning() {
			TextView warning = (TextView) dialog.findViewById(R.id.warning_text_no_name);
			warning.setVisibility(View.VISIBLE);
		}

	}
}
