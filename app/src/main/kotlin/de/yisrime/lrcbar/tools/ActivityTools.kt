/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * Copyright (C) 2026 YisRime
 * https://github.com/Block-Network/StatusBarLyric
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

package de.yisrime.lrcbar.tools

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import de.yisrime.lrcbar.LyricApplication
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun restartApp() {
        val intent = LyricApplication.appContext.packageManager.getLaunchIntentForPackage(LyricApplication.appContext.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        LyricApplication.appContext.startActivity(intent)
        exitProcess(0)
    }
}