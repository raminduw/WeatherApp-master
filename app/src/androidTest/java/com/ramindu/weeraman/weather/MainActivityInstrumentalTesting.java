package com.ramindu.weeraman.weather;


import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.espresso.Root;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.ramindu.weeraman.weather.views.MainActivity;

import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@SuppressWarnings("DefaultFileTemplate")
@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityInstrumentalTesting {
    @Rule

    public IntentsTestRule<MainActivity> mainActivityActivityTestRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() {

    }

    @Test
    public void testFabButtonVisibility() {

        onView(ViewMatchers.withId(R.id.fabAddCity))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewVisibility() {
        onView(ViewMatchers.withId(R.id.recyclerViewWeatherCards))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddDialogVisibility() {
        onView(ViewMatchers.withId(R.id.fabAddCity))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .perform(click());
        onView(withText((mainActivityActivityTestRule.getActivity().getResources().getString(R.string.add_city_header))))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddDialogCancel() {
        onView(ViewMatchers.withId(R.id.fabAddCity))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .perform(click());

        onView(ViewMatchers.withText(R.string.cancel))
                .inRoot(isDialog())
                .perform(click());

        onView(ViewMatchers.withId(R.id.recyclerViewWeatherCards))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddCityWithoutNameShowToastMessage() {
        onView(ViewMatchers.withId(R.id.fabAddCity))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .perform(click());

        onView(ViewMatchers.withText(R.string.add))
                .inRoot(isDialog())
                .perform(click());

        onView(ViewMatchers.withId(R.id.recyclerViewWeatherCards))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        onView(withText(R.string.city_name_empty)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));


    }

    @Test
    public void testShowInvalidCityNameToastMessage() {
        onView(ViewMatchers.withId(R.id.fabAddCity))
                .inRoot(withDecorView(
                        Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .perform(click());


        onView(ViewMatchers.withId(R.id.editTextAddCityName))
                .inRoot(isDialog())
                .perform(typeText("123-xxx-333"), closeSoftKeyboard());

        onView(ViewMatchers.withText(R.string.add))
                .inRoot(isDialog())
                .perform(click());


        onView(withText(R.string.add_city_failed)).inRoot(new ToastMatcher())
                .check(matches((isDisplayed())));


    }


    public class ToastMatcher extends TypeSafeMatcher<Root> {


        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText("is toast");
        }


    }


}
