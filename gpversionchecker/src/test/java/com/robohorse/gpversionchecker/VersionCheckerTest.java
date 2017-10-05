package com.robohorse.gpversionchecker;

import android.app.Activity;

import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.manager.UIManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Created by vadim on 06.08.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class VersionCheckerTest {
    @Mock private Activity activity;
    @Mock private UIManager uiManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExceptionWhenNullBuilderContext() throws Exception {
        IllegalStateException stateException = null;
        try {
            new GPVersionChecker.Builder(null).create();
        } catch (IllegalStateException exception) {
            stateException = exception;
        }
        assertNotNull("Exception was not handled, but should be", stateException);
    }

    @Test
    public void testOnResponseReceived_whenEmptyListener_withUpdateRequired() throws Exception {
        final Version version = new Version
                .Builder()
                .setNeedToUpdate(true)
                .build();

        new GPVersionChecker
                .Builder(activity, uiManager)
                .create();
        GPVersionChecker.onResponseReceived(version, null);
        Mockito.verify(uiManager).showInfoView(activity, version);
    }

    @Test
    public void testOnResponseReceived_whenEmptyListener_withUpdateNonRequired() throws Exception {
        final Version version = new Version
                .Builder()
                .build();

        new GPVersionChecker
                .Builder(activity, uiManager)
                .create();
        GPVersionChecker.onResponseReceived(version, null);
        Mockito.verifyZeroInteractions(uiManager);
    }
}
