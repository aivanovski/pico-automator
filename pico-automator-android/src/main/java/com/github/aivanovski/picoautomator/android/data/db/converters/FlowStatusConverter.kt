package com.github.aivanovski.picoautomator.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.picoautomator.android.entity.FlowStatus

@ProvidedTypeConverter
class FlowStatusConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowStatus? =
        value?.let { FlowStatus.fromName(value) }

    @TypeConverter
    fun toDatabaseValue(type: FlowStatus?): String? = type?.name
}