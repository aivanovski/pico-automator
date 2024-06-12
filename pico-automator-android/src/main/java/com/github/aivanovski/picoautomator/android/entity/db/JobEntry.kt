package com.github.aivanovski.picoautomator.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.picoautomator.android.entity.OnFinishAction
import com.github.aivanovski.picoautomator.android.entity.JobStatus

@Entity("job_entry")
data class JobEntry(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("flow_uid")
    val flowUid: String,

    @ColumnInfo("current_step_uid")
    val currentStepUid: String,

    @ColumnInfo("added_timestamp")
    val addedTimestamp: Long,

    @ColumnInfo("status")
    val status: JobStatus,

    @ColumnInfo("on_finish_action")
    val onFinishAction: OnFinishAction
)