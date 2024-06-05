package com.github.aivanovski.picoautomator.android.ui.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SettingsImpl(
    context: Context
) : Settings {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var testRunnerState: Int
        get() = getInt(Preference.TEST_RUNNER_STATE)
        set(value) {
            putInt(Preference.TEST_RUNNER_STATE, value)
        }

    override var currentStepUid: String?
        get() = getString(Preference.CURRENT_STEP_UID)
        set(value) {
            putString(Preference.CURRENT_STEP_UID, value)
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
        TEST_RUNNER_STATE(key = "testRunnerState"),
        CURRENT_STEP_UID(key = "currentStepUid")
    }
}