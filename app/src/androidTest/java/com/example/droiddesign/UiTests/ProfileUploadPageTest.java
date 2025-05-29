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
public class ProfileUploadPageTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void UsersToCreationActivityTest() {

			onView(withId(R.id.button_enter)).perform(click( ));
			onView(withId(R.id.button_profile_picture)).perform(click( ));
			onView(withId(R.id.profile_image_view)).check(matches(isDisplayed( )));

	}

}