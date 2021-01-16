/*
package com.example.famreen.application.adapters

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.R
import com.example.famreen.application.custom.predicate.RVPredicate
import com.example.famreen.application.custom.recycler_view.DetailsLookup
import com.example.famreen.application.custom.recycler_view.SelectionObserver
import com.example.famreen.application.custom.recycler_view.SelectionTracker
import com.example.famreen.application.interfaces.ItemHelper
import com.example.famreen.application.interfaces.ViewHolderDetails
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.databinding.FragmentNoteBinding
import com.example.famreen.databinding.ItemNoteListBinding

class DiaryAdapter constructor(private var items: MutableList<NoteItem>,
                               private val mBinding: FragmentNoteBinding,
                               private val diaryRoomRepository: DiaryRoomRepository) : RecyclerView.Adapter<DiaryAdapter.DiaryHolder>(), ItemHelper {
    private val tag = DiaryAdapter::class.java.name
    @ColorInt private var defBackgroundSwipeColorDark = 0xFF21242C
    @ColorInt private var defBackgroundSwipeColorLight = 0xFFF5F5F5
    private var defSwipeDrawableId = R.drawable.img_delete
    private val horizontalMargin =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, mBinding.rvNote.context.resources.displayMetrics).toInt()
    private var ST: SelectionTracker<NoteItem>? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DiaryHolder {
        val li = LayoutInflater.from(mBinding.rvNote.context)
        val binding: ItemNoteListBinding = DataBindingUtil.inflate(li, R.layout.item_note_list, viewGroup, false)
        return DiaryHolder(binding)
    }

    override fun onBindViewHolder(diaryHolder: DiaryHolder, i: Int) {
        val item: NoteItem = items[i]
        if (diaryHolder.detailsTrue is DiaryHolder.Details) {
            if(ST?.isSelected(diaryHolder.detailsTrue.getKey()) as Boolean){
                Logger.d(tag,"onBind: true","rv")
                diaryHolder.bind(item,true)
            }else{
                Logger.d(tag,"onBind: false","rv")
                diaryHolder.bind(item,false)
            }
        }
    }
    override fun getItemCount(): Int {
        return items.size }
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean { return false }
    override fun onItemDismiss(position: Int) {
        diaryRoomRepository.deleteNote(items[position])
        items.removeAt(position)
        ST?.clear()
    }
    fun setItems(items: MutableList<NoteItem>){
        this.items = items
        notifyDataSetChanged()
    }

    fun initSelectionTracker(){
        ST = SelectionTracker(mBinding.rvNote, DL())
        ST?.addObserver(object : SelectionObserver{
            override fun onItemStateChanged(key: Int, isSelected: Boolean) {
                Logger.d(tag,"key - "+key + ", isSelected - "+ isSelected,"rv")
                notifyItemChanged(key)
            }

            override fun onCounterChanged(counter: Int) {
                Logger.d(tag,"count - " + counter,"rv")
            }

            override fun onFullyCleared(isCleared: Boolean) {
                notifyDataSetChanged()
            }

            override fun onSelectionChanged() {

            }

        })
    }
    inner class DiaryHolder constructor(private val mSingleBinding: ItemNoteListBinding) : RecyclerView.ViewHolder(mSingleBinding.root) {
        private val details = Details()
        fun bind(item: NoteItem,isSelected: Boolean) {
            mSingleBinding.item = item
            if(isSelected){
                itemView.setBackgroundResource(R.drawable.selector_list_item_selected)
            }else{
                itemView.setBackgroundResource(R.drawable.selector_list_item)
            }
            if (item.important) {
                mSingleBinding.ibIsImportant.setImageResource(R.drawable.img_star)
            } else {
                mSingleBinding.ibIsImportant.setImageResource(R.drawable.img_star_empty)
            }
        }
        inner class Details : DetailsLookup.ItemDetails<NoteItem>(){
            override fun getKey(): Int {
               return adapterPosition
            }
            override fun getValue(): NoteItem {
                return items[adapterPosition]
            }

        }

        val detailsTrue: DetailsLookup.ItemDetails<NoteItem>
            get() = details
    }

    //////////////////////////
    private inner class DL : DetailsLookup<NoteItem>(){
        override fun getItemDetails(e: MotionEvent): ItemDetails<NoteItem>? {
            val view = mBinding.rvNote.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvNote.getChildViewHolder(view)
                if (holder is DiaryHolder) return holder.detailsTrue
            }
            return null
        }
    }
    //////////////////////////

    inner class TouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(0, swipeFlags)
        }
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            this@DiaryAdapter.onItemDismiss(viewHolder.adapterPosition)
        }
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if(dX<0){
                c.clipRect(viewHolder.itemView.right +  dX.toInt(), viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
                val theme = AppPreferences.getProvider()!!.readTheme()
                val colorDrawable: ColorDrawable
                colorDrawable = when (theme) {
                    AppCompatDelegate.MODE_NIGHT_YES -> {
                        ColorDrawable(defBackgroundSwipeColorDark.toInt())
                    }
                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        ColorDrawable(defBackgroundSwipeColorLight.toInt())
                    }
                    else -> return
                }
                colorDrawable.setBounds(viewHolder.itemView.right + dX.toInt(), viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
                colorDrawable.draw(c)
                var iconSize = 0
                var imgLeft = viewHolder.itemView.right
                val icon = ContextCompat.getDrawable(recyclerView.context,defSwipeDrawableId)
                if(icon != null){
                    iconSize = icon.intrinsicHeight
                    val half = iconSize/2
                    val top = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - half)
                    imgLeft = viewHolder.itemView.right - horizontalMargin - half * 2
                    icon.setBounds(imgLeft, top, viewHolder.itemView.right - horizontalMargin, top + icon.intrinsicHeight)
                    icon.draw(c)
                }

            }
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }
}






package com.example.famreen.application.adapters

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.R
import com.example.famreen.application.custom.predicate.RVPredicate
import com.example.famreen.application.custom.recycler_view.DetailsLookup
import com.example.famreen.application.interfaces.ItemHelper
import com.example.famreen.application.interfaces.ViewHolderDetails
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.databinding.FragmentNoteBinding
import com.example.famreen.databinding.ItemNoteListBinding

class DiaryAdapter constructor(private var items: MutableList<NoteItem>,
                               private val mBinding: FragmentNoteBinding,
                               private val diaryRoomRepository: DiaryRoomRepository) : RecyclerView.Adapter<DiaryAdapter.DiaryHolder>(), ItemHelper {
    private val tag = DiaryAdapter::class.java.name
    @ColorInt private var defBackgroundSwipeColorDark = 0xFF21242C
    @ColorInt private var defBackgroundSwipeColorLight = 0xFFF5F5F5
    private var defSwipeDrawableId = R.drawable.img_delete
    private val horizontalMargin =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, mBinding.rvNote.context.resources.displayMetrics).toInt()
    private var selectionTracker: SelectionTracker<NoteItem>? = null
    private var ST: com.example.famreen.application.custom.recycler_view.SelectionTracker<NoteItem>? = null
    private var isDelete = false

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DiaryHolder {
        val li = LayoutInflater.from(mBinding.rvNote.context)
        val binding: ItemNoteListBinding = DataBindingUtil.inflate(li, R.layout.item_note_list, viewGroup, false)
        return DiaryHolder(binding)
    }

    override fun onBindViewHolder(diaryHolder: DiaryHolder, i: Int) {
        val item: NoteItem = items[i]
        if (diaryHolder.itemDetails is DiaryHolder.DiaryDetails) {
            if(ST?.isSelected(diaryHolder.detailsTrue.getKey()) as Boolean){
                Logger.d(tag,"onBind: true","rv")
                diaryHolder.bind(item,true)
            }else{
                Logger.d(tag,"onBind: false","rv")
                diaryHolder.bind(item,false)
            }
        }
    }
    override fun getItemCount(): Int {
        return items.size }
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean { return false }
    override fun onItemDismiss(position: Int) {
        diaryRoomRepository.deleteNote(items[position])
        isDelete = true
        selectionTracker?.clearSelection()
    }
    fun setItems(items: MutableList<NoteItem>){
        this.items = items
        notifyDataSetChanged()
    }

    fun getSelectionTracker(): SelectionTracker<NoteItem>?{return selectionTracker}
    fun initSelectionTracker(){
        val tracerId = tag
        selectionTracker = SelectionTracker.Builder(
            tracerId,
            mBinding.rvNote,
            DiaryKeyProvider(),
            DetailsLookups(),
            StorageStrategy.createParcelableStorage(NoteItem::class.java))
            .withSelectionPredicate(RVPredicate())
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

            override fun onSelectionRefresh() {
                super.onSelectionRefresh()
                Logger.d(tag,"onRefresh()",null)
            }

            override fun onSelectionRestored() {
                super.onSelectionRestored()
                Logger.d(tag,"onRestored()",null)
            }
            override fun onItemStateChanged(key: NoteItem, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                Logger.d(tag,"item - "+items.indexOf(key) + " selected - " + selected,null)
                if(isDelete){
                    Logger.d(tag,"item - "+items.indexOf(key) + " delete - " + selected,null)
                    if(selected) selectionTracker?.deselect(key)
                    items.remove(key)
                    isDelete = false
                    notifyDataSetChanged()
                }
            }

        })
        ST = com.example.famreen.application.custom.recycler_view.SelectionTracker(mBinding.rvNote, DL())
    }
    inner class DiaryHolder constructor(private val mSingleBinding: ItemNoteListBinding) : RecyclerView.ViewHolder(mSingleBinding.root),
        ViewHolderDetails<NoteItem> {
        private val mDiaryDetails = DiaryDetails()
        private val details = Details()
        fun bind(item: NoteItem,isSelected: Boolean) {
            mSingleBinding.item = item
            if(isSelected){
                itemView.setBackgroundResource(R.drawable.selector_list_item_selected)
            }else{
                itemView.setBackgroundResource(R.drawable.selector_list_item)
            }
            if (item.important) {
                mSingleBinding.ibIsImportant.setImageResource(R.drawable.img_star)
            } else {
                mSingleBinding.ibIsImportant.setImageResource(R.drawable.img_star_empty)
            }
        }
        inner class DiaryDetails : ItemDetailsLookup.ItemDetails<NoteItem>() {
            override fun getPosition(): Int { return adapterPosition}
            override fun getSelectionKey(): NoteItem? { return items[adapterPosition] }
        }
        inner class Details : DetailsLookup.ItemDetails<NoteItem>(){
            override fun getKey(): Int {
                return adapterPosition
            }

            override fun getItem(): NoteItem {
                return items[adapterPosition]
            }

        }
        override val itemDetails: ItemDetailsLookup.ItemDetails<NoteItem>
            get() = mDiaryDetails

        val detailsTrue: DetailsLookup.ItemDetails<NoteItem>
            get() = details
    }

    private inner class DetailsLookups : ItemDetailsLookup<NoteItem>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<NoteItem>? {
            val view = mBinding.rvNote.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvNote.getChildViewHolder(view)
                if (holder is DiaryHolder) return holder.itemDetails
            }
            return null
        }
    }
    //////////////////////////
    private inner class DL : DetailsLookup<NoteItem>(){
        override fun getItemDetails(e: MotionEvent): ItemDetails<NoteItem>? {
            val view = mBinding.rvNote.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvNote.getChildViewHolder(view)
                if (holder is DiaryHolder) return holder.detailsTrue
            }
            return null
        }
    }
    //////////////////////////

    private inner class DiaryKeyProvider : ItemKeyProvider<NoteItem>(SCOPE_MAPPED) {
        override fun getKey(position: Int): NoteItem? {
            return if (mBinding.rvNote.adapter != null) {
                items[position]
            } else null }
        override fun getPosition(key: NoteItem): Int { return items.indexOf(key) }
    }

    inner class TouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(0, swipeFlags)
        }
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            this@DiaryAdapter.onItemDismiss(viewHolder.adapterPosition)
        }
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if(dX<0){
                c.clipRect(viewHolder.itemView.right +  dX.toInt(), viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
                val theme = AppPreferences.getProvider()!!.readTheme()
                val colorDrawable: ColorDrawable
                colorDrawable = when (theme) {
                    AppCompatDelegate.MODE_NIGHT_YES -> {
                        ColorDrawable(defBackgroundSwipeColorDark.toInt())
                    }
                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        ColorDrawable(defBackgroundSwipeColorLight.toInt())
                    }
                    else -> return
                }
                colorDrawable.setBounds(viewHolder.itemView.right + dX.toInt(), viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
                colorDrawable.draw(c)
                var iconSize = 0
                var imgLeft = viewHolder.itemView.right
                val icon = ContextCompat.getDrawable(recyclerView.context,defSwipeDrawableId)
                if(icon != null){
                    iconSize = icon.intrinsicHeight
                    val half = iconSize/2
                    val top = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - half)
                    imgLeft = viewHolder.itemView.right - horizontalMargin - half * 2
                    icon.setBounds(imgLeft, top, viewHolder.itemView.right - horizontalMargin, top + icon.intrinsicHeight)
                    icon.draw(c)
                }

            }
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }
}
0  - 0
1+ 1 1+
2  - 2+
-+ 3 -

onNotifyChanged()
HashMap<Item,Int>
reHash() on OnBind()
*/
