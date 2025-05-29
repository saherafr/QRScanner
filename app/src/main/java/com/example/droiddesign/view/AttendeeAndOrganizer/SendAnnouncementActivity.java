package com.example.droiddesign.view.AttendeeAndOrganizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.view.Adapters.AnnouncementAdapter;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Activity for sending announcements to attendees of an event.
 * This activity allows organizers to send announcements to attendees of an event.
 */
public class SendAnnouncementActivity extends AppCompatActivity {
	private TextView titleEditText;
	private TextView messageEditText;
	private FirebaseFirestore firestore;
	private AnnouncementAdapter announcementAdapter;
	private final List<Map<String, Object>> announcementList = new ArrayList<>();
	private String userId, userRole,eventId;

	/**
	 * Method called when the activity is created.
	 * @param savedInstanceState The saved instance state of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_announcement);
		SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
		String savedUserId = prefsHelper.getUserId();
		if (savedUserId != null) {
			// Use the userId from SharedPreferences
			userId = savedUserId;
			userRole = prefsHelper.getRole();
		} //At this point, user details are valid

		// Inflate the button based on user role
		findViewById(R.id.send_button).setVisibility("organizer".equalsIgnoreCase(userRole) ? View.VISIBLE : View.GONE );

		eventId = getIntent().getStringExtra("EVENT_ID");
		if (eventId == null || eventId.isEmpty()) {
			Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		firestore = FirebaseFirestore.getInstance();
		firestore.collection("EventsDB").document(eventId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					String organizerOwnerId = documentSnapshot.getString("organizerOwnerId");
					findViewById(R.id.only_owner).setVisibility(organizerOwnerId != null && organizerOwnerId.equals(userId) ? View.VISIBLE : View.GONE);
				})
				.addOnFailureListener(e -> Log.e("VisibilityCheck", "Error", e));
		titleEditText = findViewById(R.id.title_edit_text);
		messageEditText = findViewById(R.id.message_edit_text);
		Button sendButton = findViewById(R.id.send_button);

		sendButton.setOnClickListener(v -> {
			String title = titleEditText.getText().toString().trim();
			String message = messageEditText.getText().toString().trim();

			if (title.isEmpty() || message.isEmpty()) {
				Toast.makeText(this, "Please write a message.", Toast.LENGTH_SHORT).show();
			} else {
				saveMessage(title, message);
			}
		});
		RecyclerView announcementsRecyclerView = findViewById(R.id.organizer_message_recyclerview);
		announcementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		announcementAdapter = new AnnouncementAdapter(announcementList);
		announcementsRecyclerView.setAdapter(announcementAdapter);

		fetchAnnouncements();

		ImageButton backButton = findViewById(R.id.back_button);
		backButton.setOnClickListener(v -> {
			finish();
		});
	}

	/**
	 * Method to save the message to the Firestore database.
	 * @param title The title of the message.
	 * @param message The content of the message.
	 */
	private void saveMessage(String title, String message) {
		if (eventId == null || eventId.trim().isEmpty()) {
			Toast.makeText(this, "Event ID is not set.", Toast.LENGTH_SHORT).show();
			return;
		}

		// Create a new message map to be saved
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("title", title);
		messageMap.put("message", message);
		messageMap.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

		// Save the message under OrganizerMessages of the specific event
		firestore.collection("EventsDB").document(eventId)
				.update("organizerMessages", FieldValue.arrayUnion(messageMap))
				.addOnSuccessListener(documentReference -> {
					// Clear the input fields after successful save
					titleEditText.setText("");
					messageEditText.setText("");
					// After sending the message, refresh the activity to show updated data
					refreshActivity();
					notifyAttendees(title);
				})
				.addOnFailureListener(e -> Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show());
	}

	/**
	 * Method to fetch announcements from the Firestore database.
	 */
	@SuppressLint("NotifyDataSetChanged")
	private void fetchAnnouncements() {
		firestore.collection("EventsDB").document(eventId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists() && documentSnapshot.contains("organizerMessages")) {
						List<Map<String, Object>> unsortedMessages = (List<Map<String, Object>>) documentSnapshot.get("organizerMessages");
						if (unsortedMessages != null) {
							// Sort messages by date in descending order
							List<Map<String, Object>> sortedMessages = new ArrayList<>(unsortedMessages);
							sortedMessages.sort((map1, map2) -> ((String) map2.get("date")).compareTo((String) map1.get("date")));

							// Now use sortedMessages to update RecyclerView
							announcementList.clear();
							announcementList.addAll(sortedMessages);
							announcementAdapter.notifyDataSetChanged();
						}
					}
				})
				.addOnFailureListener(e -> Log.e("FetchAnnouncementsError", "Error loading announcements", e));
	}

	/**
	 * Method to refresh the activity.
	 */
	private void refreshActivity() {
		Intent intent = new Intent(this, SendAnnouncementActivity.class);
		intent.putExtra("EVENT_ID", eventId); // Pass the event ID back to the activity
		finish();
		startActivity(intent);
	}

	/**
	 * Method to notify attendees of the event about the announcement.
	 * @param title The title of the announcement.
	 */
	private void notifyAttendees(String title) {
		firestore.collection("EventsDB").document(eventId).get()
				.addOnSuccessListener(documentSnapshot -> {
					List<String> attendeeList = (List<String>) documentSnapshot.get("attendeeList");
					if (attendeeList != null) {
						// Prepare the list of tokens for the attendees
						List<String> tokens = new ArrayList<>();
						for (String userId : attendeeList) {
							// Fetch each user's token and add it to the tokens list
							firestore.collection("Users").document(userId).get()
									.addOnSuccessListener(userSnapshot -> {
										String token = userSnapshot.getString("fcmToken");
										if (token != null && !token.isEmpty()) {
											tokens.add(token);
											if (tokens.size() == attendeeList.size()) {
												// All tokens are collected, send them to your server/cloud function to dispatch notifications
												sendNotificationsToTokens(title, tokens, Objects.requireNonNull(documentSnapshot.get("eventName")).toString());
											}
										}
									});
						}
					}
				})
				.addOnFailureListener(e -> Log.e("NotifyAttendees", "Failed to get attendee list for event: " + eventId));
	}

	/**
	 * Method to send notifications to the attendees of the event.
	 * @param title The title of the announcement.
	 * @param tokens The list of FCM tokens of the attendees.
	 * @param eventName The name of the event.
	 */
	private void sendNotificationsToTokens(String title, List<String> tokens, String eventName) {
		// Prepare the payload
		JSONObject payload = new JSONObject();
		try {
			JSONObject notification = new JSONObject();
			notification.put("title", "New Announcement from "+eventName);
			notification.put("body", title);

			// Adding custom data including event ID
			JSONObject data = new JSONObject();
			data.put("eventId", eventId);

			payload.put("registration_ids", new JSONArray(tokens));
			payload.put("notification", notification);

			// Define the MediaType for the request body
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			RequestBody requestBody = RequestBody.create(JSON, payload.toString());

			// Build the request
			Request request = new Request.Builder()
					.url("https://fcm.googleapis.com/fcm/send")
					.addHeader("Authorization", "key=AAAA1Lwwer4:APA91bHuSelA6Mkvst7R_BZ7Vf2ot9gafIXbpW0e3NyVLAIN60xpGuRRc_QjM0jPYyIT0J4PxBejgGQOo5NuRLfOZn_M9C4m6Pl9uv_CTwKgKRimllR_00ZsVtTHghZ86yAxGuXGUGiE")
					.post(requestBody)
					.build();

			// Create a new OkHttpClient instance
			OkHttpClient client = new OkHttpClient();

			// Asynchronously send the request
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
					Log.e("sendNotificationsToTokens", "Failed to send notifications", e);
				}
				@Override
				public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
					if (!response.isSuccessful()) {
						Log.e("sendNotificationsToTokens", "Failed to send notifications: " + response);
					} else {
						Log.d("sendNotificationsToTokens", "Notifications sent successfully");
					}
				}

			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * fetchEventName method to fetch the name of the event from Firestore.
	 * @param firestore
	 * @param eventId
	 * @param callback
	 */
	public void fetchEventName(FirebaseFirestore firestore, String eventId, EventNameCallback callback) {
		firestore.collection("EventsDB").document(eventId).get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				String eventName = task.getResult().getString("name"); // Assuming the field name for event name is "name"
				callback.onEventName(eventName);
			} else {
				Log.e("fetchEventName", "Failed to fetch event name");
				callback.onEventName(null);
			}
		});
	}

	/**
	 * EventNameCallback interface to handle the event name.
	 */
	public interface EventNameCallback {
		/**
		 * Method to handle the event name.
		 * @param eventName The name of the event.
		 */
		void onEventName(String eventName);
	}
}