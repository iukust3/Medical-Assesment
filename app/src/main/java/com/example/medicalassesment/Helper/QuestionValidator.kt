package com.example.medicalassesment.Helper

import android.content.Context
import android.util.Patterns
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.Retrofit.Output
import com.example.medicalassesment.Utials.Constant

class QuestionValidator(
    val context: Context,
    private val templateId: Int,
    val index: Int
) {
    private var list: List<BaseQustion>? = null

    constructor(
        context: Context,
        templateId: Int,
        index: Int,
        list: List<BaseQustion>
    ) : this(context, templateId, index) {
        this.list = list
    }

    suspend fun validateQuestions(): Output {
        val list: List<BaseQustion> = if (!list.isNullOrEmpty()) {
            list!!
        } else {
            when (index) {
                -1 -> MedicalDataBase.getInstance(context).getDao()
                    .getDefaultInfo(templateId.toString())
                0 -> MedicalDataBase.getInstance(context).getDao()
                    .getPrelimanryInfoNonDefult(templateId.toString())
                1 -> MedicalDataBase.getInstance(context).getDao()
                    .getQuestionsNonlive(templateId.toString())
                else -> MedicalDataBase.getInstance(context).getDao()
                    .getFeedBackNonlive(templateId.toString())
            }
        }
        list.forEach {
            when (it.getQuestionTypeId()) {
                Constant.QUSTION_TYPE_SECTIONTITTLE, Constant.QUSTION_TYPE_PROXIMITY -> {
                }
                Constant.QUSTION_TYPE_SIGNATURE -> {
                    if (it.getPriority() == "1")
                        if (it.getAnswer().isNullOrEmpty() && it.getImageuri().isNullOrEmpty()) {
                            return Output.Error(list.indexOf(it))
                        }
                }
                Constant.QUSTION_TYPE_EMAIL -> {
                    if (it.getAnswer()
                            .isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(it.getAnswer())
                            .matches()
                    ) {
                        return Output.Error(list.indexOf(it))
                    }
                }
                Constant.QUSTION_TYPE_PHONE -> {
                    if (!it.getAnswer().isNullOrEmpty()) {
                        if (it.getAnswer()?.length!! !in 11 downTo 11)
                            return Output.Error(list.indexOf(it))
                    } else return Output.Error(list.indexOf(it))
                }
                else -> {
                    if (it.getPriority() == "1")
                        if (it.getAnswer().isNullOrEmpty()) {
                            return Output.Error(list.indexOf(it))
                        }
                }
            }
        }
        return Output.Success
    }
}
