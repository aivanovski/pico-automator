package com.github.aivanovski.picoautomator.android.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.github.aivanovski.picoautomator.android.data.SettingsImpl.Preference.AUTH_TOKEN
import com.github.aivanovski.picoautomator.android.data.SettingsImpl.Preference.START_JOB_UID

class SettingsImpl(
    context: Context
) : Settings {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var startJobUid: String?
        get() = getString(START_JOB_UID)
        set(value) {
            putString(START_JOB_UID, value)
        }

    override var authToken: String?
        get() = getString(AUTH_TOKEN)
        set(value) {
            putString(AUTH_TOKEN, value)
        }

    private fun getBoolean(pref: Preference): Boolean {
        return preferences.getBoolean(pref.key, false)
    }

    private fun getString(pref: Preference): String? {
        return preferences.getString(pref.key, null)
    }

    private fun getInt(pref: Preference): Int {
        return preferences.getInt(pref.key, 0)
    }

    private fun putBoolean(pref: Preference, value: Boolean) {
        putValue {
            putBoolean(pref.key, value)
        }
    }

    private fun putString(pref: Preference, value: String?) {
        putValue {
            putString(pref.key, value)
        }
    }

    private fun putInt(pref: Preference, value: Int) {
        putValue {
            putInt(pref.key, value)
        }
    }

    private inline fun putValue(action: SharedPreferences.Editor.() -> Unit) {
        val editor = preferences.edit()
        action.invoke(editor)
        editor.apply()
    }

    enum class Preference(
        val key: String
    ) {
        START_JOB_UID(key = "startJobUid"),
        AUTH_TOKEN(key = "authToken")
    }
}