package de.yisrime.lrcget.hook.app

import android.media.MediaMetadata
import de.yisrime.lyricsuite.BuildConfig
import cn.lyric.getter.api.data.ExtraData
import de.yisrime.lyricsuite.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools.eventTools
import de.yisrime.lrcget.tool.HookTools.isApi
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNot
import cn.xiaowine.xkt.Tool.isNotNull
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.helper.ObjectHelper.`-Static`.objectHelper
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import de.yisrime.lrcbar.tools.LogTools.log as dlog

object Api : BaseHook() {
    override fun init() {
        super.init()
        hook()
    }

    fun hook(classLoader: ClassLoader? = null) {
        isApi(classLoader) { clazz ->
            clazz.constructorFinder().first().createHook {
                before { hookParam ->
                    val version = hookParam.thisObject.objectHelper().getObjectOrNull("API_VERSION") as Int?
                    version.isNotNull {
                        if (version == BuildConfig.API_VERSION || version == 7 /* 不知名 API 版本 */) {
                            hookParam.thisObject.objectHelper().setObject("hasEnable", true)
                            "Api version: $version".dlog()
                            // SDK 7.0.0 的方法名是 sendMediaData；onMediaData 是旧版宿主的名字，两者有谁挂谁。
                            // 一律 firstOrNull：宿主缺方法时跳过，回调里裸 first 会抛 NoSuchElementException
                            // 中断整个构造器回调，sendLyric/clearLyric 就再也挂不上了。
                            val relayMedia: (Array<Any?>) -> Unit = { args ->
                                val metadata = args[0] as MediaMetadata
                                eventTools.sendMediaData(ExtraData().apply {
                                    this.packageName = packageName
                                    this.mediaMetadata = metadata
                                    this.artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                                    this.album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "Unknown Album"
                                    this.title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
                                })
                            }
                            clazz.methodFinder().firstOrNull { name == "sendMediaData" }.isNotNull {
                                it.createHook {
                                    after { hookParam -> relayMedia(hookParam.args) }
                                }
                                "Api: sendMediaData hooked".dlog()
                            }
                            clazz.methodFinder().firstOrNull { name == "onMediaData" }.isNotNull {
                                it.createHook {
                                    after { hookParam -> relayMedia(hookParam.args) }
                                }
                                "Api: onMediaData hooked".dlog()
                            }
                            clazz.methodFinder().firstOrNull { name == "sendLyric" }.isNotNull {
                                it.createHook {
                                    after { hookParam ->
                                        val extraData = ExtraData()
                                        val extra = hookParam.args.getOrNull(1)?.let {
                                            it.objectHelper().getObjectOrNull("extra")
                                        } as HashMap<String, Any>?
                                        if (extra != null) extraData.mergeExtra(extra)
                                        eventTools.sendLyric(
                                            hookParam.args[0] as String,
                                            extraData
                                        )
                                    }
                                }
                                "Api: sendLyric hooked".dlog()
                            }
                            clazz.methodFinder().firstOrNull { name == "clearLyric" }.isNotNull {
                                it.createHook {
                                    after {
                                        eventTools.cleanLyric()
                                    }
                                }
                                "Api: clearLyric hooked".dlog()
                            }
                        } else {
                            "The APIs do not match".log()
                        }
                    }
                }
            }

        }.isNot {
            "Not found Api class".log()
        }
    }
}