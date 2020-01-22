package com.example.medicalassesment.uIItems

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.ViewAnimation
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class Question(var questionModel: QuestionModel, context: Context) : LinearLayout(context),
    IQuestion {
    init {
        Iniate()
    }

    private lateinit var etAnswer: EditText
    private lateinit var etlat: EditText
    private lateinit var etLang: EditText
    private lateinit var yesBtn: TextView
    private lateinit var nobtn: TextView
    private lateinit var answerLabel: TextView
    private lateinit var qustiontittle: TextView
    private lateinit var description: TextView
    private lateinit var datetime: TextView
    private lateinit var guidline: TextView
    private lateinit var hide: Button
    private lateinit var show: ImageView
    private lateinit var lyt_expand_text: View
    private lateinit var yesnoLayout: LinearLayout;
    private lateinit var latLanLayout: LinearLayout;
    private lateinit var guidlineLayout: LinearLayout;

    private fun Iniate() {
        val question = LayoutInflater.from(context).inflate(R.layout.qustion_layout, this, true);
        etAnswer = question.findViewById(R.id.questionTextBox)
        guidlineLayout = question.findViewById(R.id.guidLineLayout)
        qustiontittle = question.findViewById(R.id.questionTitle)
        guidline = question.findViewById(R.id.questionGuide)
        etlat = question.findViewById(R.id.etlatude)
        etLang = question.findViewById(R.id.etlongitude)
        yesBtn = question.findViewById(R.id.optionYes)
        nobtn = question.findViewById(R.id.optionNo)
        answerLabel = question.findViewById(R.id.answer_lable)
        datetime = question.findViewById(R.id.date_time)
        answerLabel.text = "NOT SET"
        hide = question.findViewById(R.id.bt_hide)
        show = question.findViewById(R.id.bt_show)
        yesnoLayout = question.findViewById(R.id.yesNoLayout);
        latLanLayout = question.findViewById(R.id.latLangLayout);
      /*  if (questionModel.type == 3)
            latLanLayout.visibility = View.VISIBLE
        else latLanLayout.visibility = View.GONE
        if (questionModel.type == 2)
            datetime.visibility = View.VISIBLE
        else datetime.visibility = View.GONE*/
        lyt_expand_text = question.findViewById(R.id.lyt_expand_text)
        show.setOnClickListener { view -> toggleSection(view) }
        hide.setOnClickListener { toggleSection(show) }
     //   ViewAnimation.collapse(lyt_expand_text)
       /* if (questionModel.priority) {
            question.findViewById<TextView>(R.id.questionValidation).visibility = View.VISIBLE
        }
        if (questionModel.type == 1)
            yesnoLayout.visibility = View.VISIBLE
        else yesnoLayout.visibility = View.GONE*/

       /* yesBtn.setOnClickListener {
            questionModel.choiceOption = 1;
            it.setBackgroundResource(R.drawable.choice_button_back_selected)
            nobtn.setBackgroundResource(R.drawable.choice_button_back)
            answerLabel.setText("Yes")
        }
        nobtn.setOnClickListener {
            questionModel.choiceOption = 0;
            it.setBackgroundResource(R.drawable.choice_button_back_selected)
            yesBtn.setBackgroundResource(R.drawable.choice_button_back)
            answerLabel.setText("No")
        }
        qustiontittle.setText(questionModel.subTittle)
        guidline.setText(questionModel.guidLine)
        try {
            if (questionModel.guidLine == null) {
                guidlineLayout.visibility = View.GONE
            }
        } catch (e: Exception) {
        }*/
        datetime.setOnClickListener { dialogDatePickerLight() }
        etAnswer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 0)
                    answerLabel.text = "SET"
                else
                    answerLabel.text = "NOT SET"
            }

        })

    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        this.etAnswer.requestFocus()
        return super.requestFocus(direction, previouslyFocusedRect)
    }

    private fun toggleSection(view: View) {
        val show = toggleArrow(view)
        if (show) {
            ViewAnimation.expand(lyt_expand_text)
        } else {
            ViewAnimation.collapse(lyt_expand_text)
        }
    }

    fun toggleArrow(view: View): Boolean {
        if (view.rotation == 0f) {
            view.animate().setDuration(200).rotation(180f)
            return true
        } else {
            view.animate().setDuration(200).rotation(0f)
            return false
        }
    }

    private lateinit var nextFoucs: IQuestion;

    override fun isMandatoryFilled(): Boolean {
        /*if(questionModel.priority)
            if(questionModel.type==3){

            }*/
        return true
    }

    override fun getNextFoucus(): IQuestion {
        return this.nextFoucs;
    }

    override fun setNextFocus(iQuestion: IQuestion) {
        this.nextFoucs = iQuestion;
    }

    private fun dialogDatePickerLight() {
        val curCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateShipMillis = calendar.timeInMillis
                datetime.text = getFormattedDateSimple(
                    dateShipMillis
                )
                answerLabel.text = datetime.text

            },
            curCalender.get(Calendar.YEAR),
            curCalender.get(Calendar.MONTH),
            curCalender.get(Calendar.DAY_OF_MONTH)
        )
        //set dark light
        datePicker.isThemeDark = false
        datePicker.accentColor = resources.getColor(R.color.colorPrimary)
        datePicker.minDate = curCalender
        datePicker.show((context as AppCompatActivity).supportFragmentManager, "Datepickerdialog")
    }

    fun getFormattedDateSimple(dateTime: Long?): String {
        val newFormat = SimpleDateFormat("MMMM dd, yyyy")
        return newFormat.format(Date(dateTime!!))
    }

}