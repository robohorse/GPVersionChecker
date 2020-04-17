package com.robohorse.gpversionchecker

import android.app.Activity
import com.robohorse.gpversionchecker.GPVersionChecker.onResponseReceived
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.manager.UIManager
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class VersionCheckerTest {
    @RelaxedMockK
    lateinit var activity: Activity

    @RelaxedMockK
    lateinit var uiManager: UIManager

    @Test
    fun testOnResponseReceived_whenEmptyListener_withUpdateRequired() {
        val version = Version(isNeedToUpdate = true)
        GPVersionChecker.Builder(activity, uiManager).create()
        onResponseReceived(version, null)
        verify { uiManager.showInfoView(activity, version) }
    }
}