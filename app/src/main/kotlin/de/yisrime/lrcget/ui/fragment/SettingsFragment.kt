package de.yisrime.lrcget.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.databinding.FragmentSettingsBinding
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcget.tool.Tools.xpActivation
import de.yisrime.lrcbar.config.XposedOwnSP.config as barConfig
import de.yisrime.lrcget.tool.Tools.dp2px
import de.yisrime.lrcget.ui.activity.DialogTransparentActivity
import de.yisrime.lrcget.ui.activity.LogActivity
import de.yisrime.lrcget.ui.activity.TestActivity
import de.yisrime.lrcget.ui.view.Preferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    companion object {
        private const val LAUNCHER_ALIAS = "de.yisrime.lrcbar.AliasActivity"
    }


    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSettings() {
        val context = context ?: return

        binding.fragmentSettingLinearlayout.apply {
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.master_switch,
                    isChecked = barConfig.masterSwitch,
                    onCheckedChange = { _, isChecked ->
                        if (xpActivation) {
                            barConfig.masterSwitch = isChecked
                        } else {
                            barConfig.masterSwitch = false
                            Toast.makeText(context, R.string.module_inactivated, Toast.LENGTH_SHORT).show()
                        }
                    }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.show_launcher_icon,
                    isChecked = barConfig.showLauncherIcon,
                    onCheckedChange = { _, isChecked ->
                        barConfig.showLauncherIcon = isChecked
                        toggleLauncherIcon(isChecked)
                    }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.salt_use_flyme,
                    summaryResId = R.string.salt_use_flyme_summary,
                    isChecked = config.saltUseFlyme,
                    onCheckedChange = { _, isChecked -> config.saltUseFlyme = isChecked }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.regex_replace,
                    onClick = {
                        val intent = Intent(context, DialogTransparentActivity::class.java)
                        startActivity(intent)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.lyric_setting,
                    onClick = { showLyricSettingsDialog(context) }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.fuck_wyy_about,
                    onClick = { showFuckWyySettingsDialog(context) }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.lyric_test,
                    onClick = {
                        val intent = Intent(context, TestActivity::class.java)
                        startActivity(intent)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.hook_page,
                    onClick = {
                        findNavController().navigate(R.id.test_nav)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.icon_page,
                    onClick = {
                        findNavController().navigate(R.id.icon_nav)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.lyric_page,
                    onClick = {
                        findNavController().navigate(R.id.lyric_nav)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.extend_page,
                    onClick = {
                        findNavController().navigate(R.id.extend_nav)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.system_special_page,
                    onClick = {
                        findNavController().navigate(R.id.system_special_nav)
                    }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.log_page,
                    onClick = {
                        val intent = Intent(context, LogActivity::class.java)
                        startActivity(intent)
                    }
                ))
        }
    }

    private fun showLyricSettingsDialog(context: Context) {
        val (scrollView, contentLayout) = createScrollableDialogLayout(context)

        contentLayout.apply {
            // addView(
            //     createSwitchView(
            //         context = context,
            //         titleResId = R.string.enhanced_hidden_lyrics,
            //         summaryResId = R.string.enhanced_hidden_lyrics_summary,
            //         isChecked = config.enhancedHiddenLyrics,
            //         onCheckedChange = { _, isChecked -> config.enhancedHiddenLyrics = isChecked }
            //     ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.output_repeated_lyrics,
                    isChecked = config.outputRepeatedLyrics,
                    onCheckedChange = { _, isChecked -> config.outputRepeatedLyrics = isChecked }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.allow_some_software_to_output_after_the_screen,
                    isChecked = config.allowSomeSoftwareToOutputAfterTheScreen,
                    onCheckedChange = { _, isChecked ->
                        config.allowSomeSoftwareToOutputAfterTheScreen = isChecked
                    }
                ))
            // addView(
            //     createSwitchView(
            //         context = context,
            //         titleResId = R.string.show_title,
            //         isChecked = config.showTitle,
            //         onCheckedChange = { _, isChecked -> config.showTitle = isChecked }
            //     ))
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.lyric_setting)
            .setView(scrollView)
            .show()
    }

    private fun showFuckWyySettingsDialog(context: Context) {
        val (scrollView, contentLayout) = createScrollableDialogLayout(context)

        contentLayout.apply {
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.fuck_fuck_wyy,
                    summaryResId = R.string.fuck_fuck_wyy_tips,
                    isChecked = config.fuckWyy2,
                    onCheckedChange = { _, isChecked -> config.fuckWyy2 = isChecked }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.fuck_wyy,
                    summaryResId = R.string.fuck_wyy_tips,
                    isChecked = config.fuckWyy,
                    onCheckedChange = { _, isChecked -> config.fuckWyy = isChecked }
                ))
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.fuck_wyy_about)
            .setView(scrollView)
            .show()
    }


    private fun toggleLauncherIcon(enabled: Boolean) {
        requireContext().packageManager.setComponentEnabledSetting(
            ComponentName(requireContext(), LAUNCHER_ALIAS),
            if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            PackageManager.DONT_KILL_APP
        )
    }

    private fun createScrollableDialogLayout(context: Context): Pair<ScrollView, LinearLayout> {
        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 0, 0, dp2px(context, 8f))
        }

        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        scrollView.addView(contentLayout)
        return Pair(scrollView, contentLayout)
    }

    private fun createSwitchView(
        context: Context,
        titleResId: Int,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: (CompoundButton, Boolean) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            summaryResId = summaryResId,
            isChecked = isChecked,
            onCheckedChange = onCheckedChange,
            hideSummary = summaryResId == 0,
            hideSwitch = false
        )
    }

    private fun createClickableView(
        context: Context,
        titleResId: Int,
        onClick: (View) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            onClick = onClick,
            hideSummary = true,
            hideSwitch = true
        )
    }

    private fun createCustomView(
        context: Context,
        titleResId: Int,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: ((CompoundButton, Boolean) -> Unit)? = null,
        hideSummary: Boolean = false,
        hideSwitch: Boolean = false,
        onClick: ((View) -> Unit)? = null
    ): View {
        val preferences = Preferences(context)
        preferences.setViewClickToggleSwitch()
        val switchView = preferences.getView()

        // Title
        preferences.preferencesTitle.setText(titleResId)

        // Summary
        if (hideSummary || summaryResId == 0) {
            preferences.preferencesSummary.visibility = View.GONE
        } else {
            preferences.setSummary(summaryResId)
        }

        // Switch
        if (hideSwitch || onCheckedChange == null) {
            preferences.preferencesButton.visibility = View.GONE
        } else {
            preferences.preferencesButton.isChecked = isChecked
            preferences.preferencesButton.setOnCheckedChangeListener(onCheckedChange)
        }

        // Click listener
        onClick?.let { switchView.setOnClickListener(it) }

        return switchView
    }
}
