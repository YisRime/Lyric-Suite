package de.yisrime.lrcget.ui.activity

import android.Manifest
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import de.yisrime.lyricsuite.R
import cn.lyric.getter.api.API
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.tools.Tools.registerLyricListener
import cn.lyric.getter.api.tools.Tools.unregisterLyricListener
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.EventTools
import de.yisrime.lrcget.tool.Tools
import com.google.android.material.appbar.MaterialToolbar

class TestActivity : AppCompatActivity() {
    private lateinit var receiver: LyricReceiver
    private var mi: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val testView = findViewById<LinearLayout>(R.id.test_linearlayout)

        val testAppName = findViewById<TextView>(R.id.test_app_name_text)
        val testAppIcon = findViewById<TextView>(R.id.test_app_icon_text)
        val testAppCustomIcon = findViewById<TextView>(R.id.test_app_customIcon_text)
        val testAppPlay = findViewById<TextView>(R.id.test_app_play_text)
        val testAppLyric = findViewById<TextView>(R.id.test_app_lyric_text)
        testAppLyric.setSelected(true)
        val testAppDelay = findViewById<TextView>(R.id.test_app_delay_text)

        receiver = LyricReceiver(object : LyricListener() {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onUpdate(lyricData: LyricData) {
                updateTestLyricViews(
                    lyricData, testAppLyric, testAppIcon, testAppCustomIcon,
                    testAppPlay, testAppName, testAppDelay
                )
                if (mi) Tools.sendNotification(lyricData.lyric, lyricData.extraData.packageName, this@TestActivity)
            }

            override fun onStop(lyricData: LyricData) {
                updateTestLyricViews(
                    lyricData, testAppLyric, testAppIcon, testAppCustomIcon,
                    testAppPlay, testAppName, testAppDelay
                )
                if (mi) Tools.cancelNotification(context = this@TestActivity)
            }
        })
        registerLyricListener(this@TestActivity, API.API_VERSION, receiver)

        ConfigScreen(this, testView).apply {
            action(R.string.send_test_lyric) {
                EventTools(this@TestActivity).sendLyric("testlyric")
            }
            action(R.string.clean_test_lyric) {
                EventTools(this@TestActivity).cleanLyric()
            }
            switch(R.string.mifocus, R.string.mifocus_tips, prop = this@TestActivity::mi) {
                if (!it) Tools.cancelNotification(context = this@TestActivity)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterLyricListener(this, receiver)
        if (mi) Tools.cancelNotification(context = this@TestActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterLyricListener(this, receiver)
        if (mi) Tools.cancelNotification(context = this@TestActivity)
    }

    override fun onResume() {
        super.onResume()
        registerLyricListener(this@TestActivity, API.API_VERSION, receiver)
    }

    private fun updateTestLyricViews(
        lyricData: LyricData,
        testAppLyric: TextView,
        testAppIcon: TextView,
        testAppCustomIcon: TextView,
        testAppPlay: TextView,
        testAppName: TextView,
        testAppDelay: TextView
    ) {
        testAppLyric.text = lyricData.lyric
        testAppIcon.text = lyricData.extraData.base64Icon
        testAppCustomIcon.text = lyricData.extraData.customIcon.toString()
        testAppPlay.text = lyricData.type.toString()
        this.let { testAppName.text = it.getPackageManager().getApplicationInfo(lyricData.extraData.packageName, 0).loadLabel(it.packageManager) }
        testAppDelay.text = lyricData.extraData.delay.toString()
    }
}
