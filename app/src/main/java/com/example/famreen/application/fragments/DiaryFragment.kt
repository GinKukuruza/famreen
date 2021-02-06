package com.example.famreen.application.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorpickerlib.lib.ColorPickerDialog
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.DiaryAdapter
import com.example.famreen.application.adapters.NoteSortAdapter
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.viewmodels.DiaryViewModel
import com.example.famreen.databinding.FragmentNoteBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.set
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DiaryFragment : Fragment(){
    private val mTag = DiaryFragment::class.java.name
    //ui
    @Inject lateinit var mViewModel: DiaryViewModel
    private lateinit var mBinding: FragmentNoteBinding
    private var mNoteAdapter: DiaryAdapter? = null
    private var mDividerItemDecoration: DividerItemDecoration? = null
    //subjects
    private val mTagSubject = PublishSubject.create<String>()
    private val mTitleSubject = PublishSubject.create<String>()
    private val mDisposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Logger.d(mTag,"onCreateView()",null)
        mBinding = FragmentNoteBinding.inflate(inflater,container as ViewGroup)
        mDividerItemDecoration = DividerItemDecoration(mBinding.rvNote.context, RecyclerView.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.divider_drawable) as Drawable
        drawable.let { (mDividerItemDecoration as DividerItemDecoration).setDrawable(it)
            mBinding.rvNote.addItemDecoration(mDividerItemDecoration as DividerItemDecoration)
        }
        mBinding.rvNote.layoutManager = LinearLayoutManager(activity)
        mBinding.fabNoteDelete.setOnClickListener {
           /* val selection = mNoteAdapter?.getSelectionTracker()!!.selection
            val items: MutableList<Int> = ArrayList()
            for (item in selection) {
                items.add(item.id)
            }
            mNoteAdapter?.getSelectionTracker()!!.clearSelection()
            viewModel.deleteAllNotes(items)*/
        }
        val noteSortAdapter = NoteSortAdapter(requireContext(), mViewModel.getSortAdapterItems())

        mBinding.spinnerNoteSorts.adapter = noteSortAdapter
        mBinding.spinnerNoteSorts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                AppPreferences.getProvider()!!.writeNoteSortType(position)
                mViewModel.getNotes() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.cbNoteSorts.isChecked = AppPreferences.getProvider()!!.readNoteSortIsImportant()
        mBinding.cbNoteSorts.setOnCheckedChangeListener { _: CompoundButton?, it: Boolean ->
            AppPreferences.getProvider()?.writeNoteSortIsImportant(it)
            mViewModel.getNotes()}
        mBinding.etNoteTitleSorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mTitleSubject.onNext(s.toString())
            }
        })
        mBinding.etNoteTagSorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mTagSubject.onNext(s.toString())
            }
        })
        mBinding.btNoteSort.setOnClickListener {
            when (mBinding.tlNoteSort.visibility) {
                View.VISIBLE -> {
                    val hide = AnimationUtils.loadAnimation(requireContext(), R.anim.hide_from_left_to_right)
                    hide.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) { mBinding.tlNoteSort.visibility = View.GONE }
                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    mBinding.llNoteSortFilter.startAnimation(hide)
                }
                View.GONE -> {
                    mBinding.tlNoteSort.visibility = View.VISIBLE
                    val show = AnimationUtils.loadAnimation(requireContext(), R.anim.show_from_right_to_left)
                    mBinding.llNoteSortFilter.startAnimation(show)
                }
                View.INVISIBLE -> { }
            }
        }
        mBinding.ibNoteDeleteAll.findViewById<View>(R.id.ib_note_delete_all).setOnClickListener {
            mViewModel.deleteAllNotes()
            mViewModel.getState().set(States.SuccessState<NoteItem>(null))
        }
        mBinding.ivNoteTextSize.setOnClickListener {
            val size = AppPreferences.getProvider()!!.readNoteTextSize()
            val dialogTextSizeFragment = DialogTextSizeFragment(size,object : ItemObserver<Int>{
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeNoteTextSize(item)
                    mViewModel.getNotes()
                }

                override fun onFailure(msg: String) {}
            })
            dialogTextSizeFragment.show(requireActivity().supportFragmentManager, "dialogTextSize")
        }
        mBinding.ivNoteTextColor.setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog()
            colorPickerDialog.setOnColorPickedListener { color: Int, _: String? ->
                AppPreferences.getProvider()!!.writeNoteTextColor(color)
                mViewModel.getNotes()
            }
            colorPickerDialog.show(requireActivity().supportFragmentManager,"colorpickerdialog")
        }
        mBinding.ivNoteTextStyle.setOnClickListener {
            val dialog = DialogTextFontFragment(AppPreferences.getProvider()!!.readNoteTextFont(),object : ItemObserver<Int>{
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeNoteTextFont(item)
                    mViewModel.getNotes()
                }

                override fun onFailure(msg: String) {}
            })
            dialog.show(requireActivity().supportFragmentManager, "dialogTextFont")
        }
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getNotes()
        mViewModel.getState().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState ->{ }
                is States.LoadingState ->{
                    mBinding.loadingNote.smoothToShow()
                }
                is States.ErrorState ->{
                    mBinding.loadingNote.smoothToHide()
                    Toast.makeText(requireContext(),it.msg, Toast.LENGTH_LONG).show()
                }
                is States.SuccessState<*> ->{
                    mBinding.loadingNote.smoothToHide()
                    @Suppress("UNCHECKED_CAST")
                    if(it.list != null) {
                    updateAdapter(it.list as List<NoteItem>)
                    }
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        App.appComponent.inject(this@DiaryFragment)
    }
    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mViewModel.getNotes()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposables.dispose()
        mViewModel.clear()
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }

    private fun updateAdapter(items: List<NoteItem>?) {
        var list = items
        if(list == null) list = ArrayList()
        if(mNoteAdapter == null){
            Logger.d(mTag,"init adapter, listsize - " + list.size,null)
            mNoteAdapter = DiaryAdapter(list as MutableList<NoteItem>, mBinding)
            prepareAdapter(mBinding.rvNote)
            mBinding.rvNote.adapter = mNoteAdapter

        }else{
            mNoteAdapter!!.setItems(list as MutableList<NoteItem>)
            prepareAdapter(mBinding.rvNote)
            mBinding.rvNote.adapter = mNoteAdapter
        }

    }
    private fun prepareAdapter(rv: RecyclerView){
        mNoteAdapter?.let {
            val callback: ItemTouchHelper.Callback = it.TouchHelperCallback()
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(rv)
            it.initSelectionTracker(mBinding.rvNote)
            //mNoteAdapter?.initSelectionTracker()
        }
    }
    private fun init(){
        //Title Subject
        mDisposables.add(mTitleSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            Logger.d(mTag,"titleSubject",null)
            AppPreferences.getProvider()?.writeNoteSortTitle(it)
            mViewModel.getNotes() })
        //Tag Subject
        mDisposables.add(mTagSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            Logger.d(mTag,"tagSubject",null)
            AppPreferences.getProvider()?.writeNoteSortTag(it)
            mViewModel.getNotes() })
    }

}