package com.example.famreen.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.MainViewModel
import com.example.famreen.databinding.FragmentMainBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import javax.inject.Inject


class MainFragment : Fragment() {
    //ui
    @Inject
    lateinit var mViewModel: MainViewModel
    private lateinit var mNavController: NavController
    private lateinit var mBinding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@MainFragment)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentMainBinding.inflate(inflater)
        mBinding.clMainDiary.setOnClickListener {  mNavController.navigate(R.id.action_mainFragment_to_diaryFragment) }
        mBinding.clMainSearch.setOnClickListener {  mNavController.navigate(R.id.action_mainFragment_to_searchFragment) }
        mBinding.clMainTranslate.setOnClickListener {  mNavController.navigate(R.id.action_mainFragment_to_translateFragment) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mViewModel.getState().observe(viewLifecycleOwner, {
            when(it){
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