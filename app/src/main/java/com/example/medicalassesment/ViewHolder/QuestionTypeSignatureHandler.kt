package com.example.medicalassesment.ViewHolder

import android.content.Context
import com.example.medicalassesment.adapter.QustionAdapter
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.databinding.NewItemLayoutBinding

class QuestionTypeSignatureHandler(
    var mBinding: NewItemLayoutBinding,
    var questionModel: QuestionModel,
    var qustionAdapter: QustionAdapter,
    var position: Int
) {
    private val context: Context = mBinding.root.context

    init {
      //  QuestionTypeSignatureHandler.questionModel = questionModel;
        iniate()
    }



    private fun iniate() {

    }
    fun updateModel(questionModel: QuestionModel) {
        qustionAdapter.updateItem(questionModel,position)
    }
}