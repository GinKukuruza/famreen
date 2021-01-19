package com.example.famreen.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.ChangePasswordViewModel
import com.example.famreen.databinding.FragmentChangePasswordBinding
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.extensions.set

class ChangePasswordFragment : Fragment() {
    private val viewModel = ChangePasswordViewModel()
    private lateinit var mBinding: FragmentChangePasswordBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChangePasswordBinding.inflate(inflater)
        mBinding.btChangePasswordChange.setOnClickListener {
            val currentUser = FirebaseConnection.firebaseAuth?.currentUser
            val email = mBinding.etChangePasswordEmail.text.toString()
            val oldPassword = mBinding.etChangePasswordOldPassword.text.toString()
            val newPassword = mBinding.etChangePasswordNewPassword.text.toString()
            currentUser?.let { viewModel.changePassword(email,oldPassword, newPassword) }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> {
                    mBinding.etChangePasswordNewPassword.setText("")
                    mBinding.etChangePasswordOldPassword.setText("")
                    mBinding.etChangePasswordEmail.setText("")
                }
                is States.LoadingState -> {

                }
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
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }
}