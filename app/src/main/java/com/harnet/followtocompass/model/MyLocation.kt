package com.harnet.followtocompass.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyLocation @Inject constructor() {
    private val updTime= 2_000L
    internal lateinit var timer1: Timer
    internal var lm: LocationManager? = null
    internal lateinit var locationResult: LocationResult
    internal var gpsEnabled = false
    internal var networkEnabled = false

    internal var locationListenerGps: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            timer1.cancel()
            locationResult.gotLocation(location)
            lm?.let {
                // implement it if you want to get user location only one time
//                it.removeUpdates(this)
//                it.removeUpdates(locationListenerNetwork)
                Log.i("UpdateLoc", "onLocationChanged: $it")
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    internal var locationListenerNetwork: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            timer1.cancel()
            locationResult.gotLocation(location)
            lm?.let {
//                it.removeUpdates(this)
//                it.removeUpdates(locationListenerGps)
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    fun getLocation(context: Context, result: LocationResult): Boolean {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        Log.i("UpdateLoc", "At first getLocation: ")
        locationResult = result
        if (lm == null)
            lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        //exceptions will be thrown if provider is not permitted.
        try {
            lm?.let { gpsEnabled = it.isProviderEnabled(LocationManager.GPS_PROVIDER) }
        } catch (ex: Exception) {
        }

        try {
            lm?.let { networkEnabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }
        } catch (ex: Exception) {
        }

        //don't start listeners if no provider is enabled
        if (!gpsEnabled && !networkEnabled)
            return false

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) run {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 111
            )
        }

        if (gpsEnabled)
            lm?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                updTime,
                0f,
                locationListenerGps
            )
        if (networkEnabled)
            lm?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                updTime,
                0f,
                locationListenerNetwork
            )
        timer1 = Timer()
        timer1.schedule(GetLastLocation(context), 20000)
        return true
    }

    internal inner class GetLastLocation(var context: Context) : TimerTask() {
        override fun run() {
//            lm?.removeUpdates(locationListenerGps)
//            lm?.removeUpdates(locationListenerNetwork)

            var netLoc: Location? = null
            var gpsLoc: Location? = null

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) run {

                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 111
                )
            }


            if (gpsEnabled)
                gpsLoc = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (networkEnabled)
                netLoc = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            //if there are both values use the latest one
            if (gpsLoc != null && netLoc != null) {
                if (gpsLoc.getTime() > netLoc.getTime())
                    locationResult.gotLocation(gpsLoc)
                else
                    locationResult.gotLocation(netLoc)
                return
            }

            if (gpsLoc != null) {
                locationResult.gotLocation(gpsLoc)
                return
            }
            if (netLoc != null) {
                locationResult.gotLocation(netLoc)
                return
            }
            locationResult.gotLocation(null)
        }
    }

    abstract class LocationResult {
        abstract fun gotLocation(location: Location?)
    }
}