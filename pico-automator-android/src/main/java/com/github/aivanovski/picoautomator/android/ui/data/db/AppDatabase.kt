package com.github.aivanovski.picoautomator.android.ui.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.aivanovski.picoautomator.android.ui.data.db.converters.FlowStepDtoTypeConverter
import com.github.aivanovski.picoautomator.android.ui.data.db.dao.StepInfoDao
import com.github.aivanovski.picoautomator.android.ui.data.db.entity.StepInfo
import com.squareup.moshi.Moshi

@Database(
    entities = [
        StepInfo::class
    ],
    version = 1
)
@TypeConverters(
    FlowStepDtoTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract val flowStepDao: StepInfoDao

    companion object {

        fun buildDatabase(
            context: Context,
            moshi: Moshi
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "pico-automator.db"
            )
                .addTypeConverter(FlowStepDtoTypeConverter(moshi))
                .build()
        }
    }
}