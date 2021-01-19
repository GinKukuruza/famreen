package com.example.famreen.application.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.utils.observers.ItemObserver
import com.example.famreen.application.viewmodels.DialogTextSizeViewModel
import com.example.famreen.databinding.DialogTextSizeBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.extensions.set

class DialogTextSizeFragment(private val currentSize: Int, private val observer: ItemObserver<Int>) : DialogFragment() {
    private val viewModel = DialogTextSizeViewModel()
    private lateinit var mBinding: DialogTextSizeBinding
    private var newFont = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding.sbTextSize.progress = currentSize
        mBinding.tvTextSizeTest.setTextSize(TypedValue.COMPLEX_UNIT_PT, currentSize.toFloat())
        mBinding.sbTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                newFont = progress
                mBinding.tvTextSizeTest.setTextSize(TypedValue.COMPLEX_UNIT_PT, progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        mBinding.btTextSizeAccept.setOnClickListener {
            observer.getItem(newFont)
            this.dismiss()
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogTextSizeBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setView(mBinding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }
}