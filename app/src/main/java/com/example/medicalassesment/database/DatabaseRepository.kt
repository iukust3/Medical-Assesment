package com.example.medicalassesment.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.medicalassesment.models.QuestionModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(val application: Application) {
    val medicalDataBase = MedicalDataBase.getInstance(application)

    suspend fun getQuestions(tampletId: Int): LiveData<List<QuestionModel>> {
        return medicalDataBase.getDao().getQuestions(tampletId.toString())
    }

    suspend fun getQuestionsNonLive(tampletId: Int): List<QuestionModel> {
        medicalDataBase.getDao().getQuestions(tampletId.toString()).value.let {
            return it?:ArrayList();
        }
    }
}