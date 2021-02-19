package com.harnet.followtocompass.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import com.harnet.followtocompass.R
import com.harnet.followtocompass.databinding.CompassFragmentBinding
import com.harnet.followtocompass.model.Compass
import com.harnet.followtocompass.model.SOTWFormatter
import com.harnet.followtocompass.viewModel.CompassViewModel
import kotlinx.android.synthetic.main.activity_main.*

class CompassFragment : Fragment() {
    private lateinit var viewModel: CompassViewModel
    private lateinit var dataBinding: CompassFragmentBinding
    private lateinit var compass: Compass

    private var currentAzimuth: Float = 0.0f
    //TODO Inject it
    private lateinit var sotwFormatter: SOTWFormatter

    private var goalCoords: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.compass_fragment, container, false)

        sotwFormatter = SOTWFormatter(context)

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

        setupCompass()

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

    override fun onStart() {
        super.onStart()
        Log.d("errorCoords", "start compass")
        compass.start()
    }

    override fun onPause() {
        super.onPause()
        compass.stop()
    }

    override fun onResume() {
        super.onResume()
        compass.start()
    }

    override fun onStop() {
        super.onStop()
        Log.d("errorCoords", "stop compass")
        compass.stop()
    }

    private fun setupCompass(){
        compass = Compass(context)
//        val cl: Compass.CompassListener = getCompassListener()
//        compass.setListener(cl)
    }

    private fun adjustArrow(azimuth: Float) {
        Log.d(
            "Compasss azimut",
            "will set rotation from " + currentAzimuth + " to "
                    + azimuth
        )
        val an: Animation = RotateAnimation(
            -currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        dataBinding.mainImageHands.startAnimation(an)
    }

    private fun adjustSotwLabel(azimuth: Float) {
        dataBinding.sotwLabel.text = sotwFormatter.format(azimuth)
    }

//    private fun getCompassListener(): Compass.CompassListener {
//        return Compass.CompassListener {
//            fun onNewAzimuth(azimuth: Float) {
//                // UI updates only in UI thread
//                // https://stackoverflow.com/q/11140285/444966
////                runOnUiThread(Runnable {
//                    adjustArrow(azimuth)
//                    adjustSotwLabel(azimuth)
////                })
//            }
//        }
//    }



    // method is called when activity get a result of user permission decision
    fun onPermissionsResult(permissionGranted: Boolean) {
        if (permissionGranted) {
            Log.i("userCoords", "onPermissionsResult: $permissionGranted")
            viewModel.refreshUsersCoords(activity as Activity)
        }
    }
}