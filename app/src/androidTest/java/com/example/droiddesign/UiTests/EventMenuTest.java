package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventMenuTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void EventsTest() {
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
					Thread.sleep(3000); // Sleep for 1 second
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		onView(withId(R.id.button_menu)).perform(click( ));

		// Check if nav_manage_events button is displayed
		onView(withId(R.id.nav_manage_events)).check(matches(isDisplayed()));
		// Check if browse_events button is displayed
		onView(withId(R.id.browse_events)).check(matches(isDisplayed()));
		onView(withId(R.id.browse_events)).perform(click( ));

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