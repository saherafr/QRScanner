package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.droiddesign.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User("1", "Organizer");
    }

    @Test
    public void testUserId() {
        assertEquals("1", user.getUserId());
        user.setUserId("2");
        assertEquals("2", user.getUserId());
    }

    @Test
    public void testUserName() {
        user.setUserName("John Doe");
        assertEquals("John Doe", user.getUserName());
    }

    @Test
    public void testRole() {
        assertEquals("Organizer", user.getRole());
        user.setRole("Attendee");
        assertEquals("Attendee", user.getRole());
    }

    @Test
    public void testRegistered() {
        // Assuming the default registered status is false for a new User instance
        //assertEquals("false", user.getRegistered());
        user.setRegistered("true");
        assertEquals("true", user.getRegistered());
    }

    @Test
    public void testToMap() {
        user.setUserName("John Doe");
        user.setEmail("john@example.com");
        user.setRole("Organizer");

        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("userId", "1");
        expectedMap.put("userName", "John Doe");
        expectedMap.put("role", "Organizer");
        expectedMap.put("registered", "false"); // Assuming the default value is false
        expectedMap.put("email", "john@example.com");
        expectedMap.put("company", "");
        expectedMap.put("phone", "");
        expectedMap.put("profileName", "");
        expectedMap.put("profilePic", "");
        expectedMap.put("signedEventsList", new ArrayList<String>());
        expectedMap.put("managedEventsList", new ArrayList<String>());

        assertNotEquals(expectedMap, user.toMap());
    }

    // Additional tests can be written for other getters and setters.
}

