package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventSignUpTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void BrowseEventsTest() {
		if (isButtonEnterDisplayed()) {
			onView(withId(R.id.button_enter)).perform(click( ));
			onView(withId(R.id.skip_account_creation)).perform(click( ));
			// Check if RoleSelectionActivity is displayed by checking if one of its buttons is displayed
			onView(withId(R.id.organizer_button)).check(matches(isDisplayed( )));
			// Perform a click on the "attendee" button
			onView(withId(R.id.organizer_button)).perform(click( ));
			// Wait for the RoleSelectionActivity to be displayed
			while (!isActivityDisplayed( )) {
				// Introduce a delay
				try {
					Thread.sleep(5000); // Sleep for 5 second
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		onView(withId(R.id.button_menu)).perform(click( ));
		// Check if browse_events button is displayed
		onView(withId(R.id.browse_events)).check(matches(isDisplayed()));
		onView(withId(R.id.browse_events)).perform(click( ));
		// wait for the events are displayed
		try {
			Thread.sleep(2000); // Sleep for 3 second to load events
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		// Check if browse_events button is displayed
		Espresso.onView(ViewMatchers.withId(R.id.text_upcoming_events))
				.check(ViewAssertions.matches(ViewMatchers.withText("Discover Events")));
		Espresso.onView(ViewMatchers.withId(R.id.events_recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
		onView(withId(R.id.event_description)).check(matches(isDisplayed()));
		onView(withId(R.id.sign_up_button)).perform(click( ));
		try {
			Thread.sleep(2000); // Sleep for 3 second to load events
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		onView(withId(R.id.back_button)).perform(click( ));
		onView(withId(R.id.button_back)).perform(click( ));
		// Check if nav_manage_events button is displayed
		try {
			Thread.sleep(2000); // Sleep for 3 second to load events
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		onView(withId(R.id.button_menu)).perform(click( ));
		onView(withId(R.id.nav_manage_events)).check(matches(isDisplayed()));
		// Perform a scroll action to ensure the RecyclerView is populated

	}
	private boolean isButtonEnterDisplayed() {
		try {
			onView(withId(R.id.button_enter)).check(matches(isDisplayed()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private boolean isActivityDisplayed() {
		try {
			onView(withId(R.id.activity_event_menu)).check(matches(isDisplayed()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}