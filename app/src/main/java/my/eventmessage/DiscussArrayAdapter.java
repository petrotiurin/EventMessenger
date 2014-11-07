package my.eventmessage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscussArrayAdapter extends ArrayAdapter<Message> {

	private TextView text_view;
	private List<Message> messages = new ArrayList<Message>();
	private LinearLayout wrapper;

	@Override
	public void add(Message object) {
		messages.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.messages.size();
	}

	public Message getItem(int index) {
		return this.messages.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		Message comment = getItem(position);

		text_view = (TextView) row.findViewById(R.id.comment);

		text_view.setText(comment.content);

		text_view.setBackgroundResource(comment.position ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		wrapper.setGravity(comment.position ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}