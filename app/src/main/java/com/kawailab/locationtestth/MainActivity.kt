package com.kawailab.locationtestth

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.kawailab.locationtestth.Services.LocationService
import android.widget.Button
import android.content.Intent
import com.kawailab.locationtestth.database.AppDatabase
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import com.kawailab.locationtestth.Services.GetAccountInfo
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUsageStatsPermission()
        requestPermission()
        requestBackgroundLocationPersmission()
        setContentView(R.layout.activity_main)
        AppDatabase.getInstance(this)
        createNotificationChannel()

        val startButton:Button = findViewById(R.id.startButton)
        val finishButton:Button = findViewById(R.id.finishButton)

        startButton.setOnClickListener {
            val intent = Intent(this,LocationService::class.java)
            startForegroundService(intent)
        }
        finishButton.setOnClickListener {
            val intent = Intent(this,LocationService::class.java)
            stopService(intent)
        }
        val getAccountInfoRequest = PeriodicWorkRequestBuilder<GetAccountInfo>(1,TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueue(getAccountInfoRequest)

    }
    companion object{
        private const val PERMISSION_REQUEST_CODE = 1234
        private const val USAGE_STATS_STR = "android.permission.PACKAGE_STATS"
    }
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            LocationService.CHANNEL_ID,
            "お知らせ",
            NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "お知らせを通知します。"
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun requestPermission(){
        val permissionAccessCoarseLocationApproved =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (! permissionAccessCoarseLocationApproved) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestBackgroundLocationPersmission(){
        val backgroudLocationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (backgroudLocationPermission){
            Log.i("success","permission")
        }else{
            Log.i("fail","request permission")
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                PERMISSION_REQUEST_CODE,
            )
        }
    }

    private fun checkUsageStatsPermission(){
        val aom = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = aom.unsafeCheckOp(AppOpsManager.OPSTR_GET_USAGE_STATS,Process.myUid(),packageName)
        val permission = if (mode == AppOpsManager.MODE_DEFAULT){
            checkPermission(
                USAGE_STATS_STR,
                Process.myPid(),
                Process.myUid()
            )== PackageManager.PERMISSION_GRANTED
        } else mode == AppOpsManager.MODE_ALLOWED

        if (!permission){
            Toast.makeText(
                this,
                "Failed to retrieve app usage statistics. " +
                        "You may need to enable access for this app through " +
                        "Settings > Security > Apps with usage access",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

    }

}