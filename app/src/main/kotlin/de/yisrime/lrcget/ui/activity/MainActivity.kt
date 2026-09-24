package de.yisrime.lrcget.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.databinding.ActivityMainBinding
import de.yisrime.lrcget.tool.ActivityTools
import de.yisrime.lrcget.tool.Tools.xpActivation
import de.yisrime.lrcbar.LyricApplication
import de.yisrime.lrcget.ui.viewmodel.ShareViewModel
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private val shareViewModel: ShareViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var activatedChange: ((Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityTools.context = this
        shareViewModel.activated = xpActivation
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            if (!shareViewModel.activated) {
                nav.visibility = View.GONE
            }
        }
        activatedChange = { activated ->
            runOnUiThread {
                if (isDestroyed) return@runOnUiThread
                if (shareViewModel.activated != activated) {
                    shareViewModel.activated = activated
                    recreate()
                }
            }
        }
        LyricApplication.activatedChange = activatedChange
    }

    override fun onStart() {
        super.onStart()
        LyricApplication.refreshScope()
        (binding.nav as NavigationBarView).setupWithNavController(findNavController(R.id.nav_host_fragment))
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) return
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }

    override fun onDestroy() {
        if (LyricApplication.activatedChange === activatedChange) LyricApplication.activatedChange = null
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}