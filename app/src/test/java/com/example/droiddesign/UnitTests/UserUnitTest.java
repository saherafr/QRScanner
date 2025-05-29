package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class UserUnitTest {
    private User attendeeUser;
    private User adminUser;
    private User organizerUser;
    private User user;

    @Before
    public void setUp() {
        // Initialize user
        user = new User("userId123", "user");
        user.setProfileName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("1234567890");
        user.setProfilePic("profilePicUrl");

        // Initialize attendee user
        attendeeUser = new User("attendeeId123", "attendee");
        attendeeUser.setProfileName("Attendee Name");
        attendeeUser.setEmail("attendee@example.com");
        attendeeUser.setPhone("1234567890");
        attendeeUser.setProfilePic("attendeeProfilePicUrl");

        // Initialize admin user
        adminUser = new User("adminId123", "admin");
        adminUser.setProfileName("Admin Name");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhone("1234567890");
        adminUser.setProfilePic("adminProfilePicUrl");

        // Initialize organizer user
        organizerUser = new User("organizerId123", "organizer");
        organizerUser.setProfileName("Organizer Name");
        organizerUser.setEmail("organizer@example.com");
        organizerUser.setPhone("1234567890");
        organizerUser.setProfilePic("organizerProfilePicUrl");
    }

    @Test
    public void testUserConstructor() {
        assertNotNull("User object should not be null", attendeeUser);
    }

    @Test
    public void testGetters() {
        assertEquals("Profile name should match", "Attendee Name", attendeeUser.getProfileName());
        assertEquals("Email should match", "attendee@example.com", attendeeUser.getEmail());
        assertEquals("Phone should match", "1234567890", attendeeUser.getPhone());
    }

    @Test
    public void testToMap() {
        HashMap<String, Object> map = user.toMap();
        assertEquals("userId should match", "userId123", map.get("userId"));
        assertEquals("role should match", "user", map.get("role"));
        assertEquals("registered status should match", null, map.get("registered"));
        assertEquals("profileName should match", "John Doe", map.get("profileName"));
        assertEquals("email should match", "john.doe@example.com", map.get("email"));
        assertEquals("phone should match", "1234567890", map.get("phone"));
        assertEquals("profilePic should match", "profilePicUrl", map.get("profilePic"));
        assertTrue("signedEventsList should be an ArrayList", map.get("signedEventsList") instanceof ArrayList);
        assertTrue("managedEventsList should be an ArrayList", map.get("managedEventsList") instanceof ArrayList);
    }

    @Test
    public void testAddEvent() {
        // Mock Event object to add to User's event list
        Event mockEvent = new Event() {
            @Override
            public String getEventId() {
                return "eventId123";
            }
        };

        attendeeUser.addSignedEvent(mockEvent);

        assertTrue("eventsList should contain the added event ID", attendeeUser.getSignedEventsList().contains("eventId123"));
    }

    @Test
    public void testAddAndDeleteUserAttributes() {
        // Test adding and deleting attributes for attendeeUser
        attendeeUser.setRegistered("true");
        assertEquals("Attribute should be set", "true", attendeeUser.getRegistered());

        attendeeUser.setRegistered(null);
        assertEquals("Attribute should be deleted", null, attendeeUser.getRegistered());

        // Test adding and deleting attributes for adminUser
        adminUser.setRegistered("true");
        assertEquals("Attribute should be set", "true", adminUser.getRegistered());

        adminUser.setRegistered(null);
        assertEquals("Attribute should be deleted", null, adminUser.getRegistered());

        // Test adding and deleting attributes for organizerUser
        organizerUser.setRegistered("true");
        assertEquals("Attribute should be set", "true", organizerUser.getRegistered());

        organizerUser.setRegistered(null);
        assertEquals("Attribute should be deleted", null, organizerUser.getRegistered());
    }
}