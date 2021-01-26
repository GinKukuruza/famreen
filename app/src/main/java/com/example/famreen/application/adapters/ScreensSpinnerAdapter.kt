package com.example.famreen.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.famreen.R
import com.example.famreen.application.items.ScreensSpinnerItem

class ScreensSpinnerAdapter(context: Context, items: List<ScreensSpinnerItem>) : ArrayAdapter<Any>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_screens_item, parent, false)
        }
        val imageView = view!!.findViewById<ImageView>(R.id.spinner_screens_image)
        val item = getItem(position) as ScreensSpinnerItem?
        if (item != null) imageView?.setImageResource(item.mImage)
        return view
    }
}