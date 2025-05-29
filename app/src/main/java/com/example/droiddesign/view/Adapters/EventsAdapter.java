package com.example.droiddesign.view.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter class for populating the RecyclerView with events.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

	/**
	 * A list containing Event objects to be displayed in the RecyclerView.
	 */
	private final List<Event> eventsList;

	/**
	 * An interface defining the onItemClick method to handle item click events in the RecyclerView.
	 * Implementations of this interface can be passed to the EventsAdapter to handle item clicks.
	 */
	private final OnItemClickListener listener;

	/**
	 * Interface for handling item click events.
	 */
	public interface OnItemClickListener {
		void onItemClick(Event event);
	}

	/**
	 * Constructor for the EventsAdapter.
	 *
	 * @param eventsList List of events to be displayed.
	 * @param listener   Listener for item click events.
	 */
	public EventsAdapter(List<Event> eventsList, OnItemClickListener listener) {
		this.eventsList = eventsList;
		this.listener = listener;
	}

	/**
	 * Inflates the layout for each event card.
	 *
	 * @param parent   The parent view group.
	 * @param viewType The type of view to be inflated.
	 * @return An instance of EventViewHolder.
	 */
	@NonNull
	@Override
	public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
		return new EventViewHolder(view);
	}

	/**
	 * Binds event data to the views within each card.
	 *
	 * @param holder   The view holder to bind data to.
	 * @param position The position of the item within the RecyclerView.
	 */
	@Override
	public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
		Event event = eventsList.get(position);
		holder.textEventName.setText(event.getEventName());
		holder.textLocation.setText(event.getEventLocation());

		// Adjusted date parsing to handle strings like "08 Mar" or "14 March"
		String dateString = event.getEventDate();
		if (dateString != null && !dateString.isEmpty()) {
			// Define format based on your input string
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
			Calendar cal = Calendar.getInstance();
			try {
				Date date = dateFormat.parse(dateString);
				if (date != null) {
					cal.setTime(date);
					// Assume the current year if no year is provided in the dateString
					cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
					int day = cal.get(Calendar.DAY_OF_MONTH);
					String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
					int year = cal.get(Calendar.YEAR);

					holder.dateDay.setText(String.valueOf(day));
					holder.dateMonth.setText(month);
					holder.dateYear.setText(String.valueOf(year));
				}
			} catch (ParseException e) {
				Log.e("EventAdapter", "Error parsing date string: " + dateString);
				holder.dateDay.setText("");
				holder.dateMonth.setText("");
				holder.dateYear.setText("");
			}
		} else {
			Log.e("EventAdapter", "Date string is null or empty for event: " + event.getEventName());
			holder.dateDay.setText("");
			holder.dateMonth.setText("");
			holder.dateYear.setText("");
		}

		holder.bind(event, listener);
	}

	/**
	 * Sets the events data in the adapter.
	 *
	 * @param events List of events to be displayed.
	 */
	public void setEvents(List<Event> events) {
		this.eventsList.clear();
		this.eventsList.addAll(events);
		notifyDataSetChanged();
	}

	/**
	 * Gets the number of events in the list.
	 *
	 * @return The number of events.
	 */
	@Override
	public int getItemCount() {
		return eventsList.size();// request database
	}

	/**
	 * ViewHolder class for holding event views.
	 */
	public static class EventViewHolder extends RecyclerView.ViewHolder {
		TextView textEventName, textLocation;
		TextView dateDay;
		TextView dateMonth;
		TextView dateYear;


		/**
		 * Constructor for the EventViewHolder.
		 *
		 * @param itemView The view for each event item.
		 */
		public EventViewHolder(View itemView) {
			super(itemView);
			// Find views by ID
			textEventName = itemView.findViewById(R.id.text_event_name);
			textLocation = itemView.findViewById(R.id.text_location);
			dateDay = itemView.findViewById(R.id.date_day);
			dateMonth = itemView.findViewById(R.id.date_month);
			dateYear = itemView.findViewById(R.id.date_year);
		}

		/**
		 * Binds event data to the views and sets click listener.
		 *
		 * @param event    The event object to bind.
		 * @param listener The listener for item click events.
		 */
		public void bind(Event event, OnItemClickListener listener) {
			// Bind event data to the views
			itemView.setOnClickListener(v -> listener.onItemClick(event));
		}
	}
}
