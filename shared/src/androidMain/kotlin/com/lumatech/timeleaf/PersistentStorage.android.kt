package com.lumatech.timeleaf

import android.content.Context
import android.content.SharedPreferences

object AndroidStorage {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("TimeLeafPrefs", Context.MODE_PRIVATE)
    }

    fun getInt(key: String, default: Int): Int = if (::prefs.isInitialized) prefs.getInt(key, default) else default
    fun setInt(key: String, value: Int) { if (::prefs.isInitialized) prefs.edit().putInt(key, value).apply() }
    fun getString(key: String, default: String): String = if (::prefs.isInitialized) prefs.getString(key, default) ?: default else default
    fun setString(key: String, value: String) { if (::prefs.isInitialized) prefs.edit().putString(key, value).apply() }
}

actual object PersistentStorage {
    actual fun getInt(key: String, default: Int): Int = AndroidStorage.getInt(key, default)
    actual fun setInt(key: String, value: Int) = AndroidStorage.setInt(key, value)
    actual fun getString(key: String, default: String): String = AndroidStorage.getString(key, default)
    actual fun setString(key: String, value: String) = AndroidStorage.setString(key, value)
}
