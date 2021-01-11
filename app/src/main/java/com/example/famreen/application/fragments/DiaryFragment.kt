package com.example.famreen.application.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.azeesoft.lib.colorpicker.ColorPickerDialog
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.DiaryAdapter
import com.example.famreen.application.adapters.NoteSortAdapter
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.observers.ItemObserver
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.viewmodels.DiaryViewModel
import com.example.famreen.databinding.FragmentNoteBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.set
import javax.inject.Inject


class DiaryFragment : Fragment(){
    @Inject lateinit var diaryRoomRepository: DiaryRoomRepository
    @Inject lateinit var viewModel: DiaryViewModel
    private lateinit var mBinding: FragmentNoteBinding
    private var mNoteAdapter: DiaryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentNoteBinding.inflate(inflater)
        mBinding.rvNote.layoutManager = LinearLayoutManager(activity)
        mBinding.fabNoteDelete.setOnClickListener {
            val selection = mNoteAdapter?.getSelectionTracker()!!.selection
            val items: MutableList<Int> = ArrayList()
            for (item in selection) { items.add(item.id) }
            mNoteAdapter?.getSelectionTracker()!!.clearSelection()
            viewModel.deleteAllNotes(items)
        }
        val noteSortAdapter = NoteSortAdapter(requireContext(), viewModel.getSortAdapterItems())
        mBinding.spinnerNoteSorts.adapter = noteSortAdapter
        mBinding.spinnerNoteSorts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
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
                AppPreferences.getProvider()?.writeNoteSortTitle(s.toString())
                viewModel.getNotes() }
        })
        mBinding.etNoteTagSorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                AppPreferences.getProvider()?.writeNoteSortTag(s.toString())
                viewModel.getNotes() }
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
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog(requireContext(), ColorPickerDialog.DARK_THEME)
            colorPickerDialog.setOnColorPickedListener { color: Int, _: String? ->
                AppPreferences.getProvider()!!.writeNoteTextColor(color)
                viewModel.getNotes()
            }
            colorPickerDialog.show()
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
                    updateAdapter(it.list as List<NoteItem>)
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
        viewModel.getNotes()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@DiaryFragment)
    }
    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }

    private fun updateAdapter(items: List<NoteItem>?) {
        var list = items
        if(list == null) list = ArrayList()
        Log.d("ADAPTER", "list size  in fragment" + items?.size)
        mNoteAdapter = DiaryAdapter(requireContext(),
            list as MutableList<NoteItem>,
            mBinding,diaryRoomRepository)
        mBinding.rvNote.adapter = mNoteAdapter
        val callback: ItemTouchHelper.Callback = mNoteAdapter!!.TouchHelperCallback()
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mBinding.rvNote)
        mNoteAdapter!!.initSelectionTracker()
    }

}