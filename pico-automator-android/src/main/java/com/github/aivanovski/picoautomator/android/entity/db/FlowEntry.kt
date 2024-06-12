package com.github.aivanovski.picoautomator.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.picoautomator.android.entity.FlowSourceType

@Entity("flow_entry")
data class FlowEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("source_type")
    val sourceType: FlowSourceType,
)