package com.example.client;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StartActivityTest1 {
    private String serverAddress = "84.201.154.171";
    private String login = "A";
    private String password = "A";

    @Rule
    public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(StartActivity.class);

    @Test
    public void startActivityTest1() {
        onView(allOf(withId(R.id.settings_head),
            childAtPosition(childAtPosition(
                withClassName(
                    is("android.widget.LinearLayout")),
                1), 0),
            isDisplayed())).perform(click());

        onView(allOf(withId(R.id.address),
            childAtPosition(childAtPosition(
                withId(R.id.settings_drawer_layout),
                0), 1),
            isDisplayed()))
            .perform(replaceText(serverAddress));

        onView(allOf(withText("Сохранить"),
            childAtPosition(childAtPosition(
                withId(R.id.settings_drawer_layout),
                0), 4),
            isDisplayed())).perform(click());

        pressBack();

        onView(allOf(withId(R.id.login),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                0), 2),
            isDisplayed())).perform(replaceText(login));

        onView(allOf(withId(R.id.password),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                0), 3),
            isDisplayed())).perform(replaceText(password));

        onView(allOf(withId(R.id.sign_in), withText("Войти"),
            childAtPosition(childAtPosition(
                    withClassName(is("android.widget.LinearLayout")),
                    0), 5),
            isDisplayed())).perform(click());

        onView(allOf(withId(R.id.resultView),
            childAtPosition(childAtPosition(
                    IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                    0), 1),
            isDisplayed())).perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }
            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}