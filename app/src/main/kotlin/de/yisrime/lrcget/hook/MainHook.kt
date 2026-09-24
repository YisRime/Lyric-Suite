package de.yisrime.lrcget.hook

import de.yisrime.lyricsuite.BuildConfig
import de.yisrime.lrcget.config.ConfigStore
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
import cn.xiaowine.xkt.LogTool
import cn.xiaowine.xkt.LogTool.log
import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

class MainHook : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        EzXposed.initOnModuleLoaded(this, param)
        EzXposed.initModuleResources()
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        EzXposed.initOnPackageLoaded(param)
        EzXReflection.init(param.defaultClassLoader)
        attachPreferences()
        LogTool.init("Lyrics Getter", { true }, false)
        "${frameworkName} ${frameworkVersion}(${frameworkVersionCode}) api=$apiVersion".log()
        when (param.packageName) {
            "com.android.systemui" -> initHooks(SystemUi)
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
            else -> initHooks(Api)
        }
    }

    private fun attachPreferences() {
        runCatching {
            ConfigStore.attach(getRemotePreferences(ConfigStore.GROUP), false)
        }.onFailure { "Remote preferences unavailable: ${it.message}".log() }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            try {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                "Inited hook: ${it.javaClass.name}, Package Name: ${EzXposed.hookedPackageName}".log()
            } catch (e: Exception) {
                e.printStackTrace()
                "Init hook ${it.javaClass.name} failed".log()
            }
        }
    }
}
