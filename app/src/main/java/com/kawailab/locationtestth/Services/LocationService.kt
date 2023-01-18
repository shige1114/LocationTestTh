package com.kawailab.locationtestth.Services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.kawailab.locationtestth.R
import com.kawailab.locationtestth.database.AppDatabase
import com.kawailab.locationtestth.database.Location


class LocationService:Service() {
    val database  = AppDatabase.getInstance(this)
    companion object{
        const val CHANNEL_ID = "777"
        const val TAG = "LocationService"

    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var updatedCount = 0

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                for (location in p0.locations){
                    updatedCount++
                    Log.d(TAG,"[${updatedCount}],${location.time} ${location.latitude} , ${location.longitude}")

                    suspend {
                        try{
                            Log.i(TAG,"Database Sucess")
                            database.LocationDao().insert(Location(location.time,location.latitude,location.longitude))

                        }catch (ex:Exception){
                            Log.e(TAG,"Database",ex)
                        }
                    }
                }
            }
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("位置情報収集中")
            .setContentText("keep getting your locations")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            .build()

        startForeground(9999,notification)
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
        stopLocationUpdates()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        stopSelf()
    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        val locationRequest = createLocationRequest()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }
    private fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
    private fun createLocationRequest():LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,100000).build()
    }
}