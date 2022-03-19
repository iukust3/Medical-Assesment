package com.example.medicalassesment.ViewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.database.DatabaseRepository
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Retrofit.APIinterFace
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.database.Template_questions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class SurveyViewModel @Inject constructor(
    private val apInterFace: APIinterFace,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val surveyViewStateLiveData: MutableLiveData<SurvayViewState> = MutableLiveData()
    private val prefHelper = PrefHelper(databaseRepository.application)
    fun onScreenLoaded(templateModel: TemplateModel) {
        templateModel.id = databaseRepository.medicalDataBase.getDao().getQuestions().size + 1;
        surveyViewStateLiveData.value = Loading
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            var templateQuestions: Template_questions? = null
            if (prefHelper.getTempleteData(templateModel.title!!) == "{}") {
                var value =
                    apInterFace.getQuestionsForTamplete("templatedata/" + templateModel.templateId)
                templateQuestions = value.body();
                prefHelper.setTempleteData(templateModel.title!!, Gson().toJson(templateQuestions))
            } else {
                templateQuestions = Gson().fromJson<Template_questions>(
                    prefHelper.getTempleteData(templateModel.title!!),
                    Template_questions::class.java
                );

            }
            templateModel.description = templateModel.title
            try {
                val list = Constant.getDefaultQuestions(templateModel)
                templateQuestions?.defultPreliminaryInfo = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
            success(templateModel, templateQuestions)
        }
    }

    suspend fun insertData(templateQuestions: Template_questions, templateModel: TemplateModel) {

        templateModel.status = "InProgress"
        /* //  templateModelnew = templateModel;
         templateModelnew.type = templateModel.type
         templateModelnew.category = templateModel.category
         templateModelnew.title =templateModel.title
         templateModelnew.description = templateModel.description
         templateModelnew.templateId = templateModel.templateId
        */
         var templateModelnew = templateModel;
        templateModelnew.id = 0
        templateModel.id = databaseRepository.medicalDataBase
            .getDao().insertTamplate(templateModelnew).toInt()

        val file =
            Utils.getFolderPath(MyApplication.APPLICATION, templateModel.id.toString())
        if (file.exists()) {
            file.delete()
        }
        try {
            templateQuestions?.templateQuestions?.let { it ->
                it.forEach {
                    it.template_id = "${templateModel.id}"
                }
                databaseRepository.medicalDataBase.getDao().insertQustions(
                    it
                )
            }
            templateQuestions.tempalteFeedbacks?.let { it1 ->
                it1.forEach {
                    it.template_id = "${templateModel.id}"
                }
                databaseRepository.medicalDataBase.getDao().insertFeedBack(
                    it1
                )
            }
            templateQuestions.defultPreliminaryInfo.forEach {
                it.template_id = "${templateModel.id}"
            }
            databaseRepository.medicalDataBase.getDao().insertPreliminaryInfo(
                templateQuestions.defultPreliminaryInfo
            )
            templateQuestions.templatePreliminaryinfo?.toList()?.let { it1 ->
                it1.forEach {
                    it.template_id = "${templateModel.id}"
                }
                databaseRepository.medicalDataBase.getDao().insertPreliminaryInfo(
                    it1
                )
            }
            success(templateModel, templateQuestions)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e)
        }
    }

    private fun success(templateModel: TemplateModel, templateQuestions: Template_questions?) {
        surveyViewStateLiveData.postValue(
            templateQuestions?.let { Success(templateModel, it) })
    }

    private fun onError(exception: Throwable) {
        Log.e("TAG", "Error " + exception.message)
        surveyViewStateLiveData.postValue(NetworkError(exception.message))
        exception.printStackTrace()
    }

    fun getsurvayViewStateLiveData(): LiveData<SurvayViewState> {
        return surveyViewStateLiveData
    }
}