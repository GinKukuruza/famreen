package com.example.famreen.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.AboutAppViewModel
import com.example.famreen.databinding.FragmentAboutAppBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set

class AboutAppFragment : Fragment(){
    private val mViewModel = AboutAppViewModel()
    private lateinit var mBinding: FragmentAboutAppBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this@AboutAppFragment)
        mBinding = FragmentAboutAppBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getState().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> { }
                is States.LoadingState -> { }
                is States.ErrorState -> {
                    Toast.makeText(requireContext(),it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }
    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }
}