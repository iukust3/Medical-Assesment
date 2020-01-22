package com.example.medicalassesment.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.medicalassesment.models.TemplateModel
import kotlinx.coroutines.launch

class TemplateViewModel(application: Application) :
    AndroidViewModel(application) {
    private val medicalDataBase = MedicalDataBase.getInstance(application)

    var templateModels: MutableLiveData<List<TemplateModel>> = MutableLiveData()

    init {
        templateModels.value = medicalDataBase.getDao().getTemplate("InProgress")
    }

    fun filterData(status: String, filter: String) {
        viewModelScope.launch {
            templateModels.postValue(
                if (filter.isNullOrEmpty()) {
                    medicalDataBase.getDao().getTemplate(status)
                } else {
                    var filterchange = "${filter}%";
                    Log.e("TAG", " Onchange $filterchange")
                    medicalDataBase.getDao().getTemplate(status, filterchange)
                }
            )
        }
    }
}