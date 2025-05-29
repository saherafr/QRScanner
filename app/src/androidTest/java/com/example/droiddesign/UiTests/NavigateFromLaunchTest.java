package com.example.droiddesign.UiTests;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;

public class NavigateFromLaunchTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Rule
	public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
			android.Manifest.permission.ACCESS_FINE_LOCATION,
			android.Manifest.permission.CAMERA);

	@Test
	public void navigateToRoleSelectionAndSelectAttendee() {
		// Click the skip button on the launch screen
		Espresso.onView(withId(R.id.button_enter)).perform(click());

		// Click the attendee button on the role selection screen
		Espresso.onView(withId(R.id.skip_account_creation)).perform(click());

		// Wait for the RoleSelectionActivity to be displayed
		Espresso.onView(isRoot())
				.check(matches(isDisplayed()));

	}
}

