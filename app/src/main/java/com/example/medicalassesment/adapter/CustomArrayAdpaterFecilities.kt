package com.example.medicalassesment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.medicalassesment.models.Facility

class CustomArrayAdpaterFecilities(context: Context, var resource: Int, var fecilities: Array<Facility>) :
    ArrayAdapter<Facility>(context, resource, fecilities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view:View = if (convertView == null) {
            LayoutInflater.from(context).inflate(resource, parent, false)
        } else convertView

        var text=view.findViewById<TextView>(android.R.id.text1)
text.text=fecilities[position].name

        return view
    }

    override fun getCount(): Int {
        return fecilities.size
    }
}