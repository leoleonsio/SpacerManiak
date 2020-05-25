package com.pw.spacermaniak

//import android.support.annotation.NonNull
//import android.support.v4.app.ActivityCompat
//import android.support.v4.content.ContextCompat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import java.util.*
import kotlin.collections.ArrayList

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private var drawerLayout : DrawerLayout? = null
    private var actionBarDrawerToggle : ActionBarDrawerToggle? = null
    private var toolbar : Toolbar? = null
    private var navigationView : NavigationView? = null

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
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
                initializeApp()
            }
        }
    }

    private fun initializeApp(){
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.root_container);
        navigationView = findViewById(R.id.navigation_view)
        navigationView?.setNavigationItemSelectedListener(this)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle?.syncState()

        Navigator.navigateToMap(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout?.closeDrawer(GravityCompat.START)
        when (item.itemId){
            R.id.map -> Navigator.navigateToMap(this)
            R.id.search -> Navigator.navigateToSearch(this)
        }
        return true
    }
}
