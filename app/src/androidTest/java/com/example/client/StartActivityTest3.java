package com.example.client;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StartActivityTest3 {
    private String serverAddress = "84.201.156.235";
    private String login = "A";
    private String password = "A";

    @Rule
    public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(StartActivity.class);

    @Test
    public void startActivityTest2() {
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

        onView(allOf(withId(R.id.button1),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                5), 0),
            isDisplayed())).perform(click());

        onView(allOf(withId(R.id.button0),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                6), 1),
            isDisplayed())).perform(click(),click());

        onView(allOf(withId(R.id.listViewCurrency),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                2), 0),
            isDisplayed())).perform(click());

        if (Build.VERSION.SDK_INT >= 23) {
            onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                    childAtPosition(withId(R.id.contentPanel), 0)))
                .atPosition(0).perform(click());
        }
        else {
            onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                    childAtPosition(withId(R.id.contentPanel), 1)))
                .atPosition(0).perform(click());
        }

        onView(allOf(withId(R.id.listViewCapitalization), withText("Капитализация"),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                1), 0),
                isDisplayed())).perform(click());

        if (Build.VERSION.SDK_INT >= 23) {
            onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                    childAtPosition(withId(R.id.contentPanel), 0)))
                .atPosition(1).perform(click());
        }
        else{
            onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                    childAtPosition(withId(R.id.contentPanel), 1)))
                .atPosition(1).perform(click());
        }

        CalculateActivity.period[0] = new AppBase.Date(2020, (byte) 5, (byte) 12);
        CalculateActivity.period[1] = new AppBase.Date(2021, (byte) 5, (byte) 12);

        onView(withId(R.id.enterPercentsBar)).perform(setProgress(504));

        onView(allOf(withId(R.id.compute_button),
            childAtPosition(childAtPosition(
                withClassName(is("android.widget.LinearLayout")),
                6), 3),
            isDisplayed())).perform(click());

        onView(allOf(withId(R.id.resultView), withText("= 105.08 $"),
            childAtPosition(childAtPosition(
                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                0), 1),
            isDisplayed())).perform(click());
    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
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
    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }
}