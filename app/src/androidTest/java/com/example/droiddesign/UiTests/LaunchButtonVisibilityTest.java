package com.example.droiddesign.UiTests;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LaunchButtonVisibilityTest {

	@Rule
	public ActivityTestRule<LaunchScreenActivity> activityRule = new ActivityTestRule<>(LaunchScreenActivity.class);
	@Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
	// Helper method to launch the fragment
	public void launchFragment() {
		Espresso.onView(withId(R.id.button_enter)).perform(ViewActions.click());
		// You may need to add some waiting logic here if the fragment is added asynchronously
	}

	@Test
	public void testFragmentButtons() {
		launchFragment(); // Make sure the fragment is displayed

		// Check for the "edit_user_name" EditText
		Espresso.onView(withId(R.id.edit_user_name))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

		// Check for the "edit_email" EditText
		Espresso.onView(withId(R.id.edit_email))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

		// Check for the "edit_company" EditText
		Espresso.onView(withId(R.id.edit_company))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

		// Check for the "edit_phone_number" EditText
		Espresso.onView(withId(R.id.edit_phone_number))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

		// Check for the "spinner_role" Spinner
		Espresso.onView(withId(R.id.spinner_role))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

		// Check for the "skip_account_creation" Button
		Espresso.onView(withId(R.id.skip_account_creation))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
				.check(ViewAssertions.matches(ViewMatchers.isEnabled()));

		// Check for the "button_profile_picture" Button
		Espresso.onView(withId(R.id.button_profile_picture))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
				.check(ViewAssertions.matches(ViewMatchers.isEnabled()));

		// Check for the "button_create_account" Button
		Espresso.onView(withId(R.id.button_create_account))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
				.check(ViewAssertions.matches(ViewMatchers.isEnabled()));
	}

}
