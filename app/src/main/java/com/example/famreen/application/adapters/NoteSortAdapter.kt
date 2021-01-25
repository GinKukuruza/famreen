package com.example.famreen.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.famreen.R
import com.example.famreen.application.items.NoteSortItem

class NoteSortAdapter(context: Context, list: List<NoteSortItem>) : ArrayAdapter<Any?>(context, 0, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, view: View?, parent: ViewGroup): View ?{
        var v = view
        if (view == null) v = LayoutInflater.from(context).inflate(R.layout.spinner_note_sort_item, parent, false)
        val imageView = v!!.findViewById<ImageView>(R.id.img_note_sort_item)
        val textView = v.findViewById<TextView>(R.id.tv_note_sort_item)
        val noteSortItem = getItem(position) as NoteSortItem?
        if (noteSortItem != null) imageView.setImageResource(noteSortItem.itemImg)
        textView.text = noteSortItem!!.itemName
        return v
    }
}