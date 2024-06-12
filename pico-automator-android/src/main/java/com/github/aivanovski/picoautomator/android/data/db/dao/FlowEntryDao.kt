package com.github.aivanovski.picoautomator.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.FlowWithSteps

@Dao
interface FlowEntryDao {

    @Query("SELECT * FROM flow_entry")
    fun getAll(): List<FlowEntry>

    @Query("SELECT * FROM flow_entry WHERE uid = :uid")
    fun getByUid(uid: String): FlowEntry?

    @Transaction
    @Query("SELECT * FROM flow_entry WHERE uid = :uid")
    fun getByUidWithSteps(uid: String): FlowWithSteps?

    @Insert
    fun insert(flow: FlowEntry)

    @Update
    fun update(flow: FlowEntry)

    @Query("DELETE FROM flow_entry WHERE uid = :uid")
    fun removeByUid(uid: String)
}