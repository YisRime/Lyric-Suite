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
                            clazz.methodFinder().first { name == "onMediaData" }.isNotNull {
                                it.createHook {
                                    after { hookParam ->
                                        val metadata = hookParam.args[0] as MediaMetadata

                                        eventTools.sendMediaData(ExtraData().apply {
                                            this.packageName = packageName
                                            this.mediaMetadata = metadata
                                            this.artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                                            this.album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "Unknown Album"
                                            this.title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
                                        })
                                    }
                                }
                            }
                            clazz.methodFinder().first { name == "sendLyric" }.createHook {
                                after { hookParam ->
                                    val extraData = ExtraData()
                                    val extra = hookParam.args[1]!!.objectHelper().getObjectOrNull("extra") as HashMap<String, Any>
                                    extraData.mergeExtra(extra)
                                    eventTools.sendLyric(
                                        hookParam.args[0] as String,
                                        extraData
                                    )
                                }
                            }
                            clazz.methodFinder().first { name == "clearLyric" }.createHook {
                                after {
                                    eventTools.cleanLyric()
                                }
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