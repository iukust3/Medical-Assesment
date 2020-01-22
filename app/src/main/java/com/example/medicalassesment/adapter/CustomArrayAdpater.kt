package com.example.medicalassesment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.medicalassesment.models.Catogaries
import com.example.medicalassesment.models.Fecilities
import com.example.medicalassesment.models.StateLga

class CustomArrayAdpater(
    context: Context,
    var resource: Int,
    var states: List<Any>,
    var option: Int
) :
    ArrayAdapter<Any>(context, resource) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View =
            convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        var text = view.findViewById<TextView>(android.R.id.text1)
        when (option) {
            STATE -> {
                val state = states as ArrayList<StateLga.State>
                text.text = state[position].name
            }
            LGA -> {
                val state=states as ArrayList<StateLga.State.Lga>
                text.text = state[position].name
            }
            WARD -> {
                val state=states as ArrayList<StateLga.State.Lga.Ward>
                text.text = state[position].name
            }
            CATOGARY -> {
                val state = states as ArrayList<Catogaries.Category>
                text.text = state[position].name
            }
        }

        return view
    }

    override fun getCount(): Int {
        return states.size
    }

    companion object {
        const val STATE = 0
        const val LGA = 1
        const val WARD = 2
        const val CATOGARY=3

    }
}