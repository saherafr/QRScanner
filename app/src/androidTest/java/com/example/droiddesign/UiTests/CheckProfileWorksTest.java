package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
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
public class CheckProfileWorksTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void BrowseEventsDisplayTest() {
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
//		onView(withId(R.id.profile_settings)).check(matches(isDisplayed()));
		onView(withId(R.id.profile)).perform(click( ));
		onView(withId(R.id.edit_profile_button)).perform(click( ));


		// Check if the events are displayed
		onView(withId(R.id.profile_image_view)).check(matches(isDisplayed()));
		Espresso.onView(ViewMatchers.withId(R.id.editUsername))
				.perform(ViewActions.typeText("John Doe"));

		Espresso.onView(ViewMatchers.withId(R.id.editUserCompany))
				.perform(ViewActions.typeText("Example Company"));

		Espresso.onView(ViewMatchers.withId(R.id.editUserEmail))
				.perform(ViewActions.typeText("johndoe@example.com"));

		Espresso.onView(ViewMatchers.withId(R.id.editUserContactNumber))
				.perform(ViewActions.typeText("1234567890"));

		// Click the "Save" button
		Espresso.onView(withId(R.id.buttonSave))
				.perform(ViewActions.click());



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