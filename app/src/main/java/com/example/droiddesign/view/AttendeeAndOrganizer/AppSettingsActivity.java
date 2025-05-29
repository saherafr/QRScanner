package com.example.droiddesign.view.AttendeeAndOrganizer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import com.example.droiddesign.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity is used to display the application settings.
 * The user can enable or disable geolocation and select the notification preference.
 */
public class AppSettingsActivity extends AppCompatActivity {

	/**
	 * Switch for toggling the geolocation setting on or off.
	 */
	private SwitchCompat switchGeolocation;

	/**
	 * Spinner for selecting the user's notification preference.
	 */
	private Spinner spinnerNotificationPreference;

	/**
	 * Instance of FirebaseFirestore used for database operations.
	 */
	private FirebaseFirestore db; // Firestore database reference

	/**
	 * Current user's ID, fetched from the authentication service.
	 */
	private final String currentUserId = getCurrentUserId();

	/**
	 * This method is called when the activity is created.
	 * It initializes the activity layout and loads the user settings from Firestore.
	 * @param savedInstanceState The saved instance state of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		// Initialize Firestore
		db = FirebaseFirestore.getInstance();

		// Setup UI components
		setupBackButton();
		setupSwitchGeolocation();
		setupSpinner();

		// Load user settings
		loadUserSettings();
	}

	/**
	 * This method is called when the activity is resumed.
	 * It sets up the listeners for the UI components.
	 */
	private void setupBackButton() {
		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(v -> finish());
	}

	/**
	 * This method is called when the activity is resumed.
	 * It sets up the listeners for the UI components.
	 */
	private void setupSwitchGeolocation() {
		switchGeolocation = findViewById(R.id.switch_geo_location);
		Drawable thumbDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.thumb_selector);
		Drawable trackDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.track_selector);
		switchGeolocation.setThumbDrawable(thumbDrawable);
		switchGeolocation.setTrackDrawable(trackDrawable);
	}

	/**
	 * This method is called when the activity is resumed.
	 * It sets up the listeners for the UI components.
	 */
	private void setupSpinner() {
		spinnerNotificationPreference = findViewById(R.id.settings_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
				R.layout.custom_spinner_item,
				new String[]{"Selected Events", "None", "All Events"});
		adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
		spinnerNotificationPreference.setAdapter(adapter);
	}

	/**
	 * This method is called when the activity is resumed.
	 * It updates the Firestore database with the new settings.
	 * @param field The field to be updated.
	 * @param value The new value of the field.
	 */
	private void updateFirestore(String field, Object value) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(field, value);
		db.collection("Users").document(currentUserId)
				.update(updates)
				.addOnSuccessListener(aVoid -> Log.d("Firestore", "Successfully updated " + field))
				.addOnFailureListener(e -> Log.e("Firestore", "Error updating " + field, e));
	}

	/**
	 * This method is called when the activity is resumed.
	 * It gets the current user ID from Firebase Authentication.
	 * @return The current user ID.
	 */
	private String getCurrentUserId() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			return user.getUid();
		} else {
			return null; // Handle this case properly in your app
		}
	}

	/**
	 * Fetches and loads the user settings from the Firestore database.
	 * This method retrieves the user's settings, such as geolocation and notification preferences,
	 * from the Firestore database and updates the UI elements accordingly.
	 * Listeners for UI elements are set up after the settings are loaded to prevent premature triggering.
	 */
	private void loadUserSettings() {
		db.collection("Users").document(currentUserId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocation");
						String notificationPreference = documentSnapshot.getString("notificationPreference");

						if (geolocationEnabled != null) {
							switchGeolocation.setChecked(geolocationEnabled);
						}

						if (notificationPreference != null) {
							int position = ((ArrayAdapter<String>) spinnerNotificationPreference.getAdapter())
									.getPosition(notificationPreference);
							spinnerNotificationPreference.setSelection(position);
						}

						// Setup the listeners after settings have been loaded to avoid overriding them upon initialization
						setupListeners();
					}
				})
				.addOnFailureListener(e -> Log.e("AppSettings", "Error loading user settings", e));
	}

	/**
	 * This method is called when the activity is resumed.
	 * It sets up the listeners for the UI components.
	 */
	private void setupListeners() {
		switchGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
			updateFirestore("geolocation", isChecked);
			Toast.makeText(AppSettingsActivity.this,
					"Geolocation is " + (isChecked ? "enabled" : "disabled"),
					Toast.LENGTH_SHORT).show();
		});

		spinnerNotificationPreference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedItem = parent.getItemAtPosition(position).toString();
				updateFirestore("notificationPreference", selectedItem);
				Toast.makeText(AppSettingsActivity.this,
						"Selected notification preference: " + selectedItem,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//do nothing
			}
		});
	}
}
