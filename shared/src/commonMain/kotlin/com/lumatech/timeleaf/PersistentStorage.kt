package com.lumatech.timeleaf

expect object PersistentStorage {
    fun getInt(key: String, default: Int): Int
    fun setInt(key: String, value: Int)
    fun getString(key: String, default: String): String
    fun setString(key: String, value: String)
}
