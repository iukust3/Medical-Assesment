package com.example.medicalassesment.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.database.MedicalDataBase
import kotlinx.coroutines.launch

class TempleteViewModels : ViewModel() {
    private val templateModelLiveData: MutableLiveData<List<TemplateModel>> = MutableLiveData()

    fun getTempleteData(context: Context, status: String) {
        viewModelScope.launch {
            templateModelLiveData.value =
                MedicalDataBase.getInstance(context).getDao().getTemplate(status)
        }
    }
}