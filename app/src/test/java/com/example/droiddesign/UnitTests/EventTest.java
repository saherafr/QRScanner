package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.droiddesign.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {


    @Test
    public void testEventConstructor() {
        // Create test data
        String eventId = "123";
        String eventName = "Test Event";
        String eventDate = "2022-01-01";
        String eventLocation = "Test Location";
        String startTime = "10:00 AM";
        String endTime = "12:00 PM";
        double longitude = 0.00;
        double latitude = 0.00;
        String organizerOwnerId = "456";
        String imagePosterId = "789";
        String description = "Test description";
        int signupLimit = 100;
        int attendeesCount = 50;
        List<Integer> milestones;
        String shareQrCode = "shareQrCode";
        String checkInQrCode = "checkInQrCode";
        String shareQrId = "shareQrId";
        String checkInQrId = "checkInQrId";

        // Create the event object using the constructor
        Event event = new Event(eventId,eventName,eventDate,eventLocation,longitude,latitude,startTime,
                endTime,organizerOwnerId, imagePosterId, description, signupLimit,attendeesCount,null,
                shareQrCode,checkInQrCode, shareQrId, checkInQrId);

        // Verify that the event object has been created with the expected values
        assertEquals(eventId, event.getEventId());
        assertEquals(eventName, event.getEventName());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(eventLocation, event.getEventLocation());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals(organizerOwnerId, event.getOrganizerOwnerId());
        assertEquals(imagePosterId, event.getImagePosterId());
        assertEquals(description, event.getDescription());
        assertEquals(shareQrCode, event.getShareQrCode());
    }


    private Event event;

    @Before
    public void setUp() {
        // Assuming Event class has a constructor or method to set Firestore instance
        event = new Event(); // Assuming a constructor without Firestore instance for simplicity

    }

    @Test
    public void constructorTest() {
        assertNotNull("Event instance should not be null", event);
    }

    @Test
    public void settingAndGettingEventName() {
        String expectedName = "Sample Event";
        event.setEventName(expectedName);
        assertEquals("Event name should match the set value", expectedName, event.getEventName());
    }

    @Test
    public void eventUpdatesCorrectly() {
        String newLocation = "New Location";
        event.setEventLocation(newLocation);
        assertEquals("Event location should be updated", newLocation, event.getEventLocation());
    }


}
