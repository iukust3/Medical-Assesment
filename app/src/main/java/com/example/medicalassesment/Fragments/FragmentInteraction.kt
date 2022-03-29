package com.example.medicalassesment.Fragments

import android.widget.LinearLayout
import com.example.medicalassesment.models.BaseQustion

interface FragmentInteraction {
    fun onNextClick(index:Int)
    fun takePicture(questionModel: BaseQustion, position: Int)
    fun onBackClick(index:Int)
}