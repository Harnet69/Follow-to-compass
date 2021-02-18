package com.harnet.followtocompass.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import com.harnet.followtocompass.viewModel.CompassViewModel
import com.harnet.followtocompass.R
import com.harnet.followtocompass.databinding.CompassFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*

class CompassFragment : Fragment() {
    private lateinit var viewModel: CompassViewModel
    private lateinit var dataBinding: CompassFragmentBinding

    private var goalCoords: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.compass_fragment, container, false)

        return dataBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // check if permission have been granted already
        if (this.context?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.refreshUsersCoords(activity as Activity)
        } else {
            (activity as MainActivity).appPermissions.getPermissionClass(
                "location",
                activity as MainActivity,
                fragment
            )
                ?.checkPermission()
        }

        observeViewModel()

        dataBinding.mapBtn.setOnClickListener {
            Navigation.findNavController(dataBinding.mapBtn)
                .navigate(CompassFragmentDirections.actionCompassFragmentToMapFragment())
        }

        dataBinding.calcDistance.setOnClickListener {
            if (!dataBinding.goalLat.text.equals("") && !dataBinding.goalLng.text.equals("")) {
                try {
                    goalCoords = LatLng(
                        dataBinding.goalLat.text.toString().toDouble(),
                        dataBinding.goalLng.text.toString().toDouble()
                    )
                }catch (e: Exception){
                    Log.i("errorCoords", "Something wrong with coords ${e.localizedMessage}")
                }
                val distance = goalCoords?.let { viewModel.mUserCoords.value?.let { it1 ->
                    viewModel.calcDistance(
                        it1, goalCoords!!
                    )
                }
                }?.let { it2 -> viewModel.roundOffDecimal(it2).toString() }
                dataBinding.distance.text = distance + " km"

            }
        }
    }

    private fun observeViewModel() {
        viewModel.mUserCoords.observe(viewLifecycleOwner, { userCoords ->
            if (userCoords != null && goalCoords != null) {
                val distance =
                    viewModel.roundOffDecimal(viewModel.calcDistance(userCoords, goalCoords!!))
                        .toString()
                dataBinding.distance.text = distance + " km"

            }
        })
    }

    // method is called when activity get a result of user permission decision
    fun onPermissionsResult(permissionGranted: Boolean) {
        if (permissionGranted) {
            Log.i("userCoords", "onPermissionsResult: $permissionGranted")
            viewModel.refreshUsersCoords(activity as Activity)
        }
    }
}