package de.yisrime.lyricsuite.config

import android.content.SharedPreferences
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

    val isEmpty: Boolean
        get() = prefs?.all?.isEmpty() != false

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
            is Double -> sp.getFloat(key, default.toFloat())?.toDouble() as T
            else -> default
        }
    }

    fun put(key: String, value: Any?) {
        val editor = editor() ?: return
        editor.putTyped(key, value)
        editor.apply()
    }

    fun clearAll() {
        editor()?.clear()?.apply()
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

private class ConfigStoreProperty<T>(private val default: T) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        ConfigStore.opt(property.name, default)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        ConfigStore.put(property.name, value)
    }
}

fun <T> serialLazy(default: T): ReadWriteProperty<Any, T> = ConfigStoreProperty(default)
