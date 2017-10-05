package com.robohorse.gpversionchecker;

import android.app.Activity;
import android.content.Intent;

import com.robohorse.gpversionchecker.domain.CheckingStrategy;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.domain.VersionCheckedException;
import com.robohorse.gpversionchecker.manager.ServiceStartManager;
import com.robohorse.gpversionchecker.provider.SharedDataProvider;
import com.robohorse.gpversionchecker.utils.DateFormatUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by v.shchenev on 05/10/2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ServiceStartManagerTest {
    @Mock SharedDataProvider sharedDataProvider;
    @Mock DateFormatUtils formatUtils;
    @InjectMocks ServiceStartManager manager;
    private Activity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = mock(Activity.class);
    }

    @Test
    public void testCheckAndStartService_for_always_strategy() throws Exception {
        manager.checkAndStartService(activity, CheckingStrategy.ALWAYS);
        verify(activity, atLeastOnce()).startService(new Intent(activity, VersionCheckerService.class));
    }

    @Test
    public void testCheckAndStartService_for_one_per_version_strategy() throws Exception {
        manager.checkAndStartService(activity, CheckingStrategy.ONE_PER_VERSION);
        verify(activity, atLeastOnce()).startService(new Intent(activity, VersionCheckerService.class));
    }

    @Test
    public void testCheckAndStartService_for_one_per_day_strategy() throws Exception {
        when(sharedDataProvider.provideLastCheckTime()).thenReturn(new Date().getTime() - TimeUnit.HOURS.toMillis(2));
        when(formatUtils.formatTodayDate()).thenReturn(new Date());

        manager.checkAndStartService(activity, CheckingStrategy.ONE_PER_DAY);
        verify(activity, atLeastOnce()).startService(new Intent(activity, VersionCheckerService.class));
    }

    @Test
    public void testCheckAndStartService_for_one_per_version_per_day_strategy() throws Exception {
        when(sharedDataProvider.provideLastCheckTime()).thenReturn(new Date().getTime() - TimeUnit.HOURS.toMillis(2));
        when(formatUtils.formatTodayDate()).thenReturn(new Date());

        manager.checkAndStartService(activity, CheckingStrategy.ONE_PER_VERSION_PER_DAY);
        verify(activity, atLeastOnce()).startService(new Intent(activity, VersionCheckerService.class));
    }

    @Test
    public void testOnResult_one_per_version() throws Exception {
        when(sharedDataProvider.provideLastVersionCode()).thenReturn("1");
        final Version version = new Version
                .Builder()
                .setNewVersionCode("1")
                .build();
        VersionCheckedException exception = null;
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION, version);
        } catch (VersionCheckedException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void testOnResult_one_per_version_per_day() throws Exception {
        when(sharedDataProvider.provideLastVersionCode()).thenReturn("1");
        final Version version = new Version
                .Builder()
                .setNewVersionCode("1")
                .build();
        VersionCheckedException exception = null;
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION_PER_DAY, version);
        } catch (VersionCheckedException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void testOnResult_one_per_version_isValid() throws Exception {
        when(sharedDataProvider.provideLastVersionCode()).thenReturn("1");
        final Version version = new Version
                .Builder()
                .setNewVersionCode("2")
                .build();
        VersionCheckedException exception = null;
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION, version);
        } catch (VersionCheckedException e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void testOnResult_one_per_version_per_day_isValid() throws Exception {
        when(sharedDataProvider.provideLastVersionCode()).thenReturn("1");
        final Version version = new Version
                .Builder()
                .setNewVersionCode("2")
                .build();
        VersionCheckedException exception = null;
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION_PER_DAY, version);
        } catch (VersionCheckedException e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void testOnResult_one_per_day_isValid() throws Exception {
        when(sharedDataProvider.provideLastVersionCode()).thenReturn("1");
        final Version version = new Version
                .Builder()
                .setNewVersionCode("1")
                .build();
        VersionCheckedException exception = null;
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_DAY, version);
        } catch (VersionCheckedException e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void testOnResult_always_isValid() throws Exception {
        when(sharedDataProvider.provideLastVersionCode()).thenReturn("1");
        final Version version = new Version
                .Builder()
                .setNewVersionCode("1")
                .build();
        VersionCheckedException exception = null;
        try {
            manager.onResulted(CheckingStrategy.ALWAYS, version);
        } catch (VersionCheckedException e) {
            exception = e;
        }
        assertNull(exception);
    }
}
