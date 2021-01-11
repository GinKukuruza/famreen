package com.example.famreen.application.adapters

import android.content.Context
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
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.databinding.FragmentTranslateBinding
import com.example.famreen.databinding.ItemTranslateListBinding

class TranslateAdapter(private val context: Context,
                       private val items: MutableList<TranslateItem>,
                       private val mBinding: FragmentTranslateBinding,
                       private val translateRoomRepository: TranslateRoomRepository) : RecyclerView.Adapter<TranslateAdapter.TranslateHolder>(), ItemHelper {
        private var selectionTracker: SelectionTracker<TranslateItem>? = null

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TranslateHolder {
            val li = LayoutInflater.from(context)
            val binding: ItemTranslateListBinding = DataBindingUtil.inflate(li, R.layout.item_translate_list, viewGroup, false)
            return TranslateHolder(binding)
        }
        override fun onBindViewHolder(translateHolder: TranslateHolder, i: Int) {
            val item: TranslateItem = items[i]
            if (translateHolder.itemDetails is TranslateDetails) {
                (translateHolder.itemDetails as TranslateDetails).setPosition(i.toLong())
                if (selectionTracker!!.isSelected((translateHolder.itemDetails as TranslateDetails).selectionKey)) {
                    translateHolder.bind(item, true)
                } else {
                    translateHolder.bind(item, false)
                }
            }
        }

        override fun getItemCount(): Int { return items.size }
        fun getSelectionTracker(): SelectionTracker<TranslateItem>?{ return selectionTracker }
        fun initSelectionTracker(){
            val tracerId = "ItemTranslate.class"
            selectionTracker = SelectionTracker.Builder(
                tracerId,
                mBinding.rvTranslate,
                TranslateKeyProvider(),
                DetailsLookup(),
                StorageStrategy.createParcelableStorage(TranslateItem::class.java))
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build()
            selectionTracker!!.addObserver(object : SelectionTracker.SelectionObserver<TranslateItem>() {

                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    if (!selectionTracker!!.hasSelection()) {
                        mBinding.fabTranslateBack.visibility = View.INVISIBLE
                        mBinding.fabTranslateAdd.visibility = View.INVISIBLE
                    } else {
                        mBinding.fabTranslateBack.visibility = View.VISIBLE
                        mBinding.fabTranslateAdd.visibility = View.VISIBLE
                    }
                }
            })
        }

    inner class TranslateHolder(private val mSingleBinding: ItemTranslateListBinding) : RecyclerView.ViewHolder(mSingleBinding.root),
            ViewHolderDetails<TranslateItem> {
            private val mTranslateDetails = TranslateDetails()
            fun bind(item: TranslateItem, isSelected: Boolean) {
                mSingleBinding.item = item
                if (isSelected) {
                    itemView.setBackgroundResource(R.drawable.selector_list_item_selected)
                } else {
                    itemView.setBackgroundResource(R.drawable.selector_list_item)
                }
            }
            override val itemDetails: ItemDetailsLookup.ItemDetails<TranslateItem>
                get() = mTranslateDetails
        }
        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean { return false }
        override fun onItemDismiss(position: Int) {
            selectionTracker?.deselect(items[position])
            selectionTracker?.clearSelection()
            translateRoomRepository.deleteTranslate(items[position])
            items.removeAt(position)
            notifyItemRemoved(position)
        }
        inner class TouchHelperCallback : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(0, swipeFlags) }
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = (viewHolder as TranslateHolder).itemDetails.selectionKey as TranslateItem
                if(selectionTracker?.isSelected(item) as Boolean)
                    selectionTracker?.deselect(item)
                this@TranslateAdapter.onItemDismiss(viewHolder.adapterPosition)
            }
        }
        inner class DetailsLookup : ItemDetailsLookup<TranslateItem>() {
            override fun getItemDetails(e: MotionEvent): ItemDetails<TranslateItem>? {
                val view = mBinding.rvTranslate.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val holder = mBinding.rvTranslate.getChildViewHolder(view)
                    if (holder is TranslateHolder) return holder.itemDetails
                }
                return null
            }
        }

        inner class TranslateKeyProvider : ItemKeyProvider<TranslateItem>(SCOPE_MAPPED) {
            override fun getKey(position: Int): TranslateItem? {
                return if (mBinding.rvTranslate.adapter != null) {
                    this@TranslateAdapter.items[position]
                } else return null
            }
            override fun getPosition(key: TranslateItem): Int { return  this@TranslateAdapter.items.indexOf(key) }
        }

        inner class TranslateDetails : ItemDetailsLookup.ItemDetails<TranslateItem>() {
            private var mPosition: Long = 0

            override fun getPosition(): Int { return mPosition.toInt() }
            override fun getSelectionKey(): TranslateItem? { return  this@TranslateAdapter.items[mPosition.toInt()] }
            fun setPosition(position: Long) { mPosition = position }
        }
}