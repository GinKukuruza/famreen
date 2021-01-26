package com.example.famreen.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.example.famreen.R
import com.example.famreen.application.items.TextFontItem
//TODO CLASS SHOULD BE REINSPECTED

class TextFontAdapter(context: Context,list: List<TextFontItem>) : ArrayAdapter<TextFontItem?>(context, 0, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(convertView, position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(convertView, position, parent)
    }

    private fun initView(convertView: View?, position: Int, parent: ViewGroup): View? {
        var view = convertView
        if (convertView == null) { view = LayoutInflater.from(context).inflate(R.layout.spinner_dialog_text_font_item, parent, false) }
        val item = getItem(position)
        val imageView: AppCompatImageView = view!!.findViewById(R.id.iv_dialog_text_font_item)
        val textView: AppCompatTextView = view.findViewById(R.id.tv_dialog_text_font_item)
        if (item != null) {
            imageView.setImageResource(item.mImageFontRes)
            textView.text = item.mFont
        }
        return view
    }
}