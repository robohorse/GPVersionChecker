package com.robohorse.gpversionchecker

import android.content.Context
import com.robohorse.gpversionchecker.domain.CheckingStrategy
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.domain.VersionCheckedException
import com.robohorse.gpversionchecker.executor.SyncExecutor
import com.robohorse.gpversionchecker.manager.ServiceStartManager
import com.robohorse.gpversionchecker.provider.SharedDataProvider
import com.robohorse.gpversionchecker.utils.DateFormatUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class ServiceStartManagerTest {
    @RelaxedMockK
    lateinit var sharedDataProvider: SharedDataProvider

    @RelaxedMockK
    lateinit var formatUtils: DateFormatUtils

    @RelaxedMockK
    lateinit var syncExecutor: SyncExecutor

    @RelaxedMockK
    lateinit var context: Context

    @InjectMockKs
    lateinit var manager: ServiceStartManager

    @Test
    fun testCheckAndStartService_for_always_strategy() {
        manager.checkAndStartService(context, CheckingStrategy.ALWAYS)
        verify { syncExecutor.startSync(context) }
    }

    @Test
    fun testCheckAndStartService_for_one_per_version_strategy() {
        manager.checkAndStartService(context, CheckingStrategy.ONE_PER_VERSION)
        verify { syncExecutor.startSync(context) }
    }

    @Test
    fun testCheckAndStartService_for_one_per_day_strategy() {
        every {
            sharedDataProvider.provideLastCheckTime()
        }.returns(Date().time - TimeUnit.HOURS.toMillis(2))
        every { formatUtils.formatTodayDate() }.returns(Date())
        manager.checkAndStartService(context, CheckingStrategy.ONE_PER_DAY)
        verify { syncExecutor.startSync(context) }
    }

    @Test
    fun testCheckAndStartService_for_one_per_version_per_day_strategy() {
        every {
            sharedDataProvider.provideLastCheckTime()
        }.returns(Date().time - TimeUnit.HOURS.toMillis(2))
        every { formatUtils.formatTodayDate() }.returns(Date())
        manager.checkAndStartService(context, CheckingStrategy.ONE_PER_VERSION_PER_DAY)
        verify { syncExecutor.startSync(context) }
    }

    @Test
    fun testOnResult_one_per_version() {
        every {
            sharedDataProvider.provideLastVersionCode()
        }.returns("1")

        val version = Version(newVersionCode = "1")
        var exception: VersionCheckedException? = null
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION, version)
        } catch (e: VersionCheckedException) {
            exception = e
        }
        assertNotNull(exception)
    }

    @Test
    fun testOnResult_one_per_version_per_day() {
        every {
            sharedDataProvider.provideLastVersionCode()
        }.returns("1")
        val version = Version(newVersionCode = "1")
        var exception: VersionCheckedException? = null
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION_PER_DAY, version)
        } catch (e: VersionCheckedException) {
            exception = e
        }
        assertNotNull(exception)
    }

    @Test
    fun testOnResult_one_per_version_isValid() {
        every {
            sharedDataProvider.provideLastVersionCode()
        }.returns("1")
        val version = Version(newVersionCode = "2")
        var exception: VersionCheckedException? = null
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION, version)
        } catch (e: VersionCheckedException) {
            exception = e
        }
        assertNull(exception)
    }

    @Test
    fun testOnResult_one_per_version_per_day_isValid() {
        every {
            sharedDataProvider.provideLastVersionCode()
        }.returns("1")
        val version = Version(newVersionCode = "2")
        var exception: VersionCheckedException? = null
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_VERSION_PER_DAY, version)
        } catch (e: VersionCheckedException) {
            exception = e
        }
        assertNull(exception)
    }

    @Test
    fun testOnResult_one_per_day_isValid() {
        every {
            sharedDataProvider.provideLastVersionCode()
        }.returns("1")
        val version = Version(newVersionCode = "1")
        var exception: VersionCheckedException? = null
        try {
            manager.onResulted(CheckingStrategy.ONE_PER_DAY, version)
        } catch (e: VersionCheckedException) {
            exception = e
        }
        assertNull(exception)
    }

    @Test
    fun testOnResult_always_isValid() {
        every {
            sharedDataProvider.provideLastVersionCode()
        }.returns("1")
        val version = Version(newVersionCode = "1")
        var exception: VersionCheckedException? = null
        try {
            manager.onResulted(CheckingStrategy.ALWAYS, version)
        } catch (e: VersionCheckedException) {
            exception = e
        }
        assertNull(exception)
    }
}
