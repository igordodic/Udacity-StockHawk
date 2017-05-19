package com.udacity.stockhawk;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.udacity.stockhawk.sync.AddStockIdlingResource;
import com.udacity.stockhawk.ui.MainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.PendingIntent.getActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddNewStockTest{
  @Rule public final ActivityTestRule<MainActivity> mainActivityRule
      = new ActivityTestRule<>(MainActivity.class);

  private IdlingResource idlingResource;
  @Before
  public void registerIdlingResource(){
    idlingResource = new AddStockIdlingResource( mainActivityRule.getActivity());
    Espresso.registerIdlingResources(idlingResource);
  }

  @Test
    public void shouldBeAbleToAddStockAndHaveThemDisplayed(){
    onView(withId(R.id.fab)).perform(click());
    onView(withId(R.id.dialog_stock)).perform(typeText("SNAP"));
    onView(withText(R.string.dialog_add)).perform(click());
    onView(withText("SNAP")).check(matches(isDisplayed()));

  }

  @After
  public void unregisterIdlingResources(){
    Espresso.unregisterIdlingResources(idlingResource);
  }
}