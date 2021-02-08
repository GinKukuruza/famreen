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
import androidx.recyclerview.widget.RecyclerView
import com.example.colorpickerlib.lib.ColorPickerDialog
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.TranslateAdapter
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.viewmodels.TranslateViewModel
import com.example.famreen.databinding.FragmentTranslateBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import com.example.famreen.application.interfaces.ItemListener
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TranslateFragment : Fragment() {
    private val mTag = TranslateFragment::class.java.name
    //ui
    @Inject lateinit var mTranslateRoomRepositoryImpl: TranslateRoomRepository
    @Inject lateinit var mViewModel: TranslateViewModel
    private var mTranslateAdapter: TranslateAdapter? = null
    private lateinit var mBinding: FragmentTranslateBinding
    //subjects
    private lateinit var mTranslateFromSubject: PublishSubject<String>
    private lateinit var mTranslateToSubject: PublishSubject<String>
    private lateinit var mTranslateDescSubject: PublishSubject<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentTranslateBinding.inflate(inflater,container as ViewGroup)
        mTranslateFromSubject = PublishSubject.create()
        mTranslateToSubject = PublishSubject.create()
        mTranslateDescSubject = PublishSubject.create()
        mBinding.rvTranslate.layoutManager = LinearLayoutManager(context)
        mBinding.fabTranslateBack.setOnClickListener {  //mTranslateAdapter?.getSelectionTracker()?.clearSelection() }
        }
        mBinding.fabTranslateAdd.setOnClickListener {
            /*val selection = mTranslateAdapter?.getSelectionTracker()?.selection
            if(selection != null)
                viewModel.addPickedTranslates(selection)
            mTranslateAdapter?.getSelectionTracker()?.clearSelection()*/
        }
        mBinding.ivTranslateTextSize.setOnClickListener {
            val size = AppPreferences.getProvider()!!.readTranslateTextSize()
            val dialogTextSizeFragment = DialogTextSizeFragment(size,object : ItemListener<Int> {
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeTranslateTextSize(item)
                    mTranslateAdapter?.notifyDataSetChanged()
                }

                override fun onFailure(msg: String) {}
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
                mTranslateFromSubject.onNext(s.toString())
            }
        })
        mBinding.etTranslateDescSort.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mTranslateDescSubject.onNext(s.toString())
            }
        })
        mBinding.etTranslateToLangSort.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mTranslateToSubject.onNext(s.toString())
            }
        })
        mBinding.ibTranslateDeleteAll.setOnClickListener {
            mViewModel.deleteAllTranslates()
            mViewModel.getState().set(States.SuccessState<TranslateItem>(null))
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
            val dialog = DialogTextFontFragment(size,object : ItemListener<Int> {
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeTranslateTextFont(item)
                    mTranslateAdapter?.notifyDataSetChanged()
                }

                override fun onFailure(msg: String) {}

            })
            dialog.show(requireActivity().supportFragmentManager, "dialogTextFont")
        }
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        mViewModel.getState().observe(viewLifecycleOwner, {
            when(it){
                is States.DefaultState -> { }
                is States.LoadingState -> {
                    mBinding.loadingTranslate.smoothToShow()
                }
                is States.ErrorState -> {
                    mBinding.loadingTranslate.smoothToHide()
                    Toast.makeText(requireContext(),it.msg, Toast.LENGTH_LONG).show()
                }
                is States.SuccessState<*> ->{
                    mBinding.loadingTranslate.smoothToHide()
                    @Suppress("UNCHECKED_CAST")
                    updateAdapter(it.list as List<TranslateItem>)
                }
                is States.UserState<*> ->{
                    updateUI(it.user)
                }
            }
        })
        mViewModel.getTranslates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@TranslateFragment)
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clear()
    }
    override fun onDestroy() {
        super.onDestroy()
        mViewModel.release()
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }

    private fun updateAdapter(items: List<TranslateItem>?) {
        var list = items
        if(list == null) list = ArrayList()
        if(mTranslateAdapter == null){
            Logger.d(mTag,"init adapter, listsize - " + list.size,null)
            mTranslateAdapter = TranslateAdapter(list as MutableList<TranslateItem>, mBinding)
            prepareAdapter(mBinding.rvTranslate)
            mBinding.rvTranslate.adapter = mTranslateAdapter
        }else{
            mTranslateAdapter!!.setItems(list as MutableList<TranslateItem>)
            prepareAdapter(mBinding.rvTranslate)
            mBinding.rvTranslate.adapter = mTranslateAdapter
        }


    }
    private fun prepareAdapter(rv: RecyclerView){
        mTranslateAdapter?.let {
            val callback: ItemTouchHelper.Callback = mTranslateAdapter!!.TouchHelperCallback()
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(mBinding.rvTranslate)
            mTranslateAdapter!!.initSelectionTracker(rv)
        }
    }
    private fun init(){
        //Translate from subject
        mViewModel.addDisposable(mTranslateFromSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            AppPreferences.getProvider()!!.writeTranslateSortFromLang(it)
            mViewModel.getTranslates() })
        //Translate to subject
        mViewModel.addDisposable(mTranslateToSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            AppPreferences.getProvider()!!.writeTranslateSortToLang(it)
            mViewModel.getTranslates() })
        //Translate description subject
        mViewModel.addDisposable(mTranslateDescSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            AppPreferences.getProvider()!!.writeTranslateSortDescription(it)
            mViewModel.getTranslates() })
    }
}