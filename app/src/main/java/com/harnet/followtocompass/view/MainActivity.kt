package com.harnet.followtocompass.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.harnet.followtocompass.R
import com.harnet.followtocompass.di.DaggerAppPermissionsComponent
import com.harnet.followtocompass.model.AppPermissions
import com.harnet.followtocompass.model.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appPermissions: AppPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // inject appPermissions
        DaggerAppPermissionsComponent.create().inject(this)
    }

    // when user was asked for a permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isNotEmpty()) {
            //switcher of different kinds of permissions
            when (permissions[0]) {
                android.Manifest.permission.ACCESS_FINE_LOCATION -> {
                    when (fragment.childFragmentManager.primaryNavigationFragment) {
                        // request the appropriate fragment
                        is MapFragment ->
                            appPermissions.getPermissionClass(
                                Permissions.LOCATION.permName,
                                this,
                                fragment
                            )
                                ?.onRequestPermissionsResult(
                                    requestCode,
                                    permissions,
                                    grantResults
                                )
                        is CompassFragment ->
                            appPermissions.getPermissionClass(
                                Permissions.LOCATION.permName,
                                this,
                                fragment
                            )
                                ?.onRequestPermissionsResult(
                                    requestCode,
                                    permissions,
                                    grantResults
                                )
                    }
                }
            }
        }
    }
}