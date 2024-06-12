package com.github.aivanovski.picoautomator.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.picoautomator.android.entity.JobStatus

@ProvidedTypeConverter
class JobStatusConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): JobStatus? =
        value?.let { JobStatus.fromName(value) }

    @TypeConverter
    fun toDatabaseValue(status: JobStatus?): String? = status?.name
}