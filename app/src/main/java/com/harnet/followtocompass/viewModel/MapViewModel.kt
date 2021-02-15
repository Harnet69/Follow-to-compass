package com.harnet.followtocompass.viewModel

import android.app.Activity
import android.app.Application
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.harnet.followtocompass.model.MyLocation
import com.harnet.followtocompass.model.di.DaggerMyLocationComponent
import javax.inject.Inject

class MapViewModel(application: Application): BaseViewModel(application) {
    @Inject
    lateinit var myLocation: MyLocation

    val mUserCoords = MutableLiveData<LatLng>()

    init {
        DaggerMyLocationComponent.create().inject(this)
    }

    fun refreshUsersCoords(activity: Activity){
        getUserCoordinates(activity)
    }

    // get user coordinated constantly
    private fun getUserCoordinates(activity: Activity){
        val locationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location?) {
                location?.let {
                    mUserCoords.value = LatLng(it.latitude, it.longitude)
                }
            }
        }

        myLocation.getLocation(activity, locationResult)
    }
}