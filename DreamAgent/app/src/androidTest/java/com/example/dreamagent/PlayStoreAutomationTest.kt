package com.example.dreamagent

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayStoreAutomationTest {

    @Test
    fun installUberAppViaPlayStore() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Open Play Store with Uber's Play Store URL
        val intent = InstrumentationRegistry.getInstrumentation().targetContext.packageManager
            .getLaunchIntentForPackage("com.android.vending")?.apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.ubercab")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        InstrumentationRegistry.getInstrumentation().targetContext.startActivity(intent)

        println("SLEEPING!!")

        // Wait for the Play Store to load
        Thread.sleep(5000)  // Adjust this based on network speed

        println("WOKE UP!!")

        // Find and click the "Install" button
        val installButton = device.findObject(UiSelector().text("Install"))
        println("AM I HERE, ${installButton.exists()}")
        if (installButton.exists()) {
            installButton.click()
            println("CLICKING")
        } else {
            println("Install button not found")
        }
    }
}
