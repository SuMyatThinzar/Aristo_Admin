package com.aristo.admin.utils.components

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "SharedPreferencesForCategory"
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveCategoryId(categoryId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("categoryId", categoryId)
        editor.apply()
    }

    fun getCategoryId(): String? {
        return sharedPreferences.getString("categoryId", null)
    }

    fun removeCategoryId() {
        val editor = sharedPreferences.edit()
        editor.remove("categoryId")
        editor.apply()
    }


    fun saveMainIndex(index: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("position", index)
        editor.apply()
    }

    fun getMainIndex(): Int {
        return sharedPreferences.getInt("position", 0)
    }

    // Function to remove categoryId from SharedPreferences
    // Function to remove position from SharedPreferences
    fun removeMainIndex() {
        val editor = sharedPreferences.edit()
        editor.remove("position")
        editor.apply()
    }

}
