package ch.waelchli.fragment.notecard;

import android.text.Editable;
import android.widget.EditText;
import ch.waelchli.notecard.R;

public class NotecardFrontFragment extends NotecardFaceFragment {

	public NotecardFrontFragment() {

	}

	@Override
	public void afterTextChanged(Editable s) {
		notecard.setFront(s.toString().trim());
	}

	@Override
	protected void displayText(EditText edit) {
		edit.setText(notecard.getFront());
		edit.setHint(R.string.hint_question);
	}

	@Override
	protected void setImageUri(String stringUri) {
		notecard.setFrontImage(stringUri);
	}

	@Override
	protected String getImageUri() {
		return notecard.getFrontImage();
	}
}
