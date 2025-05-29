package com.example.droiddesign.controller;

/**
 * The MessageEvent class represents an event that is triggered when a message is received.
 */
public class MessageEvent {
    private final String message;

    /**
     * Constructs a new MessageEvent object with the specified message.
     * @param message the message associated with the event
     */
    public MessageEvent(String message) {
        this.message = message;
    }

    /**
     * Returns the message associated with the event.
     * @return the message associated with the event
     */
    public String getMessage() {
        return message;
    }
}
