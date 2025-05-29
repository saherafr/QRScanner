package com.example.droiddesign.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for managing user profile information using SharedPreferences.
 */
public class SharedPreferenceHelper {
	private static final String PREF_NAME = "UserPrefs";
	private static final String KEY_USER_ID = "userId";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_ROLE = "role";
	public SharedPreferences sharedPreferences;

	/**
	 * Constructs a new SharedPreferencesHelper instance.
	 * @param context The application context.
	 */
	public SharedPreferenceHelper(Context context) {
		sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Saves the user profile information (userId and role) to SharedPreferences.
	 * @param userId The user ID to be saved.
	 * @param role The role to be saved.
	 * @param email The email to be saved if logged in.
	 */
	public void saveUserProfile(String userId, String role, String email) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_USER_ID, userId);
		editor.putString(KEY_EMAIL, email); // Add email to SharedPreferences if needed
		editor.putString(KEY_ROLE, role);
		editor.apply();
	}
	/**
	 * Saves the user profile information (userId and role) only to SharedPreferences.
	 * @param userId The user ID to be saved.
	 * @param role The role to be saved.
	 */
	public void saveUserProfile(String userId, String role) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_USER_ID, userId);
		editor.putString(KEY_ROLE, role);
		editor.apply();
	}


	/**
	 * Retrieves the user ID from SharedPreferences.
	 * @return The user ID if found, otherwise null.
	 */
	public String getUserId() {
		return sharedPreferences.getString(KEY_USER_ID, null);
	}

	/**
	 * Retrieves the role from SharedPreferences.
	 * @return The role if found, otherwise null.
	 */
	public String getRole() {
		return sharedPreferences.getString(KEY_ROLE, null);
	}


	/**
	 * Checks if the app is being used for the first time by the user.
	 * Determines whether user data already exists in SharedPreferences.
	 * @return true if it is the first time the user is using the app, false otherwise.
	 */
	public boolean isFirstTimeUser() {
		return getUserId() == null;
	}

	/** Method to clear all stored preferences
	 */
	public void clearPreferences() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
	}
}
