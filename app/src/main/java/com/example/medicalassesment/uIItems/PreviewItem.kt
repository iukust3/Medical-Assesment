package com.example.medicalassesment.uIItems

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import com.example.medicalassesment.databinding.OverviewLayoutBinding
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.medicalassesment.Activities.OverViewActivity
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.FeedBackModel
import com.example.medicalassesment.models.PreliminaryInfoModel
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Constant.Companion.QUESTION_TYPE_YESNO
import com.example.medicalassesment.Utials.Constant.Companion.QUESTION_TYPE_YESNO_WITH_COMMENT
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_DROPDOWN
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PreviewItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        private var stateindex = -1;
        private var lgaindex = -1;
    }

    private var mBinding: OverviewLayoutBinding
    lateinit var mQuestionModel: BaseQustion

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding = OverviewLayoutBinding.inflate(inflater, this, true)

    }

    fun setValues(baseQuestion: BaseQustion) {
        mQuestionModel = baseQuestion
        if (baseQuestion is QuestionModel) {

            mBinding.optionButton.visibility = visibility;
            mBinding.answerLable.visibility = View.GONE
            when {
                mQuestionModel.getAnswer() == "1" -> {
                    mBinding.optionButton.text = "Yes"
                    mBinding.optionButton.setBackgroundResource(R.drawable.option_green)
                }
                mQuestionModel.getAnswer() == "0" -> {
                    mBinding.optionButton.text = "No"
                    mBinding.optionButton.setBackgroundResource(R.drawable.option_red)
                }
                else -> {
                    mBinding.optionButton.text = "NA"
                    mBinding.optionButton.setBackgroundResource(R.drawable.option_na)

                }
            }
            if (baseQuestion.fail == mQuestionModel.getAnswer()) {
                mBinding.optionButton.setBackgroundResource(R.drawable.option_red)
            } else if (baseQuestion.fail != baseQuestion.getAnswer() && baseQuestion.getAnswer()!="-1") {
                mBinding.optionButton.setBackgroundResource(R.drawable.option_green)
            }/* else if (baseQuestion.fail == "1" && baseQuestion.getAnswer() == "0") {
                mBinding.optionButton.setBackgroundResource(R.drawable.option_green)
            } else if (baseQuestion.fail == "0" && baseQuestion.getAnswer() == "1") {
                mBinding.optionButton.setBackgroundResource(R.drawable.option_green)
            }*/ else
                when {
                    baseQuestion.getAnswer() == "1" -> mBinding.optionButton.setBackgroundResource(R.drawable.option_green)
                    baseQuestion.getAnswer() == "0" -> mBinding.optionButton.setBackgroundResource(
                        R.drawable.option_red
                    )
                    else -> mBinding.optionButton.setBackgroundResource(R.drawable.option_na)
                }
        } else {
            mBinding.answerLable.visibility = View.VISIBLE

            mBinding.optionButton.visibility = View.GONE
        }
        if (baseQuestion.getSubTittle().isNullOrEmpty()) {
            mBinding.subTittle.visibility = View.GONE
        } else {
            mBinding.subTittle.text = mQuestionModel.getSubTittle()
            mBinding.subTittle.visibility = View.VISIBLE
        }
        if (!baseQuestion.getImageuri().isNullOrEmpty() && baseQuestion.getQuestionTypeId() != Constant.QUSTION_TYPE_SIGNATURE) {
            mBinding.ImageLayout.visibility = View.VISIBLE
            mBinding.ImageLayout.removeAllViews()
            baseQuestion.getImageuri()?.forEach {
                /*  val source =
                      MediaStore.Images.Media.getBitmap((context as Activity).contentResolver, it)
                  var imageBitmap = Bitmap.createScaledBitmap(source, 100, 150, true)
               */
                var imageView = ImageView(context)

                imageView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                val marginParams =
                    MarginLayoutParams(imageView.layoutParams as LayoutParams)
                marginParams.setMargins(20, 20, 10, 20)
                var parms = LayoutParams(marginParams)
                imageView.layoutParams = parms
                mBinding.ImageLayout.visibility = View.VISIBLE
                Glide.with(imageView)
                    .applyDefaultRequestOptions(RequestOptions().override(100, 100))
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(ObjectKey("" + baseQuestion.getId() + "_" + baseQuestion.getTittle()))
                    .into(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                mBinding.ImageLayout.addView(imageView)
                //  source.recycle()
                mBinding.ImageLayout.visibility = View.VISIBLE
            }
        } else {
            mBinding.ImageLayout.visibility = View.GONE
        }
        mBinding.signature.visibility = View.GONE
        if (baseQuestion.getQuestionTypeId() == Constant.QUSTION_TYPE_SIGNATURE) {
            mBinding.signature.scaleType = ImageView.ScaleType.FIT_XY
            mBinding.signature.visibility = View.VISIBLE
            try {
                Log.e("TAG", "Signature " + baseQuestion.getImageuri()!![0])
                Glide.with(mBinding.signature)
                    .applyDefaultRequestOptions(RequestOptions().override(500, 500))
                    .load(baseQuestion.getImageuri()!![0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(ObjectKey(baseQuestion.getQuestionTypeId() + "" + baseQuestion.getId() + "_" + baseQuestion.getTittle()))
                    .into(mBinding.signature)
            } catch (e: Exception) {
                mBinding.signature.setImageDrawable(null)
                mBinding.signature.visibility= GONE
            }
        }
        mBinding.title.text = mQuestionModel.getTittle()
        if (!baseQuestion.getGuideline().isNullOrEmpty()) {
            mBinding.guidLine.visibility = View.VISIBLE
            mBinding.guidLine.text = baseQuestion.getGuideline()
        } else mBinding.guidLine.visibility = View.GONE
        /*if (mQuestionModel.type == 3) {
            mBinding.answerLable.text =
                "Latitude : ${mQuestionModel.latitude}    Longitude : ${mQuestionModel.langtude}"

        } else*/
        mBinding.answerLable.text = mQuestionModel.getAnswer()
        mBinding.answerLable.layoutParams=LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        Log.e("TAG", "Answer :"+mQuestionModel.getAnswer())
        if (OverViewActivity.orignalModel.status == "completed") {
            mBinding.edit.visibility = View.GONE
        }
        if (baseQuestion is PreliminaryInfoModel) {
            val prefHelper = PrefHelper(context);
            try {
                 when (baseQuestion.title) {
                    "Facility Name:" ->
                        mBinding.answerLable.text =
                            OverViewActivity.orignalModel.title
                    "Ward :" -> {
                        mBinding.answerLable.text =
                            prefHelper.getState().state[stateindex].lgas[lgaindex].wards[Integer.parseInt(
                                baseQuestion.getAnswer().toString()
                            )].name
                    }
                    "State : " -> {
                        stateindex = Integer.parseInt(baseQuestion.getAnswer().toString())
                        mBinding.answerLable.text =
                            prefHelper.getState()
                                .getName(Integer.parseInt(baseQuestion.getAnswer().toString()))

                    }
                    "LGA : " -> {
                        lgaindex = Integer.parseInt(baseQuestion.getAnswer().toString())
                        mBinding.answerLable.text =
                            prefHelper.getState().state[stateindex].lgas[lgaindex].name
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mBinding.edit.visibility = GONE
        mBinding.edit.setOnClickListener { showCustomDialog() }
    }


    private lateinit var etAnswer: EditText
    private lateinit var etLang: EditText
    private lateinit var etlat: EditText
    private lateinit var yesBtn: TextView
    private lateinit var nobtn: TextView
    private lateinit var qustiontittle: TextView
    private lateinit var description: TextView
    private lateinit var datetime: TextView
    private lateinit var guidline: TextView
    private lateinit var yesnoLayout: LinearLayout;
    private lateinit var latLanLayout: LinearLayout;
    private lateinit var guidlineLayout: LinearLayout;

    private fun showCustomDialog() {
        var qustionModel = mQuestionModel;
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_event)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById(R.id.bt_close) as ImageButton).setOnClickListener { dialog.dismiss() }
        var title = dialog.findViewById<TextView>(R.id.title)
        title.text = mQuestionModel.getSubTittle()
        etAnswer = dialog.findViewById(R.id.questionTextBox)
        guidlineLayout = dialog.findViewById(R.id.guidLineLayout)
        qustiontittle = dialog.findViewById(R.id.title)
        guidline = dialog.findViewById(R.id.questionGuide)
        yesBtn = dialog.findViewById(R.id.optionYes)
        nobtn = dialog.findViewById(R.id.optionNo)
        datetime = dialog.findViewById(R.id.date_time)
        etlat = dialog.findViewById(R.id.etlatude)
        etLang = dialog.findViewById(R.id.etlongitude)

        yesnoLayout = dialog.findViewById(R.id.yesNoLayout);
        latLanLayout = dialog.findViewById(R.id.latLangLayout);
        /* if (mQuestionModel.type == 3)
             latLanLayout.visibility = View.VISIBLE
         else*/ latLanLayout.visibility = View.GONE
        if (mQuestionModel.getQuestionTypeId() == Constant.QUSTION_TYPE_DATEPAST)
            datetime.visibility = View.VISIBLE
        else datetime.visibility = View.GONE

        if (mQuestionModel.getQuestionTypeId() == QUESTION_TYPE_YESNO_WITH_COMMENT || mQuestionModel.getQuestionTypeId() == QUESTION_TYPE_YESNO)
            yesnoLayout.visibility = View.VISIBLE
        else yesnoLayout.visibility = View.GONE
        if (mQuestionModel.getQuestionTypeId() == QUESTION_TYPE_YESNO_WITH_COMMENT || mQuestionModel.getQuestionTypeId() == QUESTION_TYPE_YESNO) {
            if (mQuestionModel.getAnswer() == "1") {
                yesBtn.setBackgroundResource(R.drawable.choice_button_back_selected)
                nobtn.setBackgroundResource(R.drawable.choice_button_back)
            } else {
                nobtn.setBackgroundResource(R.drawable.choice_button_back_selected)
                yesBtn.setBackgroundResource(R.drawable.choice_button_back)
            }
        }
        yesBtn.setOnClickListener {
            qustionModel.setAnswer("1");
            it.setBackgroundResource(R.drawable.choice_button_back_selected)
            nobtn.setBackgroundResource(R.drawable.choice_button_back)
        }

        if (qustionModel.getQuestionTypeId() == QUSTION_TYPE_DROPDOWN) {
            mBinding.answerLable.text =
                qustionModel.getAnswer()?.toInt()?.let { qustionModel.getDropDownItems()?.get(it) }
                    ?: ""
        }
        nobtn.setOnClickListener {
            qustionModel.setAnswer("0");
            it.setBackgroundResource(R.drawable.choice_button_back_selected)
            yesBtn.setBackgroundResource(R.drawable.choice_button_back)
        }
        qustiontittle.text = qustionModel.getTittle()
        guidline.text = qustionModel.getGuideline()
        try {
            if (qustionModel.getImageuri() == null) {
                guidlineLayout.visibility = View.GONE
            }
        } catch (e: Exception) {
        }
        datetime.setOnClickListener { dialogDatePickerLight() }
        etAnswer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                qustionModel.setAnswer(s.toString());
            }
        })
        etlat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // qustionModel.latitude = s.toString();
            }
        })
        etLang.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //    qustionModel.langtude = s.toString();
            }
        })
        dialog.findViewById<Button>(R.id.bt_save).setOnClickListener {
            mQuestionModel = qustionModel;
            GlobalScope.launch {
                when (qustionModel) {
                    is QuestionModel ->
                        MedicalDataBase.getInstance(context).getDao().update(qustionModel)
                    is FeedBackModel ->
                        MedicalDataBase.getInstance(context).getDao().update(qustionModel)
                    is PreliminaryInfoModel ->
                        MedicalDataBase.getInstance(context).getDao().update(qustionModel)
                }

            }
            setValues(mQuestionModel)
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun dialogDatePickerLight() {
        val curCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateShipMillis = calendar.timeInMillis
                datetime.text = getFormattedDateSimple(
                    dateShipMillis
                )
                mQuestionModel.setAnswer(datetime.text.toString())

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

    private fun getFormattedDateSimple(dateTime: Long?): String {
        val newFormat = SimpleDateFormat("MMMM dd, yyyy")
        return newFormat.format(Date(dateTime!!))
    }

    private fun showDialog(baseQuestion: BaseQustion) {
        var alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(baseQuestion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            baseQuestion.getDropDownItems()?.toTypedArray(),
            if (baseQuestion.getAnswer() != null) Integer.parseInt(baseQuestion.getAnswer().toString()) else -1
        ) { dialog, which ->
            mBinding.guidLine.visibility = VISIBLE
            mBinding.guidLine.text = baseQuestion.getDropDownItems()?.get(which) ?: ""
            baseQuestion.setAnswer("$which")
            //   qustionAdapter.updateItem(baseQuestion, adapterPosition)

            GlobalScope.launch {
                //  dao.update(baseQuestion)
            }
            dialog.dismiss()
        }
        var dialog = alertDialog.create()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog.window?.attributes = lp
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    context,
                    android.R.color.white
                )
            )
        )

    }

}