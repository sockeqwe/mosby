package com.hannesdorfmann.mvp_integration_test;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hannesdorfmann.mosby3.mvp.integrationtest.ViewStateFinishOnCreateActivity;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewGroupFinishActivityOnCreateActivityTest {
    @Rule
    public ActivityTestRule<ViewStateFinishOnCreateActivity> rule =
            new ActivityTestRule<>(ViewStateFinishOnCreateActivity.class);

    @Test
    public void dontCrash() throws InterruptedException {
        ViewStateFinishOnCreateActivity activity = rule.getActivity();
        Thread.sleep(2000);
        Assert.assertNotNull(activity.presenter);
        Assert.assertEquals(1, activity.createPresenterInvocation);
    }
}
