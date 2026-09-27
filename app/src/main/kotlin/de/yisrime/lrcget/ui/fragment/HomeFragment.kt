package de.yisrime.lrcget.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import de.yisrime.lyricsuite.BuildConfig
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.databinding.FragmentHomeBinding
import de.yisrime.lrcget.tool.ActivityTools.rules
import de.yisrime.lrcget.tool.Tools.getPhoneName
import de.yisrime.lrcget.tool.Tools.restartTheScopedSoftware
import de.yisrime.lrcget.ui.viewmodel.HomeViewModel
import de.yisrime.lrcget.ui.viewmodel.ShareViewModel
import cn.xiaowine.xkt.AcTool.restartApp
import cn.xiaowine.xkt.Tool.isNotNull
import cn.xiaowine.xkt.Tool.toUpperFirstCaseAndLowerOthers
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private val shareViewModel: ShareViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            nestedScrollView.scrollY = homeViewModel.scrollY
            if (!shareViewModel.activated) {
                statusIcon.setImageResource(R.drawable.ic_round_error_outline)
                statusTitle.text = getString(R.string.unactivated)
                statusSummary.text = getString(R.string.unactivated_summary)
                status.apply {
                    setBackgroundColor(MaterialColors.getColor(requireContext(), android.R.attr.colorError, Color.RED))
                    setOnClickListener {
                        restartApp()
                    }
                }
                floatingActionButton.visibility = View.GONE
            }
            if (shareViewModel.activated) {
                floatingActionButton.setOnClickListener { view ->
                    Snackbar.make(view, getString(R.string.restart_the_scoped_software), Snackbar.LENGTH_LONG).apply {
                        anchorView = view
                        setAction(getString(R.string.restart)) {
                            restartTheScopedSoftware(context)
                        }
                    }.show()
                }
            }
            deviceValue.text = "$getPhoneName (${Build.DEVICE}) Android ${Build.VERSION.RELEASE}"
            versionLabelValue.text = BuildConfig.VERSION_NAME
            versionCodeValue.text = BuildConfig.VERSION_CODE.toString()
            versionTypeValue.text = BuildConfig.BUILD_TYPE.toUpperFirstCaseAndLowerOthers()
            buildTimeValue.text = homeViewModel.buildTimeValue ?: SimpleDateFormat("yyyy-MM-dd HH:mm z", Locale.getDefault()).format(BuildConfig.BUILD_TIME)
            apiVersionValue.text = BuildConfig.API_VERSION.toString()
            configVersionValue.text = BuildConfig.CONFIG_VERSION.toString()
            appRulesVersionValue.text = homeViewModel.appRulesVersionValue ?: rules.appRulesVersion.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.apply {
            scrollY = binding.nestedScrollView.scrollY
            appRulesVersionValue = binding.appRulesVersionValue.text.toString()
            homeViewModel.buildTimeValue = binding.buildTimeValue.text.toString()
        }
        _binding = null
    }
}