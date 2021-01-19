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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorpickerlib.lib.ColorPickerDialog
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.DiaryAdapter
import com.example.famreen.application.adapters.NoteSortAdapter
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.utils.observers.ItemObserver
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.viewmodels.DiaryViewModel
import com.example.famreen.databinding.FragmentNoteBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.extensions.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DiaryFragment : Fragment(){
    @Inject lateinit var diaryRoomRepository: DiaryRoomRepository
    @Inject lateinit var viewModel: DiaryViewModel
    private val _tag = DiaryFragment::class.java.name
    private lateinit var mBinding: FragmentNoteBinding
    private var mNoteAdapter: DiaryAdapter? = null
    private var dividerItemDecoration: DividerItemDecoration? = null
    private val tagSubject = PublishSubject.create<String>()
    private val titleSubject = PublishSubject.create<String>()
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Logger.d(_tag,"onCreateView()",null)
        mBinding = FragmentNoteBinding.inflate(inflater)
        dividerItemDecoration = DividerItemDecoration(mBinding.rvNote.context, RecyclerView.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.divider_drawable) as Drawable
        drawable.let { (dividerItemDecoration as DividerItemDecoration).setDrawable(it)
            mBinding.rvNote.addItemDecoration(dividerItemDecoration as DividerItemDecoration)
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
        val noteSortAdapter = NoteSortAdapter(requireContext(), viewModel.getSortAdapterItems())

        mBinding.spinnerNoteSorts.adapter = noteSortAdapter
        mBinding.spinnerNoteSorts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                AppPreferences.getProvider()!!.writeNoteSortType(position)
                viewModel.getNotes() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.cbNoteSorts.isChecked = AppPreferences.getProvider()!!.readNoteSortIsImportant()
        mBinding.cbNoteSorts.setOnCheckedChangeListener { _: CompoundButton?, it: Boolean ->
            AppPreferences.getProvider()?.writeNoteSortIsImportant(it)
            viewModel.getNotes()}
        mBinding.etNoteTitleSorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                titleSubject.onNext(s.toString())
            }
        })
        mBinding.etNoteTagSorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                tagSubject.onNext(s.toString())
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
            viewModel.deleteAllNotes()
            viewModel.state.set(States.SuccessState<NoteItem>(null))
        }
        mBinding.ivNoteTextSize.setOnClickListener {
            val size = AppPreferences.getProvider()!!.readNoteTextSize()
            val dialogTextSizeFragment = DialogTextSizeFragment(size,object : ItemObserver<Int>{
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeNoteTextSize(item)
                    viewModel.getNotes()
                }
            })
            dialogTextSizeFragment.show(requireActivity().supportFragmentManager, "dialogTextSize")
        }
        mBinding.ivNoteTextColor.setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog()
            colorPickerDialog.setOnColorPickedListener { color: Int, _: String? ->
                AppPreferences.getProvider()!!.writeNoteTextColor(color)
                viewModel.getNotes()
            }
            colorPickerDialog.show(requireActivity().supportFragmentManager,"colorpickerdialog")
        }
        mBinding.ivNoteTextStyle.setOnClickListener {
            val dialog = DialogTextFontFragment(AppPreferences.getProvider()!!.readNoteTextFont(),object : ItemObserver<Int>{
                override fun getItem(item: Int) {
                    AppPreferences.getProvider()!!.writeNoteTextFont(item)
                    viewModel.getNotes()
                }
            })
            dialog.show(requireActivity().supportFragmentManager, "dialogTextFont")
        }
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNotes()
        viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState ->{

                }
                is States.LoadingState ->{

                }
                is States.ErrorState ->{

                }
                is States.SuccessState<*> ->{
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
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.getNotes()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        viewModel.clear()
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }

    private fun updateAdapter(items: List<NoteItem>?) {
        var list = items
        if(list == null) list = ArrayList()
        if(mNoteAdapter == null){
            Logger.d(_tag,"init adapter, listsize - " + list.size,null)
            mNoteAdapter = DiaryAdapter(list as MutableList<NoteItem>, mBinding,diaryRoomRepository)
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
        disposables.add(titleSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            Logger.d(_tag,"titleSubject",null)
            AppPreferences.getProvider()?.writeNoteSortTitle(it)
            viewModel.getNotes() })
        //Tag Subject
        disposables.add(tagSubject.debounce(600, TimeUnit.MILLISECONDS).subscribe {
            Logger.d(_tag,"tagSubject",null)
            AppPreferences.getProvider()?.writeNoteSortTag(it)
            viewModel.getNotes() })
    }

}