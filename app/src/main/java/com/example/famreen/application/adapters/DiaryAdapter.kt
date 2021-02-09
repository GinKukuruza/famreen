package com.example.famreen.application.adapters

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.custom.recycler_view.DetailsLookup
import com.example.famreen.application.custom.recycler_view.SelectionObserver
import com.example.famreen.application.custom.recycler_view.SelectionTracker
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.ItemHelper
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.databinding.FragmentDiaryBinding
import com.example.famreen.databinding.ItemNoteListBinding
import javax.inject.Inject

class DiaryAdapter(private var mItems: MutableList<NoteItem>,
                   private val mBinding: FragmentDiaryBinding) : RecyclerView.Adapter<DiaryAdapter.DiaryHolder>(), ItemHelper {
    private val mTag = DiaryAdapter::class.java.name

    @ColorInt private var mDefBackgroundSwipeColorDark = 0xFF21242C
    @ColorInt private var mDefBackgroundSwipeColorLight = 0xFFF5F5F5

    private var mDefSwipeDrawableId = R.drawable.img_delete
    private val mHorizontalMargin =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, App.getAppContext().resources.displayMetrics).toInt()
    //ui
    private var mSelectionTracker: SelectionTracker<NoteItem>? = null
    @Inject lateinit var mDiaryRoomRepositoryImpl: DiaryRoomRepository
    init {
        App.appComponent.inject(this@DiaryAdapter)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DiaryHolder {
        val li = LayoutInflater.from(viewGroup.context)
        val binding: ItemNoteListBinding = DataBindingUtil.inflate(li, R.layout.item_note_list, viewGroup, false)
        return DiaryHolder(binding)
    }

    override fun onBindViewHolder(diaryHolder: DiaryHolder, i: Int) {
        val item: NoteItem = mItems[i]
        if (diaryHolder.getItemDetails() is DiaryHolder.Details) {
            if(mSelectionTracker?.isSelected(diaryHolder.getItemDetails().getKey()) as Boolean){
                Logger.d(mTag,"onBind: true","rv")
                diaryHolder.bind(item,true)
            }else{
                Logger.d(mTag,"onBind: false","rv")
                diaryHolder.bind(item,false)
            }
        }
    }
    override fun getItemCount(): Int { return mItems.size }
    override fun onItemDismiss(position: Int) {
        mDiaryRoomRepositoryImpl.deleteNote(mItems[position])
        mItems.removeAt(position)
        mSelectionTracker?.clear()
    }
    //Holder
    inner class DiaryHolder constructor(private val mSingleBinding: ItemNoteListBinding) : RecyclerView.ViewHolder(mSingleBinding.root) {
        private val mDetails = Details()
        fun bind(item: NoteItem,isSelected: Boolean) {
            mSingleBinding.item = item
            if(isSelected){
                itemView.setBackgroundResource(R.drawable.selector_list_item_selected)
            }else{
                itemView.setBackgroundResource(R.drawable.selector_list_item)
            }
            if (item.mImportant) {
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
                return mItems[adapterPosition]
            }

        }
        fun getItemDetails(): DetailsLookup.ItemDetails<NoteItem> = mDetails
    }
    //DiaryLookup
    private inner class DiaryLookup : DetailsLookup<NoteItem>(){
        override fun getItemDetails(e: MotionEvent): ItemDetails<NoteItem>? {
            val view = mBinding.rvNote.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvNote.getChildViewHolder(view)
                if (holder is DiaryHolder) return holder.getItemDetails()
            }
            return null
        }
    }
    //TouchHelperCallback
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
                        ColorDrawable(mDefBackgroundSwipeColorDark.toInt())
                    }
                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        ColorDrawable(mDefBackgroundSwipeColorLight.toInt())
                    }
                    else -> return
                }
                colorDrawable.setBounds(viewHolder.itemView.right + dX.toInt(), viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
                colorDrawable.draw(c)
                val icon = ContextCompat.getDrawable(recyclerView.context,mDefSwipeDrawableId)
                if(icon != null){
                    val iconSize = icon.intrinsicHeight
                    val half = iconSize/2
                    val top = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - half)
                    val imgLeft = viewHolder.itemView.right - mHorizontalMargin - half * 2
                    icon.setBounds(imgLeft, top, viewHolder.itemView.right - mHorizontalMargin, top + icon.intrinsicHeight)
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
    /**
     *
     **/
    fun setItems(items: MutableList<NoteItem>){
        mItems = items
    }
    /**
     * Инициализирует трекер. Функция должна быть вызвана каждый раз при обновлении view(фрагмента)
     * @param(rv: RecyclerView) - каждый раз передается ссылка на recycler view обновленного view(фрагмента)
     * **/
    fun initSelectionTracker(rv: RecyclerView){
        mSelectionTracker = SelectionTracker(rv, DiaryLookup())
        mSelectionTracker?.addObserver(object : SelectionObserver{
            override fun onItemStateChanged(key: Int, isSelected: Boolean) {
                Logger.d(mTag, "key - $key, isSelected - $isSelected","rv")
                Logger.d(mTag, "Item Id: " + getItemId(key)+", key - " + key,"rv")
            }

            override fun onCounterChanged(counter: Int) {
                Logger.d(mTag, "count - $counter","rv")
            }

            override fun onFullyCleared(isCleared: Boolean) {
                notifyDataSetChanged()
            }

            override fun onSelectionChanged() {

            }

        })
    }
}
