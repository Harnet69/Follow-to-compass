package com.harnet.followtocompass.model.di

import com.harnet.followtocompass.model.MyLocation
import com.harnet.followtocompass.viewModel.CompassViewModel
import com.harnet.followtocompass.viewModel.MapViewModel
import dagger.Component
import javax.inject.Singleton

@Component
@Singleton
interface MyLocationComponent {
    fun getMyLocation(): MyLocation

    fun inject(mapsViewModel: MapViewModel)
    fun inject(compassViewModel: CompassViewModel)
}