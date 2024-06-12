package com.github.aivanovski.picoautomator.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.picoautomator.android.entity.StepVerificationType
import com.github.aivanovski.picoautomator.domain.newapi.entity.FlowStep

@Entity("step_entry")
data class StepEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("index")
    val index: Int,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("flow_uid")
    val flowUid: String,

    @ColumnInfo("next_uid")
    val nextUid: String?,

    @ColumnInfo("command")
    val command: FlowStep,

    @ColumnInfo("step_verification_type")
    val stepVerificationType: StepVerificationType
)