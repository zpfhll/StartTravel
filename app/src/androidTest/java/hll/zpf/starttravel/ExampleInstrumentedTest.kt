package hll.zpf.starttravel

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import hll.zpf.starttravel.page.SignUpActivity
import hll.zpf.starttravel.page.SplashActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import android.R.attr.description
import android.os.Environment
import android.util.Log
import java.nio.file.Files.exists
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    lateinit var device:UiDevice

    val packgName = "hll.zpf.starttravel"

    @Before
    fun init(){
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testClick(){
        Thread.sleep(2000)
        Espresso.onView(withId(R.id.guide_button)).perform(click())
        takeScreenshot()
        Thread.sleep(500)
        Espresso.onView(withId(R.id.sign_in_bt)).perform(click())
        takeScreenshot()
//        Espresso.onView(withId(R.id.add_travel_btn)).perform(click())
//        takeScreenshot()
        Log.e("test","end ")

    }

    private fun takeScreenshot(){
         val path = File(
             Environment.getExternalStorageDirectory().absolutePath + "/sdcard/Pictures/Screenshots/" + getTargetContext().packageName
         )
        if (!path.exists()) {
            path.mkdirs()
        }
        val date = Date()
        val filename ="$date.png"
        val file = File(path, filename)
        val result = device.takeScreenshot(file)
        Log.e("test","${file.absolutePath}")
        Log.e("test","$result")
    }
}
