package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;

import com.example.droiddesign.model.OrganizerMessage;

import org.junit.Before;
import org.junit.Test;

public class OrganizerMessageTest {

    private OrganizerMessage organizerMessage;
    private final String testDate = "2024-01-01";
    private final String testText = "Welcome to our event!";
    private final String testTitle = "messageTitle";

    @Before
    public void setUp() {
        organizerMessage = new OrganizerMessage(testTitle, testText, testDate);
    }

    @Test
    public void constructor_initializesPropertiesCorrectly() {
        assertEquals("Constructor should initialize name correctly",testTitle,organizerMessage.getTitle());
        assertEquals("Constructor should initialize date correctly", testDate, organizerMessage.getDate());
        assertEquals("Constructor should initialize text correctly", testText, organizerMessage.getMessage());
    }

    @Test
    public void setDate_updatesDateCorrectly() {
        String newDate = "2024-02-02";
        organizerMessage.setDate(newDate);
        assertEquals("setDate should update date correctly", newDate, organizerMessage.getDate());
    }

    @Test
    public void setText_updatesTextCorrectly() {
        String newText = "Change of plans!";
        organizerMessage.setMessage(newText);
        assertEquals("setText should update text correctly", newText, organizerMessage.getMessage());
    }

}

