package com.example.droiddesign.model;

/**
 * This class is used to store the image URL and the owner ID of the image.
 * The owner ID is used to identify the owner of the image.
 */
public class ImageItem {
	private String imageUrl;
	private String ownerId; // Attribute to store the owner ID

	/**
	 * Constructor to initialize the image URL and the owner ID.
	 * @param imageUrl The URL of the image.
	 * @param ownerId The ID of the owner of the image.
	 */
	public ImageItem(String imageUrl, String ownerId) {
		this.imageUrl = imageUrl;
		this.ownerId = ownerId;
	}

	/**
	 * Get the URL of the image.
	 * @return The URL of the image.
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * Get the owner ID of the image.
	 * @return The owner ID of the image.
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * Set the URL of the image.
	 * @param imageUrl The URL of the image.
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * Set the owner ID of the image.
	 * @param ownerId The owner ID of the image.
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

}
