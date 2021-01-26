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
import com.example.famreen.application.interfaces.ItemHelper
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.databinding.FragmentTranslateBinding
import com.example.famreen.databinding.ItemTranslateListBinding
import javax.inject.Inject

class TranslateAdapter(private var mItems: MutableList<TranslateItem>,
                       private val mBinding: FragmentTranslateBinding) : RecyclerView.Adapter<TranslateAdapter.TranslateHolder>(), ItemHelper {
    @ColorInt private var mDefBackgroundSwipeColorDark = 0xFF21242C
    @ColorInt private var mDefBackgroundSwipeColorLight = 0xFFF5F5F5

    private var mDefSwipeDrawableId = R.drawable.img_delete
    private val mHorizontalMargin =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, App.getAppContext().resources.displayMetrics).toInt()
    //ui
    private var mSelectionTracker: SelectionTracker<TranslateItem>? = null
    @Inject lateinit var mTranslateRoomRepositoryImpl: TranslateRoomRepository

    init {
        App.appComponent.inject(this@TranslateAdapter)
    }
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TranslateHolder {
            val li = LayoutInflater.from(viewGroup.context)
            val binding: ItemTranslateListBinding = DataBindingUtil.inflate(li, R.layout.item_translate_list, viewGroup, false)
            return TranslateHolder(binding)
        }
    override fun onBindViewHolder(translateHolder: TranslateHolder, i: Int) {
        val item: TranslateItem = mItems[i]
        if (translateHolder.getItemDetails() is TranslateHolder.Details) {
            if (mSelectionTracker?.isSelected(translateHolder.getItemDetails().getKey()) as Boolean) {
                translateHolder.bind(item, true)
            } else {
                translateHolder.bind(item, false)
            }
        }
    }
    override fun getItemCount(): Int { return mItems.size }

    //Holder
    inner class TranslateHolder(private val mSingleBinding: ItemTranslateListBinding) : RecyclerView.ViewHolder(mSingleBinding.root) {
        private val mDetails = Details()
        fun bind(item: TranslateItem, isSelected: Boolean) {
            mSingleBinding.item = item
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.selector_list_item_selected)
            } else {
                itemView.setBackgroundResource(R.drawable.selector_list_item)
            }
        }
        inner class Details : DetailsLookup.ItemDetails<TranslateItem>(){
            override fun getKey(): Int {
                return adapterPosition
            }
            override fun getValue(): TranslateItem {
                return mItems[adapterPosition]
            }

        }
        fun getItemDetails(): DetailsLookup.ItemDetails<TranslateItem> = mDetails
        }
        override fun onItemDismiss(position: Int) {
            mTranslateRoomRepositoryImpl.deleteTranslate(mItems[position])
            mItems.removeAt(position)
            mSelectionTracker?.clear()
        }
    //DetailsLookup
    private inner class TranslateLookup : DetailsLookup<TranslateItem>(){
        override fun getItemDetails(e: MotionEvent): ItemDetails<TranslateItem>? {
            val view = mBinding.rvTranslate.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvTranslate.getChildViewHolder(view)
                if (holder is TranslateHolder) return holder.getItemDetails()
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
            this@TranslateAdapter.onItemDismiss(viewHolder.adapterPosition)
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
    fun setItems(items: MutableList<TranslateItem>){
        mItems = items
        notifyDataSetChanged()
    }
    /**
     * Инициализирует трекер. Функция должна быть вызвана каждый раз при обновлении view(фрагмента)
     * @param(rv: RecyclerView) - каждый раз передается recycler view обновленного view(фрагмента)
     * **/
    fun initSelectionTracker(rv: RecyclerView){
        mSelectionTracker = SelectionTracker(rv, TranslateLookup())
        mSelectionTracker?.addObserver(object : SelectionObserver {
            override fun onItemStateChanged(key: Int, isSelected: Boolean) {
            }
            override fun onCounterChanged(counter: Int) {
            }
            override fun onFullyCleared(isCleared: Boolean) {
                notifyDataSetChanged()
            }
            override fun onSelectionChanged() {
            }

        })
    }

}