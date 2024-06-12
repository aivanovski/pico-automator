package com.github.aivanovski.picoautomator.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry

@Dao
interface StepEntryDao {

    @Query("SELECT * FROM step_entry")
    fun getAll(): List<StepEntry>

    @Query("SELECT * FROM step_entry WHERE flow_uid = :flowUid")
    fun getByFlowUid(flowUid: String): List<StepEntry>

    @Query("SELECT * FROM step_entry WHERE uid = :uid")
    fun getByUid(uid: String): StepEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: StepEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entries: List<StepEntry>)

    @Update
    fun update(step: StepEntry)

    @Query("DELETE FROM step_entry WHERE uid = :uid")
    fun removeByUid(uid: String)

    @Query("DELETE FROM step_entry WHERE flow_uid = :flowUid")
    fun removeByFlowUid(flowUid: String)

    @Query("DELETE FROM step_entry")
    fun removeAll()
}