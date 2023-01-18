package com.kawailab.locationtestth.database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
@Dao
interface LocationDao {

    @Query("SELECT * FROM Locations ORDER BY id")
    fun getLocations(): Flow<List<Location>>

    @Query("SELECT * FROM Locations WHERE id > :startTime and id < :endTime")
    fun getLocationsFocus(startTime:Long,endTime:Long):Flow<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location)
}