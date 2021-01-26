package com.example.famreen.application.fragments

import android.net.Uri
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
import com.example.famreen.application.viewmodels.RegistrationViewModel
import com.example.famreen.databinding.FragmentRegistrationEmailBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import javax.inject.Inject

class RegistrationFragment : Fragment(){
    //TODO добавить поддержку фото
    private var mImageUri: Uri? = null
    //ui
    @Inject lateinit var mViewModel: RegistrationViewModel
    private lateinit var mNavController: NavController
    private lateinit var mBinding: FragmentRegistrationEmailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this@RegistrationFragment)
        mBinding = FragmentRegistrationEmailBinding.inflate(inflater)
        mBinding.btRegisterSignUp.setOnClickListener {
            val name = mBinding.etRegisterEmailName.text.toString()
            val email = mBinding.etRegisterEmailEmail.text.toString()
            val password = mBinding.etRegisterEmailPassword.text.toString()
            mViewModel.signUp(email,password,name,mImageUri?.toString())
        }
        /*mBinding.ibRegisterEmailImage.setOnClickListener {
            imageRequest()
        }*/
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mViewModel.getState().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> {
                    mBinding.loadingRegister.smoothToHide()
                    mBinding.etRegisterEmailName.setText("")
                    mBinding.etRegisterEmailPassword.setText("")
                    mBinding.etRegisterEmailEmail.setText("")
                }
                is States.LoadingState -> {
                    mBinding.loadingRegister.smoothToShow()
                }
                is States.ErrorState -> {
                    mBinding.loadingRegister.smoothToHide()
                    Toast.makeText(requireContext(),it.msg,Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@RegistrationFragment)
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }
    //Код запроса фото
   /* private fun imageRequest(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, imgReq)
    }*/
    */
}