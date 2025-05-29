package com.example.droiddesign.UiTests;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;

public class AllowNotificationTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> activityScenarioRule
			= new ActivityScenarioRule<>(LaunchScreenActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void testLaunchScreen() {
		// Check if the enter button is displayed
		Espresso.onView(ViewMatchers.withId(R.id.button_enter))
				.check(matches(isDisplayed()));

		// Check if the enter button is enabled
		Espresso.onView(ViewMatchers.withId(R.id.button_enter))
				.check(matches(isEnabled()));

		// Simulate a button click to open the login fragment
		Espresso.onView(ViewMatchers.withId(R.id.button_enter))
				.perform(ViewActions.click());

	}

}
