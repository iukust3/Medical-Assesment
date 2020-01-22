package com.example.medicalassesment.Fragments

import android.widget.LinearLayout
import com.example.medicalassesment.models.BaseQustion

interface FragmentInterction {
    fun onNextClick(index:Int)
    fun takePicture(questionModel: BaseQustion,linearLayout: LinearLayout)
}