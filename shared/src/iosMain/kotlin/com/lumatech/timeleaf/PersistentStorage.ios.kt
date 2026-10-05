package com.lumatech.timeleaf

import platform.Foundation.NSUserDefaults

actual object PersistentStorage {
    actual fun getInt(key: String, default: Int): Int {
        val defaults = NSUserDefaults.standardUserDefaults
        return if (defaults.objectForKey(key) != null) defaults.integerForKey(key).toInt() else default
    }

    actual fun setInt(key: String, value: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), forKey = key)
    }

    actual fun getString(key: String, default: String): String {
        val defaults = NSUserDefaults.standardUserDefaults
        return defaults.stringForKey(key) ?: default
    }

    actual fun setString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, forKey = key)
    }
}
