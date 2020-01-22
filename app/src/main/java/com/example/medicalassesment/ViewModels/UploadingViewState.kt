package com.example.medicalassesment.ViewModels

import com.example.medicalassesment.models.TemplateModel

sealed class UploadingViewState
data class ErrorUploading(val message: String?) : UploadingViewState()
data class Uploading(val model:String) : UploadingViewState()
data class UploadingDone(val templateModel: TemplateModel) : UploadingViewState()