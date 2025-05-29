package com.example.droiddesign.UiTests;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.RoleSelectionActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoleViewActivityTest {

	@Rule
	public ActivityScenarioRule<RoleSelectionActivity> activityRule =
			new ActivityScenarioRule<>(RoleSelectionActivity.class);

	@Test
	public void testAdminButtonIsDisplayed() {
		Espresso.onView(withId(R.id.admin_button))
				.check(ViewAssertions.matches(isDisplayed()));
	}

	@Test
	public void testOrganizerButtonIsDisplayed() {
		Espresso.onView(withId(R.id.organizer_button))
				.check(ViewAssertions.matches(isDisplayed()));
	}

	@Test
	public void testAttendeeButtonIsDisplayed() {
		Espresso.onView(withId(R.id.attendee_button))
				.check(ViewAssertions.matches(isDisplayed()));
	}
}

