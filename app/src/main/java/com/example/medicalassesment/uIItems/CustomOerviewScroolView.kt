package com.example.medicalassesment.uIItems

import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.ViewAnimation

class CustomOerviewScroolView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private lateinit var linearLayout: LinearLayout
    private lateinit var mContentView: LinearLayout
     var list: List<QuestionModel> = emptyList()
    companion object{
        lateinit var customOerviewScroolView: CustomOerviewScroolView
    }

    fun iniate(
        templateId:TemplateModel
    ) {
        customOerviewScroolView=this
        var list = MedicalDataBase.getInstance(context).getDao().getQuestions(templateId.templateId.toString())
        linearLayout = LinearLayout(context)
        linearLayout.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        linearLayout.clipChildren = true
        linearLayout.orientation = LinearLayout.VERTICAL
        val view = LayoutInflater.from(context).inflate(R.layout.header_layout, null)
        linearLayout.addView(view)
        object : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg params: Void?): Boolean? {
                var expandCollapseView: ExpandCollapseView? = null
                list.value?.forEach {
                    (context as AppCompatActivity).runOnUiThread {
                        val qustion = PreviewItem(context)
                        qustion.mQuestionModel = it
                        qustion.setValues(it)
                        linearLayout.addView(qustion)
                    }
                }
                return null
            }
        }.execute()
        this.addView(linearLayout)
    }

    private fun Addtitle(string: String) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val accordionLayout = inflater.inflate(R.layout.layoutexpend_colleps, null) as LinearLayout
        mContentView = accordionLayout.findViewById(R.id.content_layout) as LinearLayout
        lateinit var qustionTittle: TextView
        lateinit var arrow: ImageView
        lateinit var expandLayout: LinearLayout
        expandLayout = accordionLayout.findViewById(R.id.lyt_expand_collapse) as LinearLayout
      //  ViewAnimation.collapsefast(expandLayout)
        arrow = accordionLayout.findViewById(R.id.bt_show) as ImageView
        qustionTittle = accordionLayout.findViewById(R.id.questionTitle)

       // arrow.animate().setDuration(20).rotation(0f)
        (accordionLayout.findViewById(R.id.answer_lable) as TextView).visibility = View.GONE
        /*  a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandCollapseView);
        if (a.getString(R.styleable.ExpandCollapseView_tittle) != null) {
            TextView subTittle = findViewById(R.id.questionTitle);
            subTittle.setText(a.getString(R.styleable.ExpandCollapseView_tittle));
        }*/
        qustionTittle.text = string
        arrow.setOnClickListener(OnClickListener { toggleSection(it, expandLayout) })
        mContentView.removeAllViews()
        linearLayout.addView(
            accordionLayout,
            LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )

    }

    private fun toggleSection(arrow: View, layout: View) {
        val show = toggleArrow(arrow)
        if (show) {
            ViewAnimation.expand(layout)
        } else {
            ViewAnimation.collapse(layout)
        }
    }

    private fun toggleArrow(view: View): Boolean {
        return if (view.rotation == 0f) {
            view.animate().setDuration(200).rotation(180f)
            true
        } else {
            view.animate().setDuration(200).rotation(0f)
            false
        }
    }
}