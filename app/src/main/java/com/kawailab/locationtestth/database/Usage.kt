package com.kawailab.locationtestth.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "usages")
data class Usage(
    @PrimaryKey @ColumnInfo(name = "id") val id:Long,
    val name: String,
    val type: Int,
){
    override fun toString(): String {
        return "$id,$name,$type"
    }

}