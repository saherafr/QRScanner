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
public class BrowseImagesTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void UsersAdminCheckUsersTest() {
		if (isButtonEnterDisplayed()) {
			onView(withId(R.id.button_enter)).perform(click( ));
			onView(withId(R.id.skip_account_creation)).check(matches(isDisplayed( )));
			onView(withId(R.id.skip_account_creation)).perform(click( ));
			// Use the custom wait action
//			onView(isRoot()).perform(EspressoTools.waitId(R.id.admin_button, 1000));
			onView(withId(R.id.admin_button)).check(matches(isDisplayed( )));
			onView(withId(R.id.admin_button)).perform(click( ));
			while (!isActivityDisplayed( )) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		onView(withId(R.id.button_menu)).perform(click( ));
		// Check if browse_users button is displayed
		onView(withId(R.id.browse_images)).check(matches(isDisplayed()));
		onView(withId(R.id.browse_images)).perform(click( ));
		// Check if browse_images button is displayed
		onView(withId(R.id.images_recyclerview)).check(matches(isDisplayed()));

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