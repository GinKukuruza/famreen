package com.example.famreen.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.ChangePasswordViewModel
import com.example.famreen.databinding.FragmentChangePasswordBinding
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import javax.inject.Inject

class ChangePasswordFragment : Fragment() {
    @Inject lateinit var mViewModel: ChangePasswordViewModel
    private lateinit var mBinding: FragmentChangePasswordBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@ChangePasswordFragment)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChangePasswordBinding.inflate(inflater,container,false)
        mBinding.btChangePasswordChange.setOnClickListener {
            val currentUser = FirebaseConnection.firebaseAuth?.currentUser
            val email = mBinding.etChangePasswordEmail.text.toString()
            val oldPassword = mBinding.etChangePasswordOldPassword.text.toString()
            val newPassword = mBinding.etChangePasswordNewPassword.text.toString()
            currentUser?.let { mViewModel.changePassword(email,oldPassword, newPassword) }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mViewModel.getState().observe(viewLifecycleOwner, {
            when(it){
                is States.DefaultState -> {
                    mBinding.etChangePasswordNewPassword.setText("")
                    mBinding.etChangePasswordOldPassword.setText("")
                    mBinding.etChangePasswordEmail.setText("")
                    mBinding.loadingChangePassword.smoothToHide()
                }
                is States.LoadingState -> {
                    mBinding.loadingChangePassword.smoothToShow()
                }
                is States.ErrorState -> {
                    mBinding.loadingChangePassword.smoothToHide()
                    Toast.makeText(requireContext(),it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    mBinding.loadingChangePassword.smoothToHide()
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