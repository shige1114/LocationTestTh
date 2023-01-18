package com.kawailab.locationtestth.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
@Dao
interface UsageDao {

    @Query("SELECT * FROM Usages ORDER BY id")
    fun getUsages(): Flow<List<Usage>>

    @Query("SELECT * FROM Usages WHERE id > :startTime and id < :endTime")
    fun getUsageFocus(startTime:Long,endTime:Long):Flow<List<Usage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usages: List<Usage>)
}