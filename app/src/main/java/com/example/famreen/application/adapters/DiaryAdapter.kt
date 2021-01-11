package com.example.famreen.application.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.R
import com.example.famreen.application.interfaces.ItemHelper
import com.example.famreen.application.interfaces.ViewHolderDetails
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.databinding.FragmentNoteBinding
import com.example.famreen.databinding.ItemNoteListBinding

class DiaryAdapter constructor(private val context: Context,
                               private val items: MutableList<NoteItem>,
                               private val mBinding: FragmentNoteBinding,
                               private val diaryRoomRepository: DiaryRoomRepository) : RecyclerView.Adapter<DiaryAdapter.NoteHolder>(), ItemHelper {

    private var selectionTracker: SelectionTracker<NoteItem>? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoteHolder {
        val li = LayoutInflater.from(context)
        val binding: ItemNoteListBinding = DataBindingUtil.inflate(li, R.layout.item_note_list, viewGroup, false)
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(noteHolder: NoteHolder, i: Int) {
        val item: NoteItem = items[i]
        if (noteHolder.itemDetails is DiaryDetails) {
            (noteHolder.itemDetails as DiaryDetails).position = i
            if (selectionTracker?.isSelected((noteHolder.itemDetails as DiaryDetails).selectionKey) as Boolean) {
                noteHolder.bind(item, true)
            } else {
                noteHolder.bind(item, false)
            }
        }
    }
    override fun getItemCount(): Int { return items.size }
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean { return false }
    override fun onItemDismiss(position: Int) {
        Log.d("ADAPTER", "list size  before delete" + items.size)
        selectionTracker?.deselect(items[position])
        diaryRoomRepository.deleteNote(items[position])
        items.removeAt(position)
        notifyItemRemoved(position)
        Log.d("ADAPTER", "list size  after delete" + items.size)
    }
    fun getSelectionTracker(): SelectionTracker<NoteItem>?{return selectionTracker}
    fun initSelectionTracker(){
        val tracerId = "NoteItem.class"
        selectionTracker = SelectionTracker.Builder(
            tracerId,
            mBinding.rvNote,
            DiaryKeyProvider(),
            DetailsLookup(),
            StorageStrategy.createParcelableStorage(NoteItem::class.java))
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        selectionTracker!!.addObserver(object : SelectionTracker.SelectionObserver<NoteItem>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (selectionTracker!!.hasSelection()) {
                    mBinding.fabNoteDelete.visibility = View.VISIBLE
                } else {
                    mBinding.fabNoteDelete.visibility = View.INVISIBLE
                }
            }
        })
    }
    inner class NoteHolder constructor(private val mSingleBinding: ItemNoteListBinding) : RecyclerView.ViewHolder(mSingleBinding.root),
        ViewHolderDetails<NoteItem?> {
        private val mDiaryDetails = DiaryDetails()
        fun bind(item: NoteItem, isSelected: Boolean) {
            mSingleBinding.item = item
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.selector_list_item_selected)
            } else {
                itemView.setBackgroundResource(R.drawable.selector_list_item)
            }
            if (item.important) {
                mSingleBinding.ibIsImportant.setImageResource(R.drawable.img_star)
            } else {
                mSingleBinding.ibIsImportant.setImageResource(R.drawable.img_star_empty)
            }
        }

        override val itemDetails: ItemDetailsLookup.ItemDetails<NoteItem?>?
            get() = mDiaryDetails

    }

    private inner class DetailsLookup : ItemDetailsLookup<NoteItem?>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<NoteItem?>? {
            val view = mBinding.rvNote.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvNote.getChildViewHolder(view)
                if (holder is NoteHolder) return holder.itemDetails
            }
            return null
        }
    }

    private inner class DiaryKeyProvider : ItemKeyProvider<NoteItem>(SCOPE_MAPPED) {
        override fun getKey(position: Int): NoteItem? {
            return if (mBinding.rvNote.adapter != null) {
                items[position]
            } else null }
        override fun getPosition(key: NoteItem): Int { return items.indexOf(key) }
    }

    private inner class DiaryDetails : ItemDetailsLookup.ItemDetails<NoteItem?>() {
        private var mPosition = 0
        fun setPosition(position: Int) { mPosition = position }
        override fun getPosition(): Int { return mPosition }
        override fun getSelectionKey(): NoteItem? { return items[mPosition] }
    }

    inner class TouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(0, swipeFlags)
        }
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val item = (viewHolder as NoteHolder).itemDetails?.selectionKey as NoteItem
            if(selectionTracker?.isSelected(item) as Boolean)
                selectionTracker?.deselect(item)
            this@DiaryAdapter.onItemDismiss(viewHolder.adapterPosition)
        }
    }
}