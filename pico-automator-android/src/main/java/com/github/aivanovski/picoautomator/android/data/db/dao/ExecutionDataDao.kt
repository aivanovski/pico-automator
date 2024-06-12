package com.github.aivanovski.picoautomator.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.picoautomator.android.entity.db.ExecutionData

@Dao
interface ExecutionDataDao {

    @Query("SELECT * FROM execution_data WHERE " +
        "job_uid = :jobUid AND flow_uid = :flowUid AND step_uid = :stepUid")
    fun get(jobUid: String, flowUid: String, stepUid: String?): ExecutionData?

    @Insert
    fun insert(entry: ExecutionData)

    @Update
    fun update(entry: ExecutionData)
}