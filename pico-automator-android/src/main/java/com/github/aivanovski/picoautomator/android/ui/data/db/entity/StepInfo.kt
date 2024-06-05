package com.github.aivanovski.picoautomator.android.ui.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.picoautomator.android.ui.data.api.entity.FlowStep

@Entity("step_info")
data class StepInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("next_uid")
    val nextUid: String?,

    @ColumnInfo("is_finished")
    val isFinished: Boolean,

    @ColumnInfo("command")
    val command: FlowStep,

    @ColumnInfo("result")
    val result: String?,

    @ColumnInfo("attempt_count")
    val attemptCount: Int
)