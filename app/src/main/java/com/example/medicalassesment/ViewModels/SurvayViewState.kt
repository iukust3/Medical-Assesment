package com.example.medicalassesment.ViewModels

import com.example.medicalassesment.models.TemplateModel

sealed class SurvayViewState
data class NetworkError(val message: String?) : SurvayViewState()
object Loading : SurvayViewState()
data class Success(val templateModel: TemplateModel) : SurvayViewState()