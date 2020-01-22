package com.example.medicalassesment.uIItems

import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ScrollView
import com.example.medicalassesment.models.QuestionModel


class CustomRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    private lateinit var linearLayout: LinearLayout
    var onQustionAdd: OnQustionAdd? = null
        private set
    lateinit var key: String
        private set

    fun setOnQuestionAdd(onQustionAdd: OnQustionAdd) {
        this.onQustionAdd = onQustionAdd
    }

    fun iniate(
        key: String
    ) {
       /* var surveyQuestionList = Utils.getQustionList(key)
        this.key = key;
        Log.e("TAG", "Size " + surveyQuestionList?.size)
        linearLayout = LinearLayout(context)
        linearLayout.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        linearLayout.clipChildren = true
        linearLayout.orientation = LinearLayout.VERTICAL
        this.addView(linearLayout)

        object : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg params: Void?): Boolean? {
                if (surveyQuestionList != null) {
                    for (surveyQuestion in surveyQuestionList) {
                        //region Change to recycler View
                        (context as Activity).runOnUiThread {
                            val qustion = Question(
                                surveyQuestion,
                                context
                            )
                            linearLayout.addView(qustion)
                        }
                    }
                }
                return null
            }
        }.execute()*/
    }
    //endregion

    interface OnQustionAdd {
        fun onQustionAdded(iQuestion: IQuestion)
    }

    fun save() {
        var list = ArrayList<QuestionModel>()
        object : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg params: Void?): Boolean? {

                return null
            }
        }.execute()
    }
}
