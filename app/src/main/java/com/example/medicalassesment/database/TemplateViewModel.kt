package com.example.medicalassesment.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.models.InspectionToolsItem
import com.example.medicalassesment.models.TemplateModel
import kotlinx.coroutines.launch

class TemplateViewModel(application: Application) :
    AndroidViewModel(application) {
    private val medicalDataBase = MedicalDataBase.getInstance(application)

    var templateModels: MutableLiveData<List<TemplateModel>> = MutableLiveData()
    var inspectionToolsItem: MutableLiveData<ArrayList<InspectionToolsItem>> = MutableLiveData()
    var prefHelper=PrefHelper(application)

    init {
        templateModels.value = listOf()
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
    fun getFacilityData(status: String, filter: String) {
        viewModelScope.launch {
           var mlist=
                if (filter.isNullOrEmpty()) {
                    medicalDataBase.getDao().getTemplate(status)
                } else {
                    var filterchange = "${filter}%";
                    Log.e("TAG", " Onchange $filterchange")
                    medicalDataBase.getDao().getTemplate(status, filterchange)
                }
            var categorys = prefHelper.getCatogaries()
            var list=ArrayList<InspectionToolsItem>()
            list.clear()
            categorys.categories.forEach { category ->
                var inspectionToolsItem = InspectionToolsItem();
                inspectionToolsItem.name = category.name;
                inspectionToolsItem.icon= Constant.getIcon(category.name)
                list.add(inspectionToolsItem)
            }
            list.forEach { inspectionItem ->
                var item = InspectionToolsItem.Inspectiontype("Location inspection", ArrayList())
                item.list = mlist.filter { templateModel ->
                    templateModel.category == inspectionItem.name && templateModel.type == "1" && templateModel.status == "New";
                }
                inspectionItem.list.add(item)
                item = InspectionToolsItem.Inspectiontype("Registration inspection", ArrayList())
                item.list = mlist.filter { templateModel ->
                    templateModel.category == inspectionItem.name && templateModel.type == "2" && templateModel.status == "New"
                }
                inspectionItem.list.add(item)
                item = InspectionToolsItem.Inspectiontype("Routine inspection", ArrayList())
                item.list =
                    mlist.filter { templateModel -> templateModel.category == inspectionItem.name && templateModel.type == "3" && templateModel.status == "New" }
                inspectionItem.list.add(item)
                item = InspectionToolsItem.Inspectiontype("Monitoring inspection", ArrayList())
                item.list =
                    mlist.filter { templateModel -> templateModel.category == inspectionItem.name && templateModel.type == "4" && templateModel.status == "New" }
                inspectionItem.list.add(item)
            }

            inspectionToolsItem.postValue(list)
        }
    }


}