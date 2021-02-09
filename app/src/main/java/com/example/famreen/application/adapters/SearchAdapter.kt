package com.example.famreen.application.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.R
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.items.SearchItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.databinding.ItemSearchListBinding
import com.example.famreen.states.callback.ItemStates

class SearchAdapter(private val mSearchItems: List<SearchItem>) : RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    private var mUpdater: CallbackListener<SearchItem>? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SearchHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding: ItemSearchListBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_search_list, viewGroup, false)
        return SearchHolder(binding)
    }

    override fun onBindViewHolder(searchHolder: SearchHolder, i: Int) {
        searchHolder.bind(mSearchItems[i])
    }

    override fun getItemCount(): Int {
        return mSearchItems.size
    }

    inner class SearchHolder(private val mSingleBinding: ItemSearchListBinding) : RecyclerView.ViewHolder(mSingleBinding.root) {
        fun bind(item: SearchItem) {
            mSingleBinding.ivSearchItemImage.setImageDrawable(item.mImage)
            mSingleBinding.item = item
        }
        init {
            itemView.setOnClickListener {
                val packageName = mSingleBinding.item?.mPackageName
                val name = mSingleBinding.item?.mName
                AppPreferences.getProvider()!!.writeSearchPackageBrowserName(packageName)
                AppPreferences.getProvider()!!.writeSearchBrowserName(name)
                mUpdater?.let {
                    it.onItem(ItemStates.ItemState(mSingleBinding.item!!))
                    return@setOnClickListener
                }
            }
        }
    }
    /**
     * **/
    fun setUpdateListener(updater: CallbackListener<SearchItem>){
        mUpdater = updater
    }
}