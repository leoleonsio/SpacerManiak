package com.pw.spacermaniak

//import android.support.annotation.NonNull
//import android.support.v4.app.ActivityCompat
//import android.support.v4.content.ContextCompat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import java.util.*
import kotlin.collections.ArrayList
import com.here.android.mpa.common.OnEngineInitListener
import com.pw.spacermaniak.R

import java.io.File.separator

import androidx.core.app.ComponentActivity.ExtraData

import androidx.core.content.ContextCompat.getSystemService
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.MapSettings
import java.io.File


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ASK_PERMISSIONS = 1

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        //setContentView(R.layout.activity_main)
        //Navigator.navigateToMap(this)
    }
    protected fun checkPermissions() {
        val missingPermissions: MutableList<String> = ArrayList()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) { // request all missing permissions
            val permissions = missingPermissions
                    .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                var index = permissions.size - 1
                while (index >= 0) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) { // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                    --index
                }
                // all permissions were granted
                setContentView(R.layout.activity_main)
                Navigator.navigateToMap(this)

            }
        }
    }
}
