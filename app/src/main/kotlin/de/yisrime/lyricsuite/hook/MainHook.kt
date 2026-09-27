package de.yisrime.lyricsuite.hook

import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.moduleRes
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import cn.xiaowine.xkt.LogTool
import de.yisrime.lyricsuite.BuildConfig
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.config.DisplayStore
import de.yisrime.lyricsuite.config.GetterStore
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.hook.module.SystemUILyric
import de.yisrime.lrcbar.hook.module.SystemUITest
import de.yisrime.lrcbar.tools.LogTools.log
import de.yisrime.lrcget.hook.app.APlayer
import de.yisrime.lrcget.hook.app.Api
import de.yisrime.lrcget.hook.app.Apple
import de.yisrime.lrcget.hook.app.Aqzscn
import de.yisrime.lrcget.hook.app.Bodian
import de.yisrime.lrcget.hook.app.Flamingo
import de.yisrime.lrcget.hook.app.Gramophone
import de.yisrime.lrcget.hook.app.Huawei
import de.yisrime.lrcget.hook.app.Kde
import de.yisrime.lrcget.hook.app.Kugou
import de.yisrime.lrcget.hook.app.Kuwo
import de.yisrime.lrcget.hook.app.LMusic
import de.yisrime.lrcget.hook.app.Luna
import de.yisrime.lrcget.hook.app.Meizu
import de.yisrime.lrcget.hook.app.MiPlayer
import de.yisrime.lrcget.hook.app.Mimicry
import de.yisrime.lrcget.hook.app.MobileMusic
import de.yisrime.lrcget.hook.app.MusicFree
import de.yisrime.lrcget.hook.app.MusicPlayer
import de.yisrime.lrcget.hook.app.Netease
import de.yisrime.lrcget.hook.app.NeteaseLite
import de.yisrime.lrcget.hook.app.Oppo
import de.yisrime.lrcget.hook.app.Poweramp
import de.yisrime.lrcget.hook.app.QQMusic
import de.yisrime.lrcget.hook.app.Qinalt
import de.yisrime.lrcget.hook.app.RPlayer
import de.yisrime.lrcget.hook.app.Salt
import de.yisrime.lrcget.hook.app.SystemUi
import de.yisrime.lrcget.hook.app.Toside
import de.yisrime.lrcget.tool.HookTools.isApi
import java.util.Locale

class MainHook : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        EzXposed.initOnModuleLoaded(this, param)
        EzXposed.initModuleResources()
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        EzXposed.initOnPackageLoaded(param)
        EzXReflection.init(param.defaultClassLoader)
        attachPreferences()
        LogTool.init("Lyrics Getter", { false }, false)
        "${frameworkName} ${frameworkVersion}(${frameworkVersionCode}) api=$apiVersion".log()
        "onPackageLoaded: ${param.packageName}".log()
        // 钩子对象初始化抛的 Error（如宿主缺原生库）不是 Exception，initHooks 接不住，必须在此兜住并留日志
        runCatching {
            when (param.packageName) {
                SYSTEM_UI -> hookSystemUi()
                SELF -> Unit
                else -> hookLyricSource(param.packageName)
            }
        }.onFailure {
            "Dispatch failed for ${param.packageName}: ${it.javaClass.name}".log()
            it.log()
        }
    }

    private fun hookSystemUi() {
        initHooks(SystemUi)
        if (config.testMode) {
            moduleRes.getString(R.string.hook_page).log()
            initHooks(SystemUITest())
            return
        }
        "${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${
            Locale.getDefault().language
        }] *${BuildConfig.BUILD_TYPE})".log()
        moduleRes.getString(R.string.lyric_mode).log()
        try {
            initHooks(SystemUILyric())
        } catch (t: Throwable) {
            t.log()
        }
    }

    private fun hookLyricSource(packageName: String) {
        when (packageName) {
            "com.tencent.qqmusic" -> initHooks(QQMusic)
            "com.miui.player" -> initHooks(MiPlayer)
            "com.netease.cloudmusic" -> initHooks(Netease)
            "com.netease.cloudmusic.lite" -> initHooks(NeteaseLite)
            "com.kugou.android", "com.kugou.android.lite" -> initHooks(Kugou)
            "cn.kuwo.player" -> initHooks(Kuwo)
            "remix.myplayer" -> initHooks(APlayer)
            "cmccwm.mobilemusic" -> initHooks(MobileMusic)
            "com.meizu.media.music" -> initHooks(Meizu)
            "com.r.rplayer" -> initHooks(RPlayer)
            "cn.toside.music.mobile" -> initHooks(Toside)
            "com.apple.android.music" -> initHooks(Apple)
            "com.luna.music" -> initHooks(Luna)
            "com.xuncorp.qinalt.music" -> initHooks(Qinalt)
            "com.xuncorp.suvine.music", "com.salt.music" -> initHooks(Salt)
            "com.hihonor.cloudmusic" -> initHooks(Netease)
            "cn.aqzscn.stream_music" -> initHooks(Aqzscn)
            "com.lalilu.lmusic" -> initHooks(LMusic)
            "cn.wenyu.bodian" -> initHooks(Bodian)
            "fun.upup.musicfree" -> initHooks(MusicFree)
            "com.mimicry.mymusic" -> initHooks(Mimicry)
            "yos.music.player" -> initHooks(Flamingo)
            "org.kde.kdeconnect_tp" -> initHooks(Kde)
            "com.huawei.music" -> initHooks(Huawei)
            "org.akanework.gramophone" -> initHooks(Gramophone)
            "music.hifistatus" -> initHooks(MusicPlayer)
            "com.heytap.music" -> initHooks(Oppo)
            "com.oppo.music" -> initHooks(Oppo)
            "com.maxmpz.audioplayer" -> initHooks(Poweramp)
            // 本分支对注入进程内加载的每个包都会回调（如 webview），无内嵌 SDK 的包直接跳过，避免无效钩子与日志噪音
            else -> isApi { initHooks(Api) }
        }
    }

    private fun attachPreferences() {
        listOf(DisplayStore, GetterStore).forEach { store ->
            runCatching { store.attach(getRemotePreferences(store.group), false) }
                .onFailure { it.log() }
        }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            try {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                "${moduleRes.getString(R.string.hook_succeeded)}:${it.javaClass.simpleName}".log()
            } catch (e: Exception) {
                "${moduleRes.getString(R.string.hook_failed)}:${it.javaClass.simpleName}".log()
                e.log()
            }
        }
    }

    companion object {
        private const val SYSTEM_UI = "com.android.systemui"
        private val SELF = BuildConfig.APPLICATION_ID
    }
}
