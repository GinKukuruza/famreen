package com.example.famreen.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.famreen.R
import com.example.famreen.application.items.ScreenSpinnerTranslateItem

class ScreenSpinnerTranslateAdapter(context: Context, items: List<*>?) : ArrayAdapter<Any?>(context, 0, items as List) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView,parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView,parent)
    }

    private fun initView(position: Int, convertView: View?,parent: ViewGroup): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_screen_translate_item, parent, false)
        }
        val textView = view!!.findViewById<TextView>(R.id.tv_translate_lang)
        val item = getItem(position) as ScreenSpinnerTranslateItem?
        if (item != null) textView.text = item.langName
        return view
    }
}