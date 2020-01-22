package com.example.medicalassesment.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.models.FeedBackModel
import com.example.medicalassesment.models.PreliminaryInfoModel
import com.example.medicalassesment.models.QuestionModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) :
    AndroidViewModel(application) {
    private val medicalDataBase = MedicalDataBase.getInstance(application)

    companion object {
        @Volatile
        var tamplateID = 2;
    }

    val qustions: LiveData<List<QuestionModel>>
    val feedback: LiveData<List<FeedBackModel>>
    val prelimanryInfo: LiveData<List<PreliminaryInfoModel>>

    init {
        qustions = medicalDataBase.getDao().getQuestions(tamplateID.toString())
        feedback = medicalDataBase.getDao().getFeedBack(tamplateID.toString())
        prelimanryInfo = medicalDataBase.getDao().getPrelimanryInfo(tamplateID.toString())
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("TAG", "eroor  " + exception.message)
        exception.printStackTrace()
    }

    fun deleteTask(questionModel: QuestionModel) =
        viewModelScope.launch(coroutineExceptionHandler) {
            //  medicalDataBase.getDao().deleteQuestion(questionModel)
        }

    fun insertQustion() = viewModelScope.launch(coroutineExceptionHandler) {
        /*var list=com.example.medicalassesment.Utials.Utils.getMap()
        list.forEach {
            it.tamplateId=1
        }
        medicalDataBase.getDao().insertQustions(list)
         list=com.example.medicalassesment.Utials.Utils.getMap()
        list.forEach {
            it.tamplateId=2
        }
        medicalDataBase.getDao().insertQustions(list)*/
    }
}