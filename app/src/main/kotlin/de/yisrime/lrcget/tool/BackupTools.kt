package de.yisrime.lrcget.tool

import android.app.Activity
import android.content.SharedPreferences
import android.net.Uri
import cn.xiaowine.xkt.AcTool.showToast
import de.yisrime.lyricsuite.config.DisplayStore
import de.yisrime.lyricsuite.config.GetterStore
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter

object BackupTools {
    private val stores = listOf(DisplayStore, GetterStore)

    fun handleReadDocument(activity: Activity, data: Uri): Boolean {
        return try {
            val text = activity.contentResolver.openInputStream(data)?.bufferedReader()?.use { it.readText() }
            if (text.isNullOrEmpty()) {
                "load fail".showToast()
                return false
            }
            if (DisplayStore.source == null && GetterStore.source == null) {
                "load fail\n存储未挂载".showToast()
                return false
            }
            val root = JSONObject(text)
            var restored = 0
            stores.forEach { store ->
                val group = root.optJSONObject(store.group) ?: return@forEach
                store.source?.let { restoreInto(it, group) }
                restored += 1
            }
            if (restored == 0) {
                GetterStore.source?.let { restoreInto(it, root) }
            }
            "load ok".showToast()
            true
        } catch (e: Exception) {
            "load fail\n${e.message}".showToast()
            false
        }
    }

    private fun restoreInto(sp: SharedPreferences, group: JSONObject) {
        val edit = sp.edit()
        val key = group.keys()
        while (key.hasNext()) {
            val name = key.next()
            when (val value = group.get(name)) {
                is String -> {
                    if (value.startsWith("Float:")) {
                        edit.putFloat(name, value.substring("Float:".length).toFloat() / 1000)
                    } else {
                        edit.putString(name, value)
                    }
                }

                is Boolean -> edit.putBoolean(name, value)
                is Int -> edit.putInt(name, value)
                is Long -> edit.putLong(name, value)
                is Double -> edit.putFloat(name, value.toFloat())
            }
        }
        edit.commit()
    }

    fun handleCreateDocument(activity: Activity, data: Uri) {
        try {
            val root = JSONObject()
            stores.forEach { store ->
                val body = JSONObject()
                store.source?.all?.forEach { (name, value) ->
                    if (value is Float) {
                        body.put(name, "Float:" + (value * 1000).toInt().toString())
                    } else {
                        body.put(name, value)
                    }
                }
                root.put(store.group, body)
            }
            activity.contentResolver.openOutputStream(data)?.let { saveFile ->
                BufferedWriter(OutputStreamWriter(saveFile)).apply {
                    write(root.toString())
                    close()
                }
            }
            "save ok".showToast()
        } catch (e: Exception) {
            "save fail\n${e.message}".showToast()
        }
    }
}
