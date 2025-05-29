package com.example.droiddesign.model;

/**
 * Represents a message sent by an organizer within the context of an event.
 * This class captures the title, message, and its posting date.
 */
public class OrganizerMessage {
	private String title; // Title of the message
	private String message; // Text content of the message
	private String date; // Date when the message was posted

	/**
	 * Constructs an OrganizerMessage with specified title, message, and date.
	 *
	 * @param title   The title of the message.
	 * @param message The message content.
	 * @param date    The date when the message was posted.
	 */
	public OrganizerMessage(String title, String message, String date) {
		this.title = title;
		this.message = message;
		this.date = date;
	}

	/**
	 * No-argument constructor required for Firebase deserialization.
	 */
	public OrganizerMessage() {}

	// Getters and setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
