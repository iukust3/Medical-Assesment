package com.example.medicalassesment.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.database.DatabaseRepository
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.Retrofit.APIinterFace
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class InspectionViewModel @Inject constructor(
    private val apInterFace: APIinterFace,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val inspectionViewState: MutableLiveData<InspectionViewState> = MutableLiveData()
    private val prefHelper = PrefHelper(databaseRepository.application)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        onError(exception)
    }

    private fun onError(exception: Throwable) {
        exception.printStackTrace()

        inspectionViewState.value = onErrorLoad("Error :" + exception.message)
    }

    fun onScreenLoaded() {
        viewModelScope.launch(coroutineExceptionHandler) {
            var list = databaseRepository.medicalDataBase.getDao().getTemplate("New")


            if (list.isNullOrEmpty()) {
                var response =
                    apInterFace.getUserInspection()
                var jsonarray = JSONObject(response.string()).getJSONArray("data")
                for (i in 0 until jsonarray.length()) {
                    var obj = jsonarray.getJSONObject(i)
                    var templateModel = TemplateModel()
                    templateModel.title = obj.getString("title")
                    templateModel.templateId = obj.getInt("id")
                    try {
                        templateModel.type = obj.getString("question_type_id")
                        templateModel.category = obj.getString("category")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    templateModel.status = "New"
                    getTemplateQuestions(templateModel)
                    if (databaseRepository.medicalDataBase.getDao().getTemplate(
                            templateModel.templateId,
                            templateModel.status!!
                        ).isNullOrEmpty()
                    )
                        databaseRepository.medicalDataBase.getDao().insertTamplate(templateModel)
                }
                /*  var respo =
                      apInterFace.getSavedTempletes("getsavedtemplates?status=completed&user_profile_id=" + MyApplication.USER.id.toString())
                  if (respo.isSuccessful) {
                      var jsonarray =
                          JSONObject(respo.body()?.string()).getJSONArray("getsavedtemplates")
                      for (i in 0 until jsonarray.length()) {
                          var obj = jsonarray.getJSONObject(i)
                          var templateModel = TemplateModel()
                          templateModel.title = obj.getString("title")
                          templateModel.templateId = obj.getInt("id")
                          templateModel.status = "saved"
                          templateModel.description = obj.getString("description")
                          templateModel.inspectionConductedOn = obj.getString("inspection_date")
                          templateModel.inspectionConductedBy =
                              obj.getString("inspection_conducted_by")
                          templateModel.inspectionConductedAt =
                              obj.getString("inspection_conducted_at")
                          templateModel.category = obj.getString("category")
                          if (databaseRepository.medicalDataBase.getDao().getTemplate(
                                  templateModel.templateId,
                                  "saved"
                              ).isNullOrEmpty()
                          )
                              databaseRepository.medicalDataBase.getDao().insertTamplate(templateModel)

                      }
                  }*/
                var state = apInterFace.getState()
                if (state.isSuccessful) {
                    state.body()?.string()?.let { prefHelper.setState(it) }
                }
                var fecility = apInterFace.getFecilities()
                if (fecility.isSuccessful) {
                    fecility.body()?.let { prefHelper.setFacility(it) }
                }
                var catogaries = apInterFace.getCatogaries()
                if (catogaries.isSuccessful) {
                    catogaries.body()?.string()?.let { prefHelper.setCategories(it) }
                }
                list = databaseRepository.medicalDataBase.getDao().getTemplate("New")
                inspectionViewState.postValue(SuccessLoad(list))
            } else {
                /* var respo =
                     apInterFace.getSavedTempletes("getsavedtemplates?status=completed&user_profile_id=" + MyApplication.USER.id.toString())
                 if (respo.isSuccessful) {
                     var jsonarray =
                         JSONObject(respo.body()?.string()).getJSONArray("getsavedtemplates")
                     for (i in 0 until jsonarray.length()) {
                         var obj = jsonarray.getJSONObject(i)
                         var templateModel = TemplateModel()
                         templateModel.title = obj.getString("title")
                         templateModel.templateId = obj.getInt("id")
                         templateModel.status = "saved"
                         templateModel.description = obj.getString("description")
                         templateModel.inspectionConductedOn = obj.getString("inspection_date")
                         templateModel.inspectionConductedBy =
                             obj.getString("inspection_conducted_by")
                         templateModel.inspectionConductedAt =
                             obj.getString("inspection_conducted_at")
                         templateModel.category = obj.getString("category")
                         if (databaseRepository.medicalDataBase.getDao().getTemplate(
                                 templateModel.templateId,
                                 "saved"
                             ).isNullOrEmpty()
                         )
                             databaseRepository.medicalDataBase.getDao().insertTamplate(templateModel)

                     }
                 }*/
                inspectionViewState.postValue(SuccessLoad(list))
            }
        }
    }

    fun updateTamplets() {
        viewModelScope.launch(coroutineExceptionHandler) {
            var response =
                apInterFace.getUserInspection()
            var jsonarray = JSONObject(response.string()).getJSONArray("data")
            for (i in 0 until jsonarray.length()) {
                var obj = jsonarray.getJSONObject(i)
                var templateModel = TemplateModel()
                templateModel.title = obj.getString("title")
                templateModel.templateId = obj.getInt("id")
                templateModel.type = obj.getString("question_type_id")
                templateModel.status = "New"
                templateModel.category = obj.getString("category")
                getTemplateQuestions(templateModel)
                if (databaseRepository.medicalDataBase.getDao().getTemplate(
                        templateModel.templateId,
                        "New"
                    ).isNullOrEmpty()
                )
                    databaseRepository.medicalDataBase.getDao().insertTamplate(templateModel)

            }

            /*var respo =
                apInterFace.getSavedTempletes("getsavedtemplates?status=completed&user_profile_id=" + MyApplication.USER.id.toString())
            if (respo.isSuccessful) {
                var jsonarray = JSONObject(respo.body()?.string()).getJSONArray("getsavedtemplates")
                for (i in 0 until jsonarray.length()) {
                    var obj = jsonarray.getJSONObject(i)
                    var templateModel = TemplateModel()
                    templateModel.title = obj.getString("title")
                    templateModel.templateId = obj.getInt("id")
                    templateModel.status = "saved"
                    templateModel.description = obj.getString("description")
                    templateModel.inspectionConductedOn = obj.getString("inspection_date")
                    templateModel.inspectionConductedBy = obj.getString("inspection_conducted_by")
                    templateModel.inspectionConductedAt = obj.getString("inspection_conducted_at")
                    templateModel.category = obj.getString("category")
                    if (databaseRepository.medicalDataBase.getDao().getTemplate(
                            templateModel.templateId,
                            "saved"
                        ).isNullOrEmpty()
                    )
                        databaseRepository.medicalDataBase.getDao().insertTamplate(templateModel)

                }
            }*/
            /* var fecility = apInterFace.getFecilities()
             if (fecility.isSuccessful) {
                 fecility.body()?.let {
                     prefHelper.setFecility(it)
                 }
             }
             var state = apInterFace.getState()
             if (state.isSuccessful) {
                 state.body()?.string()?.let { prefHelper.setState(it) }
             }
             var catogaries = apInterFace.getCatogaries()
             if (catogaries.isSuccessful) {
                 catogaries.body()?.string()?.let { prefHelper.setCatogaries(it) }
             }*/
            var list = databaseRepository.medicalDataBase.getDao().getTemplate("New")
            inspectionViewState.postValue(SuccessLoad(list))
        }
    }

    private suspend fun getTemplateQuestions(templateModel: TemplateModel) {
        if (prefHelper.getTempleteData(templateModel.title!!) == "{}") {
             var value =
                apInterFace.getQuestionsForTamplete("templatedata/" + templateModel.templateId)
            var templateQuestions = value.body();
            prefHelper.setTempleteData(templateModel.title!!, Gson().toJson(templateQuestions))
        }
    }

    fun updateStateLgas() {
        viewModelScope.launch(coroutineExceptionHandler) {
            var fecility = apInterFace.getFecilities()
            if (fecility.isSuccessful) {
                fecility.body()?.let { prefHelper.setFacility(it) }
            }
            var state = apInterFace.getState()
            if (state.isSuccessful) {
                state.body()?.string()?.let { prefHelper.setState(it) }
            }
            var catogaries = apInterFace.getCatogaries()
            if (catogaries.isSuccessful) {
                catogaries.body()?.string()?.let { prefHelper.setCategories(it) }
            }
            inspectionViewState.postValue(SuccessLoad(listOf()))
        }
    }

    fun getViewState(): MutableLiveData<InspectionViewState> {
        return inspectionViewState
    }

    fun getUpdateViewState(): MutableLiveData<InspectionViewState> {
        return inspectionViewState
    }

}