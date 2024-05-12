package com.euphoiniateam

import android.app.Instrumentation
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertDataSourceTest() : Instrumentation() {
    lateinit var contextt: Context

    @Before
    fun setUp() {
        contextt = InstrumentationRegistry.getInstrumentation().targetContext
    }
}
