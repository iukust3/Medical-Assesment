package com.example.medicalassesment.Helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.QuestionModel
import java.text.DecimalFormat

class ScoreCalculator(var context: Context, var textView: TextView) {
    init {
        scoreCalculator = this
    }

    private val dao = MedicalDataBase.getInstance(context).getDao()
    suspend fun calcualteScore(list: List<QuestionModel>) {
        var correct = 0f;
        var na = 0;
        var nonans = 0;
        list.forEach {
            if (it.type == Constant.QUESTION_TYPE_YESNO) {
                if (!it.getAnswer().isNullOrEmpty()) {
                    if (it.getAnswer() == "-1") {
                        na++;
                    } else if (it.getAnswer() != it.fail) {
                        correct++;
                    }
                }
            } else {
                nonans++;
            }
        }
        (context as Activity).runOnUiThread {
            val dec = DecimalFormat("##.00")
            textView.visibility = View.VISIBLE
            textView.text =
                "Score " + dec.format(((correct / ((list.size - nonans) - na)) * 100)) + "%"
        }

    }

    companion object {
        @Volatile
        @JvmStatic
        var scoreCalculator: ScoreCalculator? = null
    }
}