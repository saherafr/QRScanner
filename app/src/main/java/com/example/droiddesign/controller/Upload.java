package com.example.droiddesign.controller;

/**
 * Represents an upload entity with a name and image URL.
 */
public class Upload {
    private String mName;
    private String mImageUrl;

    /**
     * Constructs an Upload object with the specified name and image URL.
     * If the name is empty, it defaults to "No Name".
     *
     * @param name     The name of the upload.
     * @param imageUrl The URL of the uploaded image.
     */
    public Upload(String name, String imageUrl) {
        // If the name is empty, set it to "No Name"
        if (name.trim().isEmpty()) {
            this.mName = "No Name";
        } else {
            this.mName = name;
        }
        this.mImageUrl = imageUrl;
    }


    /**
     * Retrieves the name of the upload.
     *
     * @return The name of the upload.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the upload.
     *
     * @param name The name to be set.
     */

    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Retrieves the URL of the uploaded image.
     *
     * @return The URL of the uploaded image.
     */

    public String getImageUrl() {
        return mImageUrl;
    }


    /**
     * Sets the URL of the uploaded image.
     *
     * @param imageUrl The URL of the uploaded image to be set.
     */
    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
