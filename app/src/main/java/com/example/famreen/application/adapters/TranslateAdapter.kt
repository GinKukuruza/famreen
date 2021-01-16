package com.example.famreen.application.adapters

import android.content.Context
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
import com.example.famreen.application.custom.recycler_view.DetailsLookup
import com.example.famreen.application.custom.recycler_view.SelectionObserver
import com.example.famreen.application.custom.recycler_view.SelectionTracker
import com.example.famreen.application.interfaces.ItemHelper
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.databinding.FragmentTranslateBinding
import com.example.famreen.databinding.ItemTranslateListBinding

class TranslateAdapter(private val context: Context,
                       private var items: MutableList<TranslateItem>,
                       private val mBinding: FragmentTranslateBinding,
                       private val translateRoomRepository: TranslateRoomRepository) : RecyclerView.Adapter<TranslateAdapter.TranslateHolder>(), ItemHelper {
    private var selectionTracker: SelectionTracker<TranslateItem>? = null
    @ColorInt
    private var defBackgroundSwipeColorDark = 0xFF21242C
    @ColorInt
    private var defBackgroundSwipeColorLight = 0xFFF5F5F5
    private var defSwipeDrawableId = R.drawable.img_delete
    private val horizontalMargin =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, mBinding.rvTranslate.context.resources.displayMetrics).toInt()

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TranslateHolder {
            val li = LayoutInflater.from(context)
            val binding: ItemTranslateListBinding = DataBindingUtil.inflate(li, R.layout.item_translate_list, viewGroup, false)
            return TranslateHolder(binding)
        }
    override fun onBindViewHolder(translateHolder: TranslateHolder, i: Int) {
        val item: TranslateItem = items[i]
        if (translateHolder.itemDetails is TranslateHolder.Details) {
            if (selectionTracker?.isSelected(translateHolder.itemDetails.getKey()) as Boolean) {
                translateHolder.bind(item, true)
            } else {
                translateHolder.bind(item, false)
            }
        }
    }
    override fun getItemCount(): Int { return items.size }
    fun initSelectionTracker(rv: RecyclerView){
        selectionTracker = SelectionTracker(rv, TranslateLookup())
        selectionTracker?.addObserver(object : SelectionObserver {
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
    fun setItems(items: List<TranslateItem>){
        this.items = items as MutableList<TranslateItem>
        notifyDataSetChanged()
    }
    //Holder
    inner class TranslateHolder(private val mSingleBinding: ItemTranslateListBinding) : RecyclerView.ViewHolder(mSingleBinding.root) {
        private val details = Details()
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
                return items[adapterPosition]
            }

        }
        val itemDetails: DetailsLookup.ItemDetails<TranslateItem>
            get() = details
        }
        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean { return false }
        override fun onItemDismiss(position: Int) {
            translateRoomRepository.deleteTranslate(items[position])
            items.removeAt(position)
            selectionTracker?.clear()
        }
    //DetailsLookup
    private inner class TranslateLookup : DetailsLookup<TranslateItem>(){
        override fun getItemDetails(e: MotionEvent): ItemDetails<TranslateItem>? {
            val view = mBinding.rvTranslate.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = mBinding.rvTranslate.getChildViewHolder(view)
                if (holder is TranslateHolder) return holder.itemDetails
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