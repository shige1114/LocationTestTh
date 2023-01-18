package com.kawailab.locationtestth.di

import android.content.Context
import com.kawailab.locationtestth.database.Location
import com.kawailab.locationtestth.database.AppDatabase
import com.kawailab.locationtestth.database.LocationDao

class DatabaseModule {
    fun provideAppDatabase( context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    fun provideLocationDao(appDatabase: AppDatabase): LocationDao {
        return appDatabase.LocationDao()
    }


}