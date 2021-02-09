package com.example.famreen.application.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.DevConnectionViewModel
import com.example.famreen.databinding.FragmentDevConnectionBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import javax.inject.Inject

class DevConnectionFragment : Fragment(){
    @Inject lateinit var mViewModel: DevConnectionViewModel
    private lateinit var mBinding: FragmentDevConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@DevConnectionFragment)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentDevConnectionBinding.inflate(inflater,container,false)
        mBinding.btDevSend.setOnClickListener {
            val send = Intent(Intent.ACTION_SEND)
            val title = mBinding.etDevTitle.text.toString()
            val description = mBinding.etDevDescription.text.toString()
            val intent = mViewModel.createSendIntent(title,description)
            if(intent != null) startActivity(Intent.createChooser(send, "Send Message"))
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getState().observe(viewLifecycleOwner, {
            when(it){
                is States.DefaultState -> {

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
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }
}