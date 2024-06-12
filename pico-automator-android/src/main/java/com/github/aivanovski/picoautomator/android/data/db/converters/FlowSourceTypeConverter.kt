package com.github.aivanovski.picoautomator.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.picoautomator.android.entity.FlowSourceType

@ProvidedTypeConverter
class FlowSourceTypeConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowSourceType? =
        value?.let { FlowSourceType.fromName(value) }

    @TypeConverter
    fun toDatabaseValue(type: FlowSourceType?): String? = type?.name
}