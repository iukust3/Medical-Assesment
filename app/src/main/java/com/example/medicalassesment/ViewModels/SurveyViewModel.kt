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
            var templateModelnew = TemplateModel()
            templateModelnew = templateModel;
            templateModelnew.status = "InProgress"
            templateModelnew.title =
                Utils.getFormattedDateSimple() + "/" + MyApplication.USER.firstname + " " + MyApplication.USER.surname
            templateModelnew.description = templateModel.title
            templateModelnew.templateId = templateModel.templateId
            templateModelnew.id = databaseRepository.medicalDataBase
                .getDao().insertTamplate(templateModelnew).toInt()
            var templateQuestions: Template_questions? = null

            if (prefHelper.getTempleteData(templateModel.title!!) == "{}") {
                var value =
                    apInterFace.getQuestionsForTamplete("templatedata/" + templateModelnew.templateId)
                templateQuestions = value.body();
                prefHelper.setTempleteData(templateModel.title!!, Gson().toJson(templateQuestions))
            } else {
               templateQuestions = Gson().fromJson<Template_questions>(
                    prefHelper.getTempleteData(templateModel.title!!),
                    Template_questions::class.java
                );
            }
            try {
                templateQuestions?.templateQuestions?.let { it ->
                    it.forEach {
                        it.template_id = "${templateModelnew.id}"
                        //it.type = Constant.QUESTION_TYPE_YESNO
                    }
                    databaseRepository.medicalDataBase.getDao().insertQustions(
                        it
                    )
                }
                templateQuestions?.tempalteFeedbacks?.let { it1 ->
                    it1.forEach {
                        it.template_id = "${templateModelnew.id}"
                    }
                    databaseRepository.medicalDataBase.getDao().insertFeedBack(
                        it1
                    )
                }
                var list = Constant.getDefaultQuestions(templateModelnew)
                list.forEach {
                    it.template_id = "${templateModelnew.id}"
                }
                databaseRepository.medicalDataBase.getDao().insertPreliminaryInfo(
                    list
                )
                templateQuestions?.templatePreliminaryinfo?.toList()?.let { it1 ->
                    it1.forEach {

                        it.template_id = "${templateModelnew.id}"
                    }
                    databaseRepository.medicalDataBase.getDao().insertPreliminaryInfo(
                        it1
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            success(templateModelnew)
        }
    }

    private fun success(templateModel: TemplateModel) {
        surveyViewStateLiveData.value =
            Success(templateModel)
    }

    private fun onError(exception: Throwable) {
        Log.e("TAG", "Error " + exception.message)
        surveyViewStateLiveData.value = NetworkError(exception.message)
        exception.printStackTrace()
    }

    fun getsurvayViewStateLiveData(): LiveData<SurvayViewState> {
        return surveyViewStateLiveData
    }
}