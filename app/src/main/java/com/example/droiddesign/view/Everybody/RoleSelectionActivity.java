package com.example.droiddesign.view.Everybody;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity to allow the user to select a role (Admin, Organizer, Attendee).
 * This class is responsible for handling user interactions with role selection buttons,
 * performing user registration for the selected role, and navigating to the next activity.
 */
public class RoleSelectionActivity extends AppCompatActivity {

	/**
	 * Buttons used for selecting the role of the new or existing user.
	 * Admin image button allows the user to select the 'Admin' role.
	 * Organizer image button allows the user to select the 'Organizer' role.
	 * Attendee image button allows the user to select the 'Attendee' role.
	 */
	private MaterialButton adminImage, organizerImage, attendeeImage;


	/**
	 * Constant used as a key in SharedPreferences to store and retrieve the user's ID.
	 * This ID is used to identify the user across different sessions and activities.
	 */
	SharedPreferenceHelper prefsHelper;

	/**
	 * Called when the activity is starting.
	 * This is where most initialization should go: calling setContentView(int) to inflate
	 * the activity's UI, using findViewById(int) to programmatically interact with widgets in the UI.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
	 *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
	 *                           Note: Otherwise it is null.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role_selection_unregistered);

		// Initialize SharedPreferences
		prefsHelper = new SharedPreferenceHelper(this);
		// Not a new user scenario
		if (!prefsHelper.isFirstTimeUser()) {
			// Returning user scenario
			navigateToEventMenu();
		}
		// Initialize views and set click listeners
		adminImage = findViewById(R.id.admin_button);
		organizerImage = findViewById(R.id.organizer_button);
		attendeeImage = findViewById(R.id.attendee_button);

		setRoleSelectionListeners();
	}

	/**
	 * Sets click listeners for each role selection button.
	 * Defines the actions to be taken when each role button is clicked.
	 */
	private void setRoleSelectionListeners() {
		attendeeImage.setOnClickListener(v -> handleRoleSelection("attendee", new InterfaceAuthCallback( ) {
			@Override
			public void onSuccess() {
				navigateToEventMenu();
				System.out.println("Authentication succeeded.");
			}
			@Override
			public void onFailure() {
				System.out.println("Authentication failed to add user");
			}
		}));
		organizerImage.setOnClickListener(v -> handleRoleSelection("organizer", new InterfaceAuthCallback( ) {
			@Override
			public void onSuccess() {
				navigateToEventMenu();
				System.out.println("Authentication succeeded.");
			}
			@Override
			public void onFailure() {
				System.out.println("Authentication failed to add user");
			}
		}));
		adminImage.setOnClickListener(v -> handleRoleSelection("admin",new InterfaceAuthCallback( ) {
			@Override
			public void onSuccess() {
				navigateToEventMenu();
				System.out.println("Authentication succeeded.");
			}
			@Override
			public void onFailure() {
				System.out.println("Authentication failed to add user");
			}
		}));
	}

	/**
	 * Handles the role selection process.
	 * Depending on the role selected, it performs actions such as creating a new user or navigating to the EventMenuActivity.
	 * @param role The role selected by the user.
	 */
	private void handleRoleSelection(String role , InterfaceAuthCallback authCallBck) {
//		Toast.makeText(RoleSelectionActivity.this, "Quick start!", Toast.LENGTH_SHORT).show();
		// New user scenario
		// Now that the user profile information is saved locally, proceed to save the user to Firestore

		// Add AuthStateListener
		FirebaseServiceUtils.getFirebaseAuth().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					FirebaseUser user = FirebaseServiceUtils.getFirebaseAuth().getCurrentUser();
					User newUser;
					if (user != null) {
						// User is signed
						newUser = new User(user.getUid(), role, false);
						// Save user profile to SharedPreferences
						prefsHelper.saveUserProfile(user.getUid(), role, null);

						UsersDB userdb = new UsersDB(FirebaseServiceUtils.getFirestore());
						userdb.addUser(newUser);
						authCallBck.onSuccess();
//						navigateToEventMenu();
				} else {
					// User is signed out
					Toast.makeText(RoleSelectionActivity.this, "User ID not saved: ", Toast.LENGTH_SHORT).show();
				}
			}
		}


	});
	}

	/**
	 * Navigates to the EventMenuActivity.
	 * Passes the role and user ID as intent extras for use in the next activity.
	 */
	private void navigateToEventMenu() {
		Intent intent = new Intent(RoleSelectionActivity.this, EventMenuActivity.class);
		startActivity(intent);
		finish();
	}
}
