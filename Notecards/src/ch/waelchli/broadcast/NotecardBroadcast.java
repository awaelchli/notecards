package ch.waelchli.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class NotecardBroadcast {

	public static final String INTENT_NOTECARD_SELECTION = "notecard selection";
	public static final String EXTRA_NOTECARD_POSITION = "notecard position";

	private LocalBroadcastManager broadcastManager;

	public NotecardBroadcast(Context context) {
		broadcastManager = LocalBroadcastManager.getInstance(context);
	}

	public void sendPositionSelected(int position) {
		Intent intent = new Intent(INTENT_NOTECARD_SELECTION);
		intent.putExtra(EXTRA_NOTECARD_POSITION, position);
		broadcastManager.sendBroadcast(intent);
	}

}
