package com.example.droiddesign.view.Everybody;

import static com.example.droiddesign.view.Everybody.FirebaseServiceUtils.getFirebaseAuth;
import static com.example.droiddesign.view.Everybody.FirebaseServiceUtils.getFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.Adapters.EventsAdapter;
import com.example.droiddesign.view.Organizer.AddEventActivity;
import com.example.droiddesign.view.Admin.AdminBrowseUsersActivity;
import com.example.droiddesign.view.Admin.BrowseImagesActivity;
import com.example.droiddesign.view.AttendeeAndOrganizer.AppSettingsActivity;
import com.example.droiddesign.view.AttendeeAndOrganizer.DiscoverEventsActivity;
import com.example.droiddesign.view.AttendeeAndOrganizer.ProfileSettingsActivity;
import com.example.droiddesign.view.AttendeeAndOrganizer.QrCodeScanActivity;
import com.example.droiddesign.view.Organizer.SignedEventsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity representing the main menu for the event application.
 * It provides the user with a list of events they have signed up for and allows navigation to other features.
 */
public class EventMenuActivity extends AppCompatActivity {
	/**
	 * RecyclerView for displaying the list of events.
	 */
	private RecyclerView eventsRecyclerView;

	/**
	 * Adapter for the events RecyclerView.
	 */
	private EventsAdapter eventsAdapter;

	/**
	 * List holding the events to be displayed.
	 */
	private List<Event> eventsList;

	/**
	 * Navigation menu for accessing different sections of the app.
	 */
	private NavigationView navigationMenu;

	/**
	 * Firebase Firestore instance for database interaction.
	 */
	private FirebaseFirestore firestore;
	private FirebaseAuth firebaseAuth;

	/**
	 * User ID and role for personalizing the user experience.
	 */
	private String userId, userRole, userEmail;
	public SharedPreferenceHelper prefsHelper;

	/**
	 * List of events the user has signed up for.
	 */
	private List<Event> eventsToDisplay;

