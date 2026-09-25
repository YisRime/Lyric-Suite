/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * Copyright (C) 2026 YisRime
 * https://github.com/Block-Network/StatusBarLyric
 *
 * Portions Copyright (C) 2026 Yis_Rime
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package de.yisrime.lrcbar

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.material.color.DynamicColors
import cn.xiaowine.xkt.AcTool
import cn.xiaowine.xkt.LogTool
import de.yisrime.lyricsuite.config.DisplayStore
import de.yisrime.lyricsuite.config.GetterStore
import de.yisrime.lyricsuite.ui.HookProbe
import de.yisrime.lrcget.tool.ActivityTools
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

class LyricApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        DynamicColors.applyToActivitiesIfAvailable(this)
        AcTool.init(this)
        ActivityTools.application = this
        LogTool.init(GETTER_LOG_TAG, { true }, false)
        HookProbe.start(this)
        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                listOf(DisplayStore, GetterStore).forEach { store ->
                    runCatching { store.attach(service.getRemotePreferences(store.group), true) }
                        .onFailure { Log.e(TAG, "框架服务绑定失败", it) }
                }
                ready = DisplayStore.isAttached && GetterStore.isAttached
                framework = service
                refreshScope()
                activatedChange?.invoke(ready && scoped)
            }

            override fun onServiceDied(service: XposedService) {
                DisplayStore.detach()
                GetterStore.detach()
                framework = null
                ready = false
                scoped = false
                activatedChange?.invoke(false)
                Log.w(TAG, "框架服务已断开")
            }
        })
    }

    companion object {
        var activatedChange: ((Boolean) -> Unit)? = null

        private const val TAG = "StatusBarLyric"
        private const val GETTER_LOG_TAG = "Lyrics Getter"
        private const val SYSTEM_UI = "com.android.systemui"

        lateinit var appContext: Context
            private set

        var ready: Boolean = false
            private set
        var scoped: Boolean = false
            private set

        private var framework: XposedService? = null

        fun refreshScope() {
            val service = framework
            if (service == null) {
                scoped = false
                Log.i(TAG, "激活判定: 框架服务未绑定")
                return
            }
            runCatching {
                scoped = service.getScope().contains(SYSTEM_UI)
            }.onFailure {
                scoped = false
                Log.e(TAG, "作用域查询失败", it)
            }
            Log.i(TAG, "激活判定: 服务绑定=$ready 作用域含SystemUI=$scoped")
            activatedChange?.invoke(ready && scoped)
        }
    }
}
