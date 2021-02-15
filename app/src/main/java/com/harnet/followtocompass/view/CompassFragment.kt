package com.harnet.followtocompass.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.harnet.followtocompass.viewModel.CompassViewModel
import com.harnet.followtocompass.R
import com.harnet.followtocompass.databinding.CompassFragmentBinding

class CompassFragment : Fragment() {
    private lateinit var viewModel: CompassViewModel
    private lateinit var dataBinding: CompassFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.compass_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}