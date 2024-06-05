package com.github.aivanovski.picoautomator.android.ui.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.picoautomator.android.ui.data.db.entity.StepInfo

@Dao
interface StepInfoDao {

    @Query("SELECT * FROM step_info")
    fun getAll(): List<StepInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(step: StepInfo): Long

    @Update
    fun update(step: StepInfo)

    @Query("DELETE FROM step_info WHERE uid = :uid")
    fun removeByUid(uid: String)

    @Query("DELETE FROM step_info")
    fun removeAll()
}