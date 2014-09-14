package ch.waelchli.fragment.notecard;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import ch.waelchli.notecard.R;

public class NotecardBackFragment extends NotecardFaceFragment {

	public NotecardBackFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		/**
		 * The back of the notecard does not have a remove button.
		 */
		view.findViewById(R.id.button_remove_card).setVisibility(View.INVISIBLE);

		return view;
	}

	@Override
	public void afterTextChanged(Editable s) {
		notecard.setBack(s.toString().trim());
	}

	@Override
	protected void displayText(EditText edit) {
		edit.setText(notecard.getBack());
		edit.setHint(R.string.hint_answer);
	}

	@Override
	protected void setImageUri(String stringUri) {
		notecard.setBackImage(stringUri);
	}

	@Override
	protected String getImageUri() {
		return notecard.getBackImage();
	}
}