package com.example.famreen.application.fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colorpickerlib.lib.ColorPickerDialog
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.TranslateAdapter
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.observers.ItemObserver
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.viewmodels.TranslateViewModel
import com.example.famreen.databinding.FragmentTranslateBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList


class TranslateFragment : Fragment() {
    @Inject lateinit var translateRoomRepository: TranslateRoomRepository
    @Inject lateinit var viewModel: TranslateViewModel
    private var mTranslateAdapter: TranslateAdapter? = null
    private lateinit var mBinding: FragmentTranslateBinding
    private val translateFromSubject = PublishSubject.create<String>()
    private val translateToSubject = PublishSubject.create<String>()
    private val translateDescSubject = PublishSubject.create<String>()
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentTranslateBinding.inflate(inflater)
        mBinding.rvTranslate.layoutManager = LinearLayoutManager(context)
        mBinding.fabTranslateBack.setOnClickListener {  mTranslateAdapter?.getSelectionTracker()?.clearSelection() }
        mBinding.fabTranslateAdd.setOnClickListener {
            val selection = mTranslateAdapter?.getSelectionTracker()?.selection
            if(selection != null)
                viewModel.addPickedTranslates(selection)
            mTranslateAdapter?.getSelectionTracker()?.clearSelection()
        }
        mBinding.ivTranslateTextSize.setOnClickListener {
            val size = AppPreferences.getProvider()!!.readTranslateTextSize()
            val dialogTextSizeFragment = DialogTextSizeFragment(size,object : ItemObserver<Int>{
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeTranslateTextSize(item)
                    mTranslateAdapter?.notifyDataSetChanged()
                }
            })
            dialogTextSizeFragment.show(requireActivity().supportFragmentManager, "dialogTextSize")
        }
        mBinding.ivTranslateTextColor.setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog()
            colorPickerDialog.setOnColorPickedListener { color: Int, _: String? ->
                AppPreferences.getProvider()!!.writeTranslateTextColor(color)
                mTranslateAdapter?.notifyDataSetChanged()
            }
            colorPickerDialog.show(requireActivity().supportFragmentManager,"colorpickerdialog")
        }
        mBinding.etTranslateFromLangSort.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                translateFromSubject.onNext(s.toString())
            }
        })
        mBinding.etTranslateDescSort.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                translateDescSubject.onNext(s.toString())
            }
        })
        mBinding.etTranslateToLangSort.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                translateToSubject.onNext(s.toString())
            }
        })
        mBinding.ibTranslateDeleteAll.setOnClickListener {
            viewModel.deleteAllTranslates()
            viewModel.state.set(States.SuccessState<TranslateItem>(null))
        }
        mBinding.ibTranslateSort.setOnClickListener {
            when (mBinding.tlTranslateSort.visibility) {
                View.VISIBLE -> {
                    val hide = AnimationUtils.loadAnimation(requireContext(), R.anim.hide_from_left_to_right)
                    hide.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            mBinding.tlTranslateSort.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    mBinding.llTranslateSortFilter.startAnimation(hide)
                }
                View.GONE -> {
                    mBinding.tlTranslateSort.visibility = View.VISIBLE
                    val show = AnimationUtils.loadAnimation(requireContext(), R.anim.show_from_right_to_left)
                    mBinding.llTranslateSortFilter.startAnimation(show)
                }
            }
        }
        mBinding.ivTranslateTextStyle.setOnClickListener {
            val size = AppPreferences.getProvider()!!.readTranslateTextFont()
            val dialog = DialogTextFontFragment(size,object : ItemObserver<Int>{
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeTranslateTextFont(item)
                    mTranslateAdapter?.notifyDataSetChanged()
                }

            })
            dialog.show(requireActivity().supportFragmentManager, "dialogTextFont")
        }
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
                is States.SuccessState<*> ->{
                    @Suppress("UNCHECKED_CAST")
                    updateAdapter(it.list as List<TranslateItem>)
                }
                is States.UserState<*> ->{
                    updateUI(it.user)
                }
            }
        })
        viewModel.getTranslates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        App.appComponent.inject(this@TranslateFragment)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        viewModel.clear()
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }

    private fun updateAdapter(items: List<TranslateItem>?) {
        var list = items
        if(list == null) list = ArrayList()
        mTranslateAdapter = TranslateAdapter(requireContext(),
            list as MutableList<TranslateItem>,
            mBinding,translateRoomRepository)
        mBinding.rvTranslate.adapter = mTranslateAdapter
        val callback: ItemTouchHelper.Callback = mTranslateAdapter!!.TouchHelperCallback()
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mBinding.rvTranslate)
        mTranslateAdapter!!.initSelectionTracker()
    }
    private fun init(){
        //Translate from subject
        disposables.add(translateFromSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            AppPreferences.getProvider()!!.writeTranslateSortFromLang(it)
            viewModel.getTranslates() })
        //Translate to subject
        disposables.add(translateToSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            AppPreferences.getProvider()!!.writeTranslateSortToLang(it)
            viewModel.getTranslates() })
        //Translate description subject
        disposables.add(translateDescSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            AppPreferences.getProvider()!!.writeTranslateSortDescription(it)
            viewModel.getTranslates() })
    }
}