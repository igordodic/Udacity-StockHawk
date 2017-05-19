package com.udacity.stockhawk.sync;

import android.support.test.espresso.IdlingResource;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by Igor on 04/05/2017.
 */

public class AddStockIdlingResource implements IdlingResource {

  private ResourceCallback resourceCallback;
  private MainActivity mainActivity;
  private MainActivity.ProgressListener progressListener;

    public AddStockIdlingResource(MainActivity main){
      mainActivity = main;

      progressListener = new MainActivity.ProgressListener() {
        @Override public void onShown() {
        }

        @Override public void onDismissed() {
          if (resourceCallback!=null)
            resourceCallback.onTransitionToIdle();
        }
      };
      mainActivity.setProgressListener(progressListener);
    }

  @Override public String getName() {
    return mainActivity.getLocalClassName();
  }

  @Override public boolean isIdleNow() {
    return !mainActivity.isInProgress();
  }

  @Override public void registerIdleTransitionCallback(ResourceCallback callback) {
      resourceCallback = callback;
  }
}
