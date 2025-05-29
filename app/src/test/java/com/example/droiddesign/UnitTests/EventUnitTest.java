package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class EventUnitTest {
	@Mock
	FirebaseFirestore mockdb;
	private Event event;

	@Before
	public void setUp() {
		event = new Event();
	}

	@Test
	public void testGettersAndSetters() {

		String testString = "Test";
		event.setEventName(testString);
		assertEquals(testString, event.getEventName());

		event.setEventLocation(testString);
		assertEquals(testString, event.getEventLocation());

		event.setEventDate(testString);
		assertEquals(testString, event.getEventDate());

		event.setStartTime(testString);
		assertEquals(testString, event.getStartTime());

		event.setEndTime(testString);
		assertEquals(testString, event.getEndTime());

		event.setDate(testString);
		assertEquals(testString, event.getDate());


		event.setOrganizerOwnerId(testString);
		assertEquals(testString, event.getOrganizerOwnerId());

		event.setImagePosterId(testString);
		assertEquals(testString, event.getImagePosterId());

		event.setDescription(testString);
		assertEquals(testString, event.getDescription());

		Integer testInteger = 100;
		event.setSignupLimit(testInteger);
		assertEquals(testInteger, event.getSignupLimit());


		event.setShareQrCode(testString, testString);
		assertEquals(testString, event.getShareQrCode());

	}

	@Test
	public void testUserSignup(){
		List<String> attendeeList = new ArrayList<>();

		User user = new User("1", "Organizer");
		attendeeList.add(user.getUserId());
		event.setAttendeeList(attendeeList);
		assertEquals("1", event.getAttendeeList().get(0));
	}

}