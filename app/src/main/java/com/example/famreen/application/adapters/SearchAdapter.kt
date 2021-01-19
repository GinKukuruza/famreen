package com.example.famreen.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.R
import com.example.famreen.application.items.SearchItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.utils.observers.UpdateObserver
import com.example.famreen.databinding.ItemSearchListBinding

class SearchAdapter(private val context: Context,
                    private val observer: UpdateObserver,
                    private val mSearchItems: List<SearchItem>) : RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SearchHolder {
        val layoutInflater = LayoutInflater.from(context)
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
            mSingleBinding.ivSearchItemImage.setImageDrawable(item.image)
            mSingleBinding.item = item
        }
        init {
            itemView.setOnClickListener {
                AppPreferences.getProvider()!!.writeSearchPackageBrowserName(mSingleBinding.item?.packageName)
                AppPreferences.getProvider()!!.writeSearchBrowserName(mSingleBinding.item?.name)
                observer.update()
            }
        }
    }
}