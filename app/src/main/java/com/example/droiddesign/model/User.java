package com.example.droiddesign.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a generic user in the system with common attributes like user ID and role.
 * This class is abstract and intended to be subclassed by specific types of users.
 */
public class User {
	/**
	 * The unique id of the user.
	 */
	private String userId;
	/**
	 * The name of of the user if signed in.
	 */
	private String userName;
	/**
	 * The role user takes on.
	 */
	private String role;
	/**
	 * The registered status of user.
	 */
	private String registered;
	/**
	 * The profile name of the user.
	 */
	private String profileName;

	/**
	 * The email address of the user.
	 */
	private String email;
	/**
	 * The company address of the user.
	 */
	private String company;

	/**
	 * The phone number of the user.
	 */
	private String phone;
	private String token;

	/**
	 * The URL or path to the profile picture of the user.
	 */
	private String profilePic;

	/**
	 * A flag indicating whether geolocation features are enabled for the user.
	 */
	private boolean geolocation;

	/**
	 * A list of event IDs that this user is associated with.
	 */
	private ArrayList<String> signedEventsList;
	/**
	 * A list of event IDs that this user manage.
	 */
	private ArrayList<String> managedEventsList;

	private String notificationPreference;

	/**
	 * Constructs an empty User instance with default values.
	 */
	public User() {
		this.userId = null;
		this.role = null;
		this.userName = "";
		this.profileName = "";
		this.email = "";
		this.company = "";
		this.phone = "";
		this.token = "";
		this.profilePic = "";
		this.geolocation = false;
		this.signedEventsList = new ArrayList<>();
		this.managedEventsList = new ArrayList<>();
		this.notificationPreference = "";
	}

	/**
	 * Constructs a User instance with the specified user ID, role, and registration status,
	 * initializing user-specific fields to default values.
	 *
	 * @param userId    The unique identifier for the user.
	 * @param role      The role of the user within the system.
	 * @param registered The registration status of the user.
	 */
	public User(String userId, String role, boolean registered) {
		this.userId = userId;
		this.role = role;
		this.registered = String.valueOf(registered);
		this.profileName = "";
		this.email = "";
		this.company = "";
		this.phone = "";
		this.token = "";
		this.profilePic = "";
		this.geolocation = false;
		this.signedEventsList = new ArrayList<>();
		this.managedEventsList = new ArrayList<>();
		this.notificationPreference = "";
	}

	/**
	 * Constructs an User instance with the specified user ID and role, initializing user-specific fields to default values.
	 *
	 * @param userId The unique identifier for the user.
	 * @param role   The role of the user within the system.
	 */
	public User(String userId, String role) {
		this.userId = userId;
		this.role = role;
		this.userName = "";
		this.profileName = "";
		this.email = "";
		this.company = "";
		this.phone = "";
		this.token = "";
		this.profilePic = "";
		this.geolocation = false;
		this.signedEventsList = new ArrayList<>();
		this.managedEventsList = new ArrayList<>();
		this.notificationPreference = "";
	}

	/**
	 * Converts the user's data to a map structure for easy storage and retrieval.
	 *
	 * @return A map of user attributes to their respective values.
	 */
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("userName", userName);
		map.put("role", role);
		map.put("registered", registered);
		map.put("email", email);
		map.put("company", company);
		map.put("phone", phone);
		map.put("fcmToken", token);
		map.put("profileName", profileName);
		map.put("profilePic", profilePic);
		map.put("signedEventsList", signedEventsList);
		map.put("managedEventsList", managedEventsList);
		map.put("notificationPreference", notificationPreference);
		map.put("geolocation", geolocation);
		return map;
	}

	/**
	 * Gets the user ID.
	 *
	 * @return The user ID.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user ID.
	 *
	 * @param userId The user ID to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * Gets the name of the user.
	 *
	 * @return The name of the user.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * Sets the user name on creation.
	 *
	 * @param userName The user name to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the role of the user.
	 *
	 * @return The role of the user.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of the user.
	 *
	 * @param role The role to set for the user.
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Gets the registered status of the user.
	 *
	 * @return The registered status of the user.
	 */
	public String getRegistered() {
		return registered;
	}

	/**
	 * Sets the registered status of the user.
	 *
	 * @param registered The new registered status to be set.
	 */
	public void setRegistered(String registered) {
		this.registered = registered;
	}

	/**
	 * Sets the profile name of the user.
	 *
	 * @param profileName The new profile name to be set.
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * Gets the profile name of the user.
	 *
	 * @return The profile name of the user.
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * Gets the company of the user.
	 *
	 * @return The company address of the user.
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * Sets the company address of the user.
	 *
	 * @param company The new email address to be set.
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Sets the email address of the user.
	 *
	 * @param email The new email address to be set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the email address of the user.
	 *
	 * @return The email address of the user.
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * Sets the phone number of the user.
	 *
	 * @param phone The new phone number to be set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the phone number of the user.
	 *
	 * @return The phone number of the user.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Gets the FCM token of the user.
	 *
	 * @return The FCM token of the user.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the FCM token of the user.
	 *
	 * @param token The new FCM token to be set.
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Sets the URL or path to the profile picture of the user.
	 *
	 * @param profilePic The new URL or path to the profile picture.
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	/**
	 * Sets the geolocation availability for the user.
	 *
	 * @param geolocation The new geolocation status to be set.
	 */
	public void setGeolocation(boolean geolocation) {
		this.geolocation = geolocation;
	}

	/**
	 * Checks if geolocation is enabled for the user.
	 *
	 * @return true if geolocation is enabled, false otherwise.
	 */
	public boolean getGeolocation() {
		return geolocation;
	}

	/**
	 * Adds an event to the user's list of events by event ID.
	 *
	 * @param event The event to be added to the user's list.
	 */
	public void addSignedEvent(Event event) {
		signedEventsList.add(event.getEventId());
	}


	/**
	 * Gets the profile picture of the user.
	 *
	 * @return The profile picture of the user.
	 */
	public String getProfilePic() {
		return this.profilePic;
	}

	/**
	 * Gets the list of events the user has signed up for.
	 *
	 * @return The list of events the user has signed up for.
	 */
	public ArrayList<String> getSignedEventsList() {
		return this.signedEventsList;
	}

	/**
	 * Sets the list of events the user has signed up for.
	 *
	 * @param signedEventsList The list of events the user has signed up for.
	 */
	public void setSignedEventsList(ArrayList<String> signedEventsList) {
		this.signedEventsList = signedEventsList;
	}

	/**
	 * Gets the list of events the user manages.
	 *
	 * @return The list of events the user manages.
	 */
	public ArrayList<String> getManagedEventsList() {
		return managedEventsList;
	}

	/**
	 * Sets the list of events the user manages.
	 *
	 * @param managedEventsList The list of events the user manages.
	 */
	public void setManagedEventsList(ArrayList<String> managedEventsList) {
		this.managedEventsList = managedEventsList;
	}

	/**
	 * Gets the notification preference of the user.
	 *
	 * @return The notification preference of the user.
	 */
	public String getNotificationPreference() {
		return notificationPreference;
	}

	/**
	 * Sets the notification preference of the user.
	 *
	 * @param notificationPreference The notification preference of the user.
	 */
	public void setNotificationPreference(String notificationPreference) {
		this.notificationPreference = notificationPreference;
	}
}
