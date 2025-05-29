package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
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
public class AttendeeViewNotificationTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void ViewAnnouncementTest() {
		if (isButtonEnterDisplayed()) {
			onView(withId(R.id.button_enter)).perform(click( ));
			onView(withId(R.
					id.skip_account_creation)).perform(click( ));
			onView(withId(R.id.attendee_button)).check(matches(isDisplayed( )));
			onView(withId(R.id.attendee_button)).perform(click( ));
			while (!isActivityDisplayed( )) {
				try {
					Thread.sleep(3000); // Sleep for 1 second
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		onView(withId(R.id.button_menu)).perform(click( ));
		// Check if browse_events button is displayed
		onView(withId(R.id.browse_events)).check(matches(isDisplayed()));
		onView(withId(R.id.browse_events)).perform(click( ));
		// Check if the events are displayed
//		onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));
		// Wait for events RecyclerView to be populated and perform a click on the first item
		onView(withId(R.id.events_recycler_view)).perform(actionOnItemAtPosition(0, click()));
		try {
			Thread.sleep(1000); // Sleep for 1 second
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		onView(withId(R.id.sign_up_button)).perform( click());

		onView(withId(R.id.back_button)).perform(click( ));
		onView(withId(R.id.button_back)).perform(click( ));
		try {
			Thread.sleep(2000); // Sleep for 1 second
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
//		onView(withId(R.id.events_recycler_view))
//				.check(matches(isDisplayed()))
//				.check(matches(hasMinimumChildCount(1))) // Make sure there is at least one item
//				.perform(actionOnItemAtPosition(0, click()));
//		onView(withId(R.id.button_menu)).perform(click( ));
//		onView(withId(R.id.announcement_menu)).perform(click( ));
//		try {
//			Thread.sleep(3000); // Sleep for 3 second
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
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