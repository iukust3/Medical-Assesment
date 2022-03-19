package com.example.medicalassesment.ViewModels

import com.example.medicalassesment.database.Template_questions
import com.example.medicalassesment.models.TemplateModel

sealed class SurvayViewState
data class NetworkError(val message: String?) : SurvayViewState()
object Loading : SurvayViewState()
data class Success(val templateModel: TemplateModel, val templateQuestions: Template_questions) : SurvayViewState()