package com.example.medicalassesment.ViewModels

import com.example.medicalassesment.models.TemplateModel

sealed class InspectionViewState
data class onErrorLoad(val message: String?) : InspectionViewState()
data class SuccessLoad(val templateModel: List<TemplateModel>) : InspectionViewState()