package com.example.medicalassesment.Helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.QuestionModel
import java.text.DecimalFormat

class ScoreCalcualtor(var context: Context, var textView: TextView) {
    init {
        scoreCalcualtor = this
    }

    private val dao = MedicalDataBase.getInstance(context).getDao()
   suspend fun calcualteScore(list:List<QuestionModel>) {
        var correct = 0f;
        list.forEach {
            if (!it.getAnswer().isNullOrEmpty()) {
                if (it.getAnswer() != it.fail) {
                    correct++;
                }
            }
        }
       (context as Activity).runOnUiThread{
           val dec=DecimalFormat("##.00")
           textView.visibility=View.VISIBLE
           textView.text = "Score " + dec.format(((correct/list.size) * 100))+"%"
       }

    }

    companion object {
        @Volatile
        @JvmStatic
        var scoreCalcualtor: ScoreCalcualtor? = null
    }
}