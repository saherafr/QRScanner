package com.example.droiddesign.view.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;

import java.util.List;
import java.util.Map;

/**
 * This adapter is used to display the announcements in a recycler view.
 * The announcements are displayed in a card view with a title, message, and date.
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

	private final List<Map<String, Object>> announcementList;

	/**
	 * This class is used to hold the views of the announcement card.
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView titleTextView;
		public TextView messageTextView;
		public TextView dateTextView;

		/**
		 * Constructor to initialize the views of the announcement card.
		 * @param view The view of the announcement card.
		 */
		public ViewHolder(View view) {
			super(view);
			titleTextView = view.findViewById(R.id.chat_card_title);
			messageTextView = view.findViewById(R.id.chat_card_message);
			dateTextView = view.findViewById(R.id.chat_card_date);
		}
	}

	/**
	 * Constructor to initialize the announcement list.
	 * @param announcementList The list of announcements to be displayed.
	 */
	public AnnouncementAdapter(List<Map<String, Object>> announcementList) {
		this.announcementList = announcementList;
	}

	/**
	 * This method is called when the view holder is created.
	 * It inflates the layout of the announcement card.
	 * @param parent The parent view group.
	 * @param viewType The type of the view.
	 * @return The view holder of the announcement card.
	 */
	@NonNull
	@Override
	public AnnouncementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_annoucement, parent, false);
		return new ViewHolder(view);
	}

	/**
	 * This method is called when the view holder is bound to the recycler view.
	 * It sets the title, message, and date of the announcement card.
	 * @param holder The view holder of the announcement card.
	 * @param position The position of the announcement in the list.
	 */
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Map<String, Object> announcement = announcementList.get(position);
		holder.titleTextView.setText((String)announcement.get("title"));
		holder.messageTextView.setText((String)announcement.get("message"));
		holder.dateTextView.setText((String)announcement.get("date"));
	}

	/**
	 * This method is called to get the number of announcements in the list.
	 * @return The number of announcements in the list.
	 */
	@Override
	public int getItemCount() {
		return announcementList.size();
	}
}
