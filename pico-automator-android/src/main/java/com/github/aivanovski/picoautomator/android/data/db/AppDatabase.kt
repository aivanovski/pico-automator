package com.github.aivanovski.picoautomator.android.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.aivanovski.picoautomator.android.data.db.converters.FlowSourceTypeConverter
import com.github.aivanovski.picoautomator.android.data.db.converters.FlowStatusConverter
import com.github.aivanovski.picoautomator.android.data.db.converters.FlowStepConverter
import com.github.aivanovski.picoautomator.android.data.db.converters.JobStatusConverter
import com.github.aivanovski.picoautomator.android.data.db.converters.StepVerificationTypeConverter
import com.github.aivanovski.picoautomator.android.data.db.dao.ExecutionDataDao
import com.github.aivanovski.picoautomator.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.picoautomator.android.data.db.dao.JobDao
import com.github.aivanovski.picoautomator.android.data.db.dao.StepEntryDao
import com.github.aivanovski.picoautomator.android.entity.db.ExecutionData
import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry

@Database(
    entities = [
        StepEntry::class,
        FlowEntry::class,
        JobEntry::class,
        ExecutionData::class
    ],
    version = 1
)
@TypeConverters(
    FlowStepConverter::class,
    StepVerificationTypeConverter::class,
    // FlowStatusConverter::class,
    FlowSourceTypeConverter::class,
    JobStatusConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract val stepEntryDao: StepEntryDao
    abstract val flowEntryDao: FlowEntryDao
    abstract val runnerEntryDao: JobDao
    abstract val executionDataDao: ExecutionDataDao

    companion object {

        fun buildDatabase(
            context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "pico-automator.db"
            )
                .addTypeConverter(FlowStepConverter())
                .addTypeConverter(StepVerificationTypeConverter())
                // .addTypeConverter(FlowStatusConverter())
                .addTypeConverter(FlowSourceTypeConverter())
                .addTypeConverter(JobStatusConverter())
                .build()
        }
    }
}