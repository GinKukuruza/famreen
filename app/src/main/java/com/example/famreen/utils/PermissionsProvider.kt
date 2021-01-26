package com.example.famreen.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class PermissionsProvider {
    companion object{
        const val PERMISSIONS_CODE = 515
        fun checkPermissions(activity: AppCompatActivity){
            val storagePerm = activity.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val alertWindowPerm = activity.checkCallingOrSelfPermission(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
            if (storagePerm == PackageManager.PERMISSION_GRANTED    && alertWindowPerm == PackageManager.PERMISSION_GRANTED ){
                return
            }else{
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.SYSTEM_ALERT_WINDOW),
                    PERMISSIONS_CODE)
            }
        }
    }
}