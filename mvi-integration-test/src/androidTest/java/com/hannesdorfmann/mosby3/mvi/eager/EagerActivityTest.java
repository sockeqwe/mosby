package com.hannesdorfmann.mosby3.mvi.eager;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hannesdorfmann.mosby3.mvi.integrationtest.eager.EagerViewActivity;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class EagerActivityTest {

    @Rule
    public ActivityTestRule<EagerViewActivity> rule =
            new ActivityTestRule<>(EagerViewActivity.class);


    @Test
    public void testStatesRendered() {
        EagerViewActivity activity = rule.getActivity();

        List<String> renderd = activity.renderedStrings.take(3).timeout(5, TimeUnit.SECONDS)
                .toList().blockingGet();

        Assert.assertEquals(Arrays.asList("Before Intent 1 - Result 1", "Intent 1 - Result 1", "Intent 2 - Result 2"), renderd);
    }


}
