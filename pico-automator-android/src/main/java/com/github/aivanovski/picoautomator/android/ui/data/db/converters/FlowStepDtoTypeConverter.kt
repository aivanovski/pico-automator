package com.github.aivanovski.picoautomator.android.ui.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.picoautomator.android.ui.data.api.entity.FlowStep
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.squareup.moshi.Moshi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

@ProvidedTypeConverter
class FlowStepDtoTypeConverter(
    moshi: Moshi
) {

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowStep? {
        if (value.isNullOrEmpty()) {
            return null
        }

        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toDatabaseValue(step: FlowStep?): String? {
        if (step == null) {
            return null
        }

        return Json.encodeToString(step)
    }

    private fun ElementReference.toJsonObject(): JSONObject {
        val obj = JSONObject()

        when (this) {
            is ElementReference.Id -> {
                obj.put(KEY_TYPE, TYPE_ID)
                obj.put(KEY_ID, id)
            }

            is ElementReference.Text -> {
                obj.put(KEY_TYPE, TYPE_TEXT)
                obj.put(KEY_TEXT, text)
            }

            is ElementReference.ContainsText -> {
                obj.put(KEY_TYPE, TYPE_CONTAINS_TEXT)
                obj.put(KEY_TEXT, text)
                obj.put(KEY_IGNORE_CASE, ignoreCase)
            }

            is ElementReference.ContentDescription -> {
//                obj.put(KEY_)
            }
        }

        return obj
    }

    private fun JSONObject.toElementReference(): ElementReference? {
        return null
    }

    companion object {
        private const val KEY_TYPE = "type"
        private const val KEY_PACKAGE_NAME = "packageName"
        private const val KEY_STEP_UID = "stepUid"

        private const val KEY_ID = "id"
        private const val KEY_TEXT = "text"
        private const val KEY_IGNORE_CASE = "ignoreCase"
        private const val KEY_CONTENT_DESCRIPTION = "contentDescription"

        // FlowStep types
        private const val TYPE_LAUNCH = "launch"
        private const val TYPE_TAP_ON = "tapOn"
        private const val TYPE_ASSERT_VISIBLE = "assertVisible"
        private const val TYPE_INPUT_TEXT = "inputText"
        private const val TYPE_SEND_BROADCAST = "sendBroadcast"

        // ElementReference types
        private const val TYPE_ID = "id"
        private const val TYPE_TEXT = "text"
        private const val TYPE_CONTAINS_TEXT = "containsText"
        private const val TYPE_CONTENT_DESCRIPTION = "contentDescription"
    }
}