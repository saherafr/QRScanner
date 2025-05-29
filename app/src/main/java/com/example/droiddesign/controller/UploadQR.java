package com.example.droiddesign.controller;

/**
 * The UploadQR class represents a data model for uploading QR codes to the Firebase Firestore database.
 */
public class UploadQR {
    String qrUrl, eventId, type;

    /**
     * Constructs a new UploadQR object with the specified QR code URL, event ID, and type.
     * @param qrUrl The URL of the QR code image.
     * @param eventId The ID of the event associated with the QR code.
     * @param type The type of the QR code.
     */
    public UploadQR(String qrUrl, String eventId, String type) {
        this.qrUrl = qrUrl;
        this.eventId = eventId;
        this.type = type;
    }

    /**
     * Gets the URL of the QR code image.
     * @return The URL of the QR code image.
     */
    public String getQrUrl() {
        return qrUrl;
    }

    /**
     * Sets the URL of the QR code image.
     * @param qrUrl The URL of the QR code image.
     */
    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    /**
     * Gets the ID of the event associated with the QR code.
     * @return The ID of the event associated with the QR code.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event associated with the QR code.
     * @param eventId The ID of the event associated with the QR code.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the type of the QR code.
     * @return The type of the QR code.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the QR code.
     * @param type The type of the QR code.
     */
    public void setType(String type) {
        this.type = type;
    }
}