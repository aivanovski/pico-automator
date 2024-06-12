package com.github.aivanovski.picoautomator.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("execution_data")
data class ExecutionData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("job_uid")
    val jobUid: String,

    @ColumnInfo("flow_uid")
    val flowUid: String,

    @ColumnInfo("step_uid")
    val stepUid: String,

    @ColumnInfo("attempt_count")
    val attemptCount: Int,

    @ColumnInfo("result")
    val result: String?
)