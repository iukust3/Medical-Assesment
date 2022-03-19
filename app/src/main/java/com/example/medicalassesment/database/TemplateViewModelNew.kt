package com.example.medicalassesment.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.medicalassesment.models.TemplateModel
import kotlinx.coroutines.launch

class TemplateViewModelNew(application: Application) :
    AndroidViewModel(application) {
    private val medicalDataBase = MedicalDataBase.getInstance(application)

    var templateModels: MutableLiveData<List<TemplateModel>> = MutableLiveData()

    init {
        viewModelScope.launch {
            templateModels.value = medicalDataBase.getDao().getTemplate()
        }
    }

    fun getSavedTempletes(filter: String) {
        viewModelScope.launch {
            templateModels.postValue(
                if (filter.isNullOrEmpty()) {
                    medicalDataBase.getDao().getTemplate()
                } else {
                    var filterchange = "${filter}%";
                    medicalDataBase.getDao().getTemplate(filterchange)
                }
            )
        }
    }

    fun getDeshboardTemplate(filter: String) {
        viewModelScope.launch {
            templateModels.postValue(
                if (filter.isEmpty()) {
                    medicalDataBase.getDao().getDeshboardAllTemplate()
                } else {
                    var filterchange = "${filter}%";
                    medicalDataBase.getDao().getDeshboardTemplate(filterchange)
                }
            )
        }
    }

    fun filterData(status: String, filter: String) {
        viewModelScope.launch {
            templateModels.postValue(
                if (filter.isNullOrEmpty()) {
                    medicalDataBase.getDao().getTemplate(status)
                } else {
                    var filterchange = "${filter}%";
                    Log.e("TAG", " Onchange $filterchange")
                    medicalDataBase.getDao().getTemplate(status, filter)
                }
            )
        }
    }
}