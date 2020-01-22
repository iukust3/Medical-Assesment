package com.example.medicalassesment.Fragments

import com.example.medicalassesment.models.QuestionModel

interface QustionAdapertInterface {
    fun processNextClick();
    fun takePicture(questionModel: QuestionModel)
}