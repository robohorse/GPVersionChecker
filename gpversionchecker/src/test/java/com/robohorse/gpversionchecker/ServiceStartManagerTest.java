package com.robohorse.gpversionchecker;

import android.app.Activity;
import android.content.Intent;

import com.robohorse.gpversionchecker.domain.CheckingStrategy;
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
}
