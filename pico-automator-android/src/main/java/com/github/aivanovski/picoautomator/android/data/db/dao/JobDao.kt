package com.github.aivanovski.picoautomator.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry

@Dao
interface JobDao {

    @Query("SELECT * FROM job_entry")
    fun getAll(): List<JobEntry>

    @Query("SELECT * FROM job_entry WHERE uid = :uid")
    fun getByUid(uid: String): JobEntry?

    @Insert
    fun insert(job: JobEntry)

    @Update
    fun update(job: JobEntry)

    @Query("DELETE FROM job_entry WHERE uid = :uid")
    fun removeByUid(uid: String)
}