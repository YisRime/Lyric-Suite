package de.yisrime.lyricsuite.config

import android.content.SharedPreferences
import android.util.Log
import de.yisrime.lrcbar.tools.LogTools
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object ConfigStore {
    const val GROUP = "config"

    @Volatile
    private var prefs: SharedPreferences? = null

    @Volatile
    private var writable = false

    val source: SharedPreferences?
        get() = prefs

    val isAttached: Boolean
        get() = prefs != null


    fun attach(source: SharedPreferences, writable: Boolean) {
        prefs = source
        this.writable = writable
    }

    fun detach() {
        prefs = null
        writable = false
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> opt(key: String, default: T): T {
        val sp = prefs ?: return default
        return when (default) {
            is String -> sp.getString(key, default) as T
            is Int -> sp.getInt(key, default) as T
            is Long -> sp.getLong(key, default) as T
            is Boolean -> sp.getBoolean(key, default) as T
            is Float -> sp.getFloat(key, default) as T
            else -> default
        }
    }

    // 同步落盘
    fun put(key: String, value: Any?) {
        val editor = editor()
        if (editor == null) {
            Log.w(LogTools.TAG, "配置写入被丢弃: $key")
            return
        }
        editor.putTyped(key, value)
        editor.commit()
    }

    fun clearAll() {
        editor()?.clear()?.commit()
    }

    private fun editor(): SharedPreferences.Editor? {
        val sp = prefs ?: return null
        if (!writable) return null
        return sp.edit()
    }

    private fun SharedPreferences.Editor.putTyped(key: String, value: Any?) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
        }
    }
}

private class ConfigStoreProperty<T>(private val key: String?, private val default: T) :
    ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        ConfigStore.opt(key ?: property.name, default)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        ConfigStore.put(key ?: property.name, value)
    }
}

fun <T> serialLazy(default: T): ReadWriteProperty<Any, T> = ConfigStoreProperty(null, default)

fun <T> serialLazy(key: String, default: T): ReadWriteProperty<Any, T> =
    ConfigStoreProperty(key, default)
