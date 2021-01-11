package com.example.famreen.application.fragments

import android.content.Intent
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
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.RegisterViewModel
import com.example.famreen.databinding.FragmentRegisterEmailBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.set
import javax.inject.Inject

class RegistrationFragment : Fragment(){
    @Inject lateinit var viewModel: RegisterViewModel
    private val imgReq = 0
    private lateinit var navController: NavController
    private lateinit var mBinding: FragmentRegisterEmailBinding
    private var mImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentRegisterEmailBinding.inflate(inflater)
        mBinding.btRegisterSignUp.setOnClickListener {
            val name = mBinding.etRegisterEmailName.text.toString()
            val email = mBinding.etRegisterEmailEmail.text.toString()
            val password = mBinding.etRegisterEmailPassword.text.toString()
            viewModel.signUp(email,password,name,mImageUri?.toString())
        }
        /*mBinding.ibRegisterEmailImage.setOnClickListener {
            imageRequest()
        }*/
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> {
                    mBinding.etRegisterEmailName.setText("")
                    mBinding.etRegisterEmailPassword.setText("")
                    mBinding.etRegisterEmailEmail.setText("")
                }
                is States.LoadingState -> {

                }
                is States.ErrorState -> {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       /* if (requestCode == imgReq && resultCode == Activity.RESULT_OK) {
            if(data == null) viewModel.state.set(States.ErrorState("Ошибка при загрузке"))
            val uri = data!!.data
            Picasso.get().load(uri).centerCrop().fit().into(mBinding.ibRegisterEmailImage)
            mImageUri = uri
        }*/
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }

   /* private fun imageRequest(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, imgReq)
    }*/
    */
}