	/**
	 * Initializes the activity, setting up UI components and event listeners.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
	 *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_menu);

		// Retrieve Firestore and FirebaseAuth instances
		FirebaseServiceUtils.initialize(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
		firestore = getFirestore();
		firebaseAuth = getFirebaseAuth();

		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		navigationMenu = findViewById(R.id.navigation_menu);
		ImageButton menuButton = findViewById(R.id.button_menu);
		FloatingActionButton fabQuickScan = findViewById(R.id.fab_quick_scan);
		FloatingActionButton addEventButton = findViewById(R.id.fab_add_event);
		TextView textViewEvents = findViewById(R.id.text_upcoming_events);
		CardView adminCard = findViewById(R.id.admin_card);
		adminCard.setVisibility(View.GONE);

		updateTokenIfNeeded();

		prefsHelper = new SharedPreferenceHelper(this);
		String savedUserId = prefsHelper.getUserId();
		if (savedUserId != null) {
			// Use the userId from SharedPreferences
			userId = savedUserId;
			userRole = prefsHelper.getRole();
		} else {
			// No userId found in SharedPreferences, fetch it from FirebaseAuth
			String currentUserId = getFirebaseAuth().getUid();
			if (currentUserId != null) {
				userId = currentUserId;
				prefsHelper.saveUserProfile(userId, userRole, userEmail);
				fetchUserRole();
			} else {
				Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(EventMenuActivity.this, BasicLoginFragment.class);
				startActivity(intent);
			}
		}

		if ("Organizer".equalsIgnoreCase(userRole)) {
			textViewEvents.setText(R.string.created_events);
		} else if ("Attendee".equalsIgnoreCase(userRole)) {
			textViewEvents.setText(R.string.upcoming_events);
			addEventButton.setVisibility(View.INVISIBLE);
		} else {
			textViewEvents.setText(R.string.administration);
			adminCard.setVisibility(View.VISIBLE);
			addEventButton.setVisibility(View.INVISIBLE);
			fabQuickScan.setVisibility(View.INVISIBLE);
		}


		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		eventsAdapter = new EventsAdapter(eventsList, event -> {
			Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId());
			toggleNavigationMenu();
			startActivity(intent);
		});
		eventsRecyclerView.setAdapter(eventsAdapter);
		fetchEvents();

		menuButton.setOnClickListener(v -> toggleNavigationMenu());
		setupRecyclerView();

		// Check if the userRole is "attendee"
		if ("attendee".equalsIgnoreCase(userRole)) {

			// Update the layout parameters to position the button at the bottom center
			ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fabQuickScan.getLayoutParams();
			params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
			params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
			params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
			params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.fab_margin_bottom));
			fabQuickScan.setLayoutParams(params);

		}
		addEventButton.setOnClickListener(view -> {
			Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
			startActivity(intent);
		});

		fabQuickScan.setOnClickListener(v -> {
			// Intent to start Quick Scan Activity or any specific logic
			Intent intent = new Intent(EventMenuActivity.this, QrCodeScanActivity.class);
			startActivity(intent);
		});


		navigationMenu.getMenu().clear();

		// Inflate the menu based on user role
		if ("organizer".equalsIgnoreCase(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_navigation_organizer);
		} else if ("admin".equalsIgnoreCase(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_admin_event_menu);
		} else { // Default to attendee if no role or attendee role
			navigationMenu.inflateMenu(R.menu.menu_navigation_attendee);
		}


		// Set the navigation item selection listener
		String finalUserId = userId;
		navigationMenu.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			Intent intent = null;
			if (id == R.id.browse_events) {
				intent = new Intent(this, DiscoverEventsActivity.class);
			} else if (id == R.id.profile) {
				intent = new Intent(this, ProfileSettingsActivity.class);
				intent.putExtra("USER_ID", finalUserId);
			} else if (id == R.id.browse_users){
				intent = new Intent(this, AdminBrowseUsersActivity.class);
			} else if (id == R.id.admin_browse_events) {
				intent = new Intent(this, DiscoverEventsActivity.class);
			} else if (id == R.id.settings) {
				intent = new Intent(this, AppSettingsActivity.class);
			} else if (id == R.id.browse_images) {
				intent = new Intent(this, BrowseImagesActivity.class);
			} else if (id == R.id.log_out) {
				intent = new Intent(this, LaunchScreenActivity.class);
				// Clear stored preferences
				prefsHelper.clearPreferences();
				// Set userId and userRole to null
				userId = null;
				userRole = null;
				toggleNavigationMenu();
				startActivity(intent);
				finish();
			} else if ("organizer".equalsIgnoreCase(userRole) && id == R.id.nav_manage_events) {
				intent = new Intent(this, SignedEventsActivity.class);
			}

			if (intent != null) {
				toggleNavigationMenu();
				startActivity(intent);
			}

			return true;
		});
	}


	/**
	 * Sets up the RecyclerView with its layout manager and adapter.
	 */
	private void setupRecyclerView() {
		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		eventsList = initializeEventsList();

		eventsAdapter = new EventsAdapter(eventsList, event -> {
			Intent intent;
			intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId());
			intent.putExtra("ORIGIN", "EventMenuActivity");
			if ("Organizer".equalsIgnoreCase(userRole)) {
				intent.putExtra("ORIGIN", "EventMenuActivity");
			} else if ("Attendee".equalsIgnoreCase(userRole)) {
				intent.putExtra("ORIGIN", "SignedEventsActivity");
			}
			startActivity(intent);
		});
		eventsRecyclerView.setAdapter(eventsAdapter);
	}

	/**
	 * Initializes the events list.
	 * @return An empty ArrayList of Event objects.
	 */

	private List<Event> initializeEventsList() {
		return new ArrayList<>();
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
	 * Fetches the events the user has signed up for and updates the UI accordingly.
	 */
	private void fetchEvents() {
		firestore.collection("Users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
			User user = documentSnapshot.toObject(User.class);
			if (user != null) {
				List<String> eventIdsToFetch;
				switch (userRole.toLowerCase()) {
					case "organizer":
						eventIdsToFetch = user.getManagedEventsList();
						break;
					case "attendee":
						eventIdsToFetch = user.getSignedEventsList();
						break;
					default:
						Log.w("EventMenuActivity", "Unrecognized user role: " + userRole);
						return;
				}

				if (eventIdsToFetch != null && !eventIdsToFetch.isEmpty()) {
					fetchEventsByIds(eventIdsToFetch);
				} else {
					Log.w("EventMenuActivity", "No events to fetch for user role: " + userRole);
				}
			} else {
				Log.w("EventMenuActivity", "User data could not be fetched.");
			}
		}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching user data", e));
	}


	/**
	 * Fetches details for each event the user has signed up for using their IDs.
	 * @param eventIds List of event IDs the user has signed up for.
	 */

	private void fetchEventsByIds(List<String> eventIds) {
		eventsToDisplay = new ArrayList<>();
		for (String eventId : eventIds) {
			firestore.collection("EventsDB").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
				Event event = documentSnapshot.toObject(Event.class);
				if (event != null) {
					eventsToDisplay.add(event);
					if (eventsToDisplay.size() == eventIds.size()) {
						updateUI();
					}
				}
			}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching event", e));
		}
	}

	/**
	 * Fetches the current user's role from Firestore and configures the UI based on the role.
	 * This method retrieves the role information from the 'Users' collection in Firestore
	 * using the current user's ID. Once the role is fetched, it calls {@link #configureUIBasedOnRole()}
	 * to update the UI elements based on the user's role.
	 */
	public void fetchUserRole() {
		firestore.collection("Users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
				userRole = documentSnapshot.getString("role");
				userEmail = documentSnapshot.getString("email");
				configureUIBasedOnRole();
			} else {
				Log.e("EventMenuActivity", "Role not found for user.");
			}
		}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching user role", e));
	}

	/**
	 * Configures the user interface based on the user's role.
	 * This method checks the user's role and sets the visibility of the add event button accordingly.
	 * If the user is an organizer, the button is made visible and clickable; otherwise, it is hidden.
	 * This method should be called after the user's role has been determined by {@link #fetchUserRole()}.
	 */
	private void configureUIBasedOnRole() {
		FloatingActionButton addEventButton = findViewById(R.id.fab_add_event);
		if ("Organizer".equalsIgnoreCase(userRole)) {
			addEventButton.setVisibility(View.VISIBLE);
			addEventButton.setOnClickListener(view -> {
				Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
				startActivity(intent);
			});
		} else {
			addEventButton.setVisibility(View.GONE);
		}
	}

	/**
	 * Called when the activity has been resumed and is now visible to the user.
	 * This method ensures that the events list is refreshed each time the activity comes into the foreground.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (eventsToDisplay != null && !eventsToDisplay.isEmpty()) {
			eventsToDisplay.clear();
			fetchEvents();
		}
	}

	/**
	 * Updates the UI to display the latest list of events.
	 */

	private void updateUI() {
		eventsAdapter.setEvents(eventsToDisplay);
		eventsAdapter.notifyDataSetChanged();
		Log.d("EventMenuActivity", "Adapter item count: " + eventsAdapter.getItemCount());
	}


	/**
	 * Updates the Firebase Messaging Service token for the current user in the Firestore database if needed.
	 * This method retrieves the latest FCM token and updates it in the Firestore database under the user's document.
	 * Success or failure of the update operation is logged accordingly.
	 */
	void updateTokenIfNeeded(){
		FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
			if(task.isSuccessful()){
				String token = task.getResult();
				firestore.collection("Users").document(userId).update("fcmToken",token)
						.addOnSuccessListener(aVoid -> Log.d("UpdateToken", "Token successfully updated for user: " + userId))
						.addOnFailureListener(e -> Log.e("UpdateToken", "Error updating token", e));
			}
		});
	}

}