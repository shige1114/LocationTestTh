package com.kawailab.locationtestth.database


import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey @ColumnInfo(name = "id") val id:Long,
    val latitude: Double,
    val longitude: Double,
){
    override fun toString(): String {
       return "$id,$latitude,$longitude"
    }
}
