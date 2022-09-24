package com.giocrnj.diary.views

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.json.JSONObject


class BasicAdapter
    (
    activity: Activity,
    private val items: Array<String>,
) :
    ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // convert view
        val viewToReturn = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        //get current string
        val item = items[position]

        //Set text
        val textView: TextView = viewToReturn.findViewById(android.R.id.text1)
        textView.text = item

        //Return View
        return viewToReturn
    }
}