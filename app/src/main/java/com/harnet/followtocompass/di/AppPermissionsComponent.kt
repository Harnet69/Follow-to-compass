package com.harnet.followtocompass.di

import com.harnet.followtocompass.model.AppPermissions
import com.harnet.followtocompass.view.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component
@Singleton
interface AppPermissionsComponent {

    fun getAppPermissions(): AppPermissions

    fun inject(mainActivity: MainActivity)
}