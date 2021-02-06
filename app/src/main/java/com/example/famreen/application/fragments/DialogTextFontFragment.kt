package com.example.famreen.application.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.TextFontAdapter
import com.example.famreen.application.viewmodels.DialogTextFontViewModel
import com.example.famreen.databinding.DialogTextFontBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import com.example.famreen.utils.observers.ItemObserver
import javax.inject.Inject

class DialogTextFontFragment(private val mCurrentFont: Int,private val mObserver: ItemObserver<Int>) : DialogFragment() {
    private var mNewFont: Int = 0

    @Inject
    lateinit var mViewModel: DialogTextFontViewModel
    private lateinit var mBinding: DialogTextFontBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this@DialogTextFontFragment)
        val fontList = mViewModel.getFonts()
        val adapter = TextFontAdapter(requireContext(), fontList)
        mBinding.spinnerDialogTextFont.adapter = adapter
        mBinding.spinnerDialogTextFont.setSelection(mCurrentFont)
        mBinding.spinnerDialogTextFont.onItemSelectedListener = object : AdapterView.OnItemSelectedListener  {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position > fontList.size) return
                mNewFont = adapter.getItem(position)!!.mResFont
                Log.d("test", "id - $id")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.btDialogTextFontAccept.setOnClickListener {
            mObserver.getItem(mNewFont)
            this.dismiss()
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getState().observe(viewLifecycleOwner, {
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogTextFontBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext()).setView(view).create()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }
}