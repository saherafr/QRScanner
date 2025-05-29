package com.example.droiddesign.view.Everybody;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.AttendeeAndOrganizer.SendAnnouncementActivity;
import com.example.droiddesign.view.Organizer.CurrentAttendanceActivity;
import com.example.droiddesign.view.Organizer.SignedUpUsersActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class that presents the details of an event.
 * It retrieves the event data from Firestore based on the passed event ID and allows the user to sign up for the event.
 */
public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

	/**
	 * The ID of the event whose details are to be displayed.
	 */
	private String eventId;

	/**
	 * Instance of FirebaseFirestore to interact with Firestore database.
	 */
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();
	/**
	 * Navigation menu for accessing different sections of the app.
	 */
	public NavigationView navigationMenu;


	private String userId, userRole;
	SharedPreferenceHelper prefsHelper;

	private GoogleMap map;

	private boolean isUserSignedUp;

	/**
	 * Initializes the activity, sets the content view, and initiates the process to fetch and display event details.
	 * Sets up the interaction logic for UI elements like back button and sign up button.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);
		String origin = getIntent().getStringExtra("ORIGIN");

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_fragment);
		if (mapFragment != null) {
			mapFragment.getMapAsync(this);
		}

		eventId = getIntent().getStringExtra("EVENT_ID");
		if (eventId == null || eventId.isEmpty()) {
			Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		prefsHelper = new SharedPreferenceHelper(this);
		String savedUserId = prefsHelper.getUserId();
		if (savedUserId != null) {
			// Use the userId from SharedPreferences
			userId = savedUserId;
			userRole = prefsHelper.getRole();
		} //At this point, user details are valid

		Button signUpButton = findViewById(R.id.sign_up_button);
		DocumentReference userRef = db.collection("Users").document(userId);
		userRef.get().addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				User user = documentSnapshot.toObject(User.class);
				if (user != null) {
					boolean isEventManaged = user.getManagedEventsList().contains(eventId);
					if ("SignedEventsActivity".equals(origin) || isEventManaged || "EventMenuActivity".equals(origin) || "admin".equalsIgnoreCase(userRole)) {
						Log.d("EventDetailsActivity", "Origin: " + origin);
						signUpButton.setVisibility(View.GONE);
					} else {
						signUpButton.setVisibility(View.VISIBLE);
					}
				}
			}
		});



		ImageButton menuButton = findViewById(R.id.button_menu);
		menuButton.setOnClickListener(v -> toggleNavigationMenu());
		navigationMenu = findViewById(R.id.navigation_menu);
		navigationMenu.getMenu().clear();



		if ("DiscoverEventsActivity".equals(origin) && !"admin".equalsIgnoreCase(userRole)) {
			menuButton.setVisibility(View.GONE);
		}



		// Inflate the menu based on user role
		if ("organizer".equalsIgnoreCase(userRole)) {

			if ("SignedEventsActivity".equals(origin)) {
				navigationMenu.inflateMenu(R.menu.menu_attendee_event_details);
			} else {
				navigationMenu.inflateMenu(R.menu.menu_event_details);
			}


			// Check if eventId is in user.manageEventList
			userRef.get().addOnSuccessListener(documentSnapshot -> {
				if (documentSnapshot.exists()) {
					User user = documentSnapshot.toObject(User.class);
					if (user != null) {
						boolean isEventManaged = user.getManagedEventsList().contains(eventId);
						findViewById(R.id.sign_up_button).setVisibility("SignedEventsActivity".equals(origin) ? View.GONE : isEventManaged ? View.GONE : View.VISIBLE);
					}
				}
			});

		} else if ("admin".equalsIgnoreCase(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_admin_event_details);
			findViewById(R.id.sign_up_button).setVisibility(View.GONE);

		} else { // Default to attendee if no role or attendee role
			navigationMenu.inflateMenu(R.menu.menu_attendee_event_details);
			findViewById(R.id.sign_up_button).setVisibility("SignedEventsActivity".equals(origin) ? View.GONE : View.VISIBLE);
		}

		DocumentReference eventRef = db.collection("EventsDB").document(eventId);

		Event.loadFromFirestore(eventId, event -> {
			if (event != null) {
				populateEventDetails(event);
			} else {
				Toast.makeText(EventDetailsActivity.this, "Unable to retrieve event details.", Toast.LENGTH_LONG).show();
			}
		});

		ImageButton backButton = findViewById(R.id.back_button);
		backButton.setOnClickListener(v -> {
			if ("AddEventSecondActivity".equals(origin) || "QrCodeScanActivity".equals(origin) ) {
				Intent intent = new Intent(EventDetailsActivity.this, EventMenuActivity.class);
				startActivity(intent);
			} else {
				finish();
			}
		});

		signUpButton.setOnClickListener(v -> {
			if (!isUserSignedUp) {
				signUpForEvent();
			} else {
				Toast.makeText(EventDetailsActivity.this, "Already signed up for this event.", Toast.LENGTH_SHORT).show();
			}
		});

		// Set the navigation item selection listener
		navigationMenu.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			Intent intent = null;

			if (id == R.id.current_attendance_menu) {
				intent = new Intent(this, CurrentAttendanceActivity.class);
				intent.putExtra("EVENT_ID", eventId);
			} else if (id == R.id.announcement_menu) {
				intent = new Intent(this, SendAnnouncementActivity.class);
				intent.putExtra("EVENT_ID", eventId);
			}else if (id == R.id.sign_ups_menu) {
				intent = new Intent(this, SignedUpUsersActivity.class);
				intent.putExtra("EVENT_ID", eventId);
			} else if (id == R.id.share_qr_menu) {
				// Retrieve the QR code URI from the event
				Event.loadFromFirestore(eventId, event -> {
					if (event != null) {
						String shareQrUri = event.getShareQrCode();
						if (shareQrUri != null && !shareQrUri.isEmpty()) {
							// Create an Intent to share the image
							Intent shareIntent = new Intent(Intent.ACTION_SEND);
							shareIntent.setType("image/png");
							shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(shareQrUri));

							// Create a chooser intent
							Intent chooserIntent = Intent.createChooser(shareIntent, "Share QR code");

							// Start the activity for result
							startActivity(chooserIntent);
						} else {
							Toast.makeText(EventDetailsActivity.this, "QR code not available for this event.", Toast.LENGTH_SHORT).show();
						}
						toggleNavigationMenu();
					}
				});
			} else if (id == R.id.remove_event_poster){
				eventRef.update("imagePosterId", null)
						.addOnSuccessListener(aVoid -> {
							Toast.makeText(this, "Event poster removed successfully.", Toast.LENGTH_SHORT).show();
							recreate();
						})
						.addOnFailureListener(e -> {
							Toast.makeText(this, "Failed to remove event poster.", Toast.LENGTH_SHORT).show();
						});

			} else if (id == R.id.remove_event){
				eventRef.delete()
						.addOnSuccessListener(aVoid -> {
							Toast.makeText(this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
							finish(); // Close the activity or navigate as needed
						})
						.addOnFailureListener(e -> {
							Toast.makeText(this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
						});
				finish();
			}

			if (intent != null) {
				startActivity(intent);
				toggleNavigationMenu();
			}

			return true;
		});
	}

	/**
	 * Toggles the visibility of the navigation menu.
	 */
	private void toggleNavigationMenu() {
		if (navigationMenu.getVisibility() == View.VISIBLE) {
			navigationMenu.setVisibility(View.GONE);
		} else {
			navigationMenu.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Populates the event details in the activity's UI components.
	 * @param event Event object containing the details to be displayed.
	 */
	private void populateEventDetails(Event event) {
		TextView eventName = findViewById(R.id.event_name);
		TextView eventDateAndTime = findViewById(R.id.date_and_time);
		TextView eventDescription = findViewById(R.id.event_description);
		ImageView eventPoster = findViewById(R.id.image_event_poster);

		eventName.setText(event.getEventName());
		String dateTime = event.getEventDate() + " " + event.getStartTime();
		eventDateAndTime.setText(dateTime);
		eventDescription.setText(event.getDescription());

		Glide.with(this)
				.load(event.getImagePosterId())
				.placeholder(R.drawable.image_placeholder)
				.into(eventPoster);
	}

	/**
	 * Signs up the current logged-in user for the event and updates the user's event list in the Firestore database.
	 * Shows a toast message based on the success or failure of the operation.
	 */
	private void signUpForEvent() {

		isUserSignedUp = true;
		String currentUserId = getCurrentUserId(); // Ensure this method gets the current user ID
		if (currentUserId == null || currentUserId.isEmpty()) {
			Toast.makeText(this, "User not logged in.", Toast.LENGTH_LONG).show();
			return;
		}

		// First, check the event details to see if signing up is possible
		db.collection("EventsDB").document(eventId)
				.get()
				.addOnSuccessListener(eventDocument -> {
					if (eventDocument.exists()) {
						Event event = eventDocument.toObject(Event.class);
						if (event != null) {
							List<String> attendeeList = event.getAttendeeList() != null ? event.getAttendeeList() : new ArrayList<>();


							if (event.getSignupLimit() == null || event.getSignupLimit() <= 0 || attendeeList.size() < event.getSignupLimit()) {

								// Add the current user to the attendee list and update the event
								attendeeList.add(currentUserId);
								db.collection("EventsDB").document(eventId)
										.update("attendeeList", attendeeList)
										.addOnSuccessListener(aVoid -> {

											// Fetch and update the user's signed events list
											db.collection("Users").document(currentUserId)
													.get()
													.addOnSuccessListener(userDocument -> {
														User user = userDocument.toObject(User.class);
														if (user != null) {
															List<String> signedEvents = user.getSignedEventsList() != null ? user.getSignedEventsList() : new ArrayList<>();
															signedEvents.add(eventId);
															db.collection("Users").document(currentUserId)
																	.update("signedEventsList", signedEvents)
																	.addOnSuccessListener(aVoidUser -> Toast.makeText(EventDetailsActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show())
																	.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to update user's signed events.", Toast.LENGTH_SHORT).show());
														}
													})
													.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show());
										})
										.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to update event attendees.", Toast.LENGTH_SHORT).show());
							} else {
								Toast.makeText(this, "Signup limit reached.", Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(this, "Event data is invalid.", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(this, "Event does not exist.", Toast.LENGTH_LONG).show();
					}
				})
				.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to fetch event data.", Toast.LENGTH_SHORT).show());
	}

	/**
	 * Retrieves the ID of the currently logged-in user from FirebaseAuth.
	 * @return The current user's ID or null if no user is logged in.
	 */
	private String getCurrentUserId() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		return (user != null) ? user.getUid() : null;
	}

	/**
	 * Called when the map is ready to be used.
	 * This callback is triggered when the map has been completely loaded and is ready to display.
	 * It sets up the map's UI settings and calls the method to display the event's location on the map.
	 *
	 * @param googleMap The GoogleMap instance ready to be used.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d("EventDetails", "Map is ready");
		this.map = googleMap;
		// Configure the map as needed
		map.getUiSettings().setAllGesturesEnabled(true); // Disable interaction for the preview
		Log.d("EventDetailsActivity", "Fetching event location for ID: " + eventId);

		displayEventLocationOnMap(); // Function to display the check-in locations
	}

	/**
	 * Fetches the event's location from the Firestore database and displays it on the map.
	 * This method retrieves the latitude and longitude of the event from the Firestore database
	 * and places a marker on the map at the event's location. It then moves the camera to center on the event's location.
	 */
	private void displayEventLocationOnMap() {
		Log.d("EventDetailsActivity", "Fetching event location for ID: " + eventId);

		db.collection("EventsDB").document(eventId)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && task.getResult() != null) {
						DocumentSnapshot document = task.getResult();
						if (document.exists()) {
							Log.d("EventDetailsActivity", "Document found in EventsDB");

							Event event = document.toObject(Event.class);
							if (event != null) {
								double latitude = event.getEventLatitude();
								double longitude = event.getEventLongitude();

								Log.d("EventDetailsActivity", "Event latitude: " + latitude);
								Log.d("EventDetailsActivity", "Event longitude: " + longitude);

								LatLng eventLocation = new LatLng(latitude, longitude);
								map.addMarker(new MarkerOptions()
										.position(eventLocation)
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

								map.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15)); // Adjust the zoom level as needed
								Log.d("EventDetailsActivity", "Map updated with event location");
							} else {
								Log.d("EventDetailsActivity", "Event data is null");
							}
						} else {
							Log.d("EventDetailsActivity", "No such document in EventsDB for ID: " + eventId);
						}
					} else {
						Log.e("EventDetailsActivity", "Failed to fetch document: ", task.getException());
					}
				});
	}

}