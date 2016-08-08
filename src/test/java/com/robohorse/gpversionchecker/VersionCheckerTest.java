package com.robohorse.gpversionchecker;

import android.app.Activity;

import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.helper.SharedPrefsHelper;
import com.robohorse.gpversionchecker.helper.UIHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Created by vadim on 06.08.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class VersionCheckerTest {
    @Mock
    private Activity activity;

    @Mock
    private SharedPrefsHelper sharedPrefsHelper;

    @Mock
    private UIHelper uiHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExceptionWhenNullBuilderContext() throws Exception {
        RuntimeException runtimeException = null;
        try {
            new GPVersionChecker.Builder(null).create();
        } catch (RuntimeException exception) {
            runtimeException = exception;
        }
        assertNotNull("Exception was not handled, but should be", runtimeException);
    }

    @Test
    public void testOnResponseReceived_whenEmptyListener_withUpdateRequired() throws Exception {
        Version version = new Version
                .Builder()
                .setNeedToUpdate(true)
                .build();

        new GPVersionChecker
                .Builder(activity, uiHelper, sharedPrefsHelper)
                .create();
        GPVersionChecker.onResponseReceived(version);
        Mockito.verify(uiHelper).showInfoView(activity, version);
    }

    @Test
    public void testOnResponseReceived_whenEmptyListener_withUpdateNonRequired() throws Exception {
        Version version = new Version
                .Builder()
                .build();

        new GPVersionChecker
                .Builder(activity, uiHelper, sharedPrefsHelper)
                .create();
        GPVersionChecker.onResponseReceived(version);
        Mockito.verifyZeroInteractions(uiHelper);
    }
}
