package com.example.medicalassesment.ViewHolder

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View.*
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Activities.SurveyActivity
import com.example.medicalassesment.Interfaces.QustionViewHolderInterface
import com.example.medicalassesment.Utials.Constant.Companion.QUESTION_TYPE_EDITTEXT
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_EMAIL
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_DROPDOWN
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_PHONE
import com.example.medicalassesment.Utials.Constant.Companion.QUESTION_TYPE_TEXTAREA
import com.example.medicalassesment.Utials.Constant.Companion.QUESTION_TYPE_YESNO
import com.example.medicalassesment.Utials.Constant.Companion.QUESTION_TYPE_YESNO_WITH_COMMENT
import com.example.medicalassesment.databinding.NewItemLayoutBinding
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import com.bumptech.glide.request.RequestOptions
import com.example.medicalassesment.Activities.BaseActivity
import com.example.medicalassesment.Activities.SignatureActivity
import com.example.medicalassesment.GlideApp
import com.example.medicalassesment.adapter.QustionAdapter
import com.example.medicalassesment.database.Dao
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_DATEFUTURE
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_DATEPAST
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_MULTICHICE
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_SECTIONTITTLE
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_SIGNATURE
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.models.*
import com.example.medicalassesment.uIItems.BaseImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@DelicateCoroutinesApi
class QustionViewHolder : RecyclerView.ViewHolder, QustionViewHolderInterface {
    private val dao: Dao
    private var binding: NewItemLayoutBinding
    private var context: Context
    private lateinit var mBaseQustion: BaseQustion
    private val qustionAdapter: QustionAdapter
    private var index = 0
    lateinit var redcolor: ColorStateList
    lateinit var greencolor: ColorStateList
    private lateinit var templateModel: TemplateModel

    constructor(
        view: NewItemLayoutBinding,
        qustionAdapter: QustionAdapter
    ) : super(view.root) {
        dao = MedicalDataBase.getInstance(view.root.context).getDao()
        this.binding = view
        this.context = view.root.context
        this.templateModel = BaseActivity.templateModel
        this.qustionAdapter = qustionAdapter
        redcolor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_800))
        greencolor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_600))
    }

    fun bindViewHolder(
        questionModel: BaseQustion,
        index: Int
    ) {

        this.index = index
        this.mBaseQustion = questionModel
        binding.guidLine.text = mBaseQustion.getGuideline()
        binding.qustionTittle.text = mBaseQustion.getTittle()
        if (mBaseQustion.getQuestionTypeId() != QUESTION_TYPE_YESNO &&
            mBaseQustion.getQuestionTypeId() != QUESTION_TYPE_YESNO_WITH_COMMENT
        ) {

            if (mBaseQustion.getGuideline() == null) {
                binding.guidLine.visibility = GONE
                binding.etqustion.hint = mBaseQustion.getSubTittle()
            } else {
                binding.guidLine.visibility = VISIBLE
                binding.etqustion.hint = mBaseQustion.getTittle()
            }
        }
        binding.dateTime.visibility = GONE
        binding.etqustion.visibility = GONE
        binding.yesNoLayout.visibility = GONE
        binding.signature.visibility = GONE
        binding.takeImage.visibility = VISIBLE
        binding.etqustion.minHeight = Utils.convertDpToPixel(40f).toInt()
        if (mBaseQustion.getImageRequired() == "0") {
            binding.takeImage.visibility = GONE
        } else binding.takeImage.visibility = VISIBLE

        binding.takeImage.setImageResource(R.drawable.ic_camera)
        if (mBaseQustion.getQuestionTypeId() != QUESTION_TYPE_YESNO &&
            mBaseQustion.getQuestionTypeId() != QUESTION_TYPE_YESNO_WITH_COMMENT
        ) {
            if (mBaseQustion.getAnswer().isNullOrEmpty())
                binding.etqustion.setText("")
            else binding.etqustion.setText(mBaseQustion.getAnswer().toString())
            binding.etqustion.error = null
            var qustionTextWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    mBaseQustion.setAnswer(s.toString())
                    qustionAdapter.updateItem(mBaseQustion)
                    if (questionModel.getQuestionTypeId() == QUSTION_TYPE_EMAIL) {
                        if (!s.isNullOrEmpty()) {
                            if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                                binding.etqustion.error = "Please enter valid email address"
                                binding.etqustion.requestFocus()
                            } else {
                                binding.etqustion.error = null
                                update()
                            }
                        }
                    } else if (questionModel.getQuestionTypeId() == QUSTION_TYPE_PHONE) {
                        if (s != null)
                            if (!s.isNullOrEmpty()) {
                                if (s.length < 11 || s.length > 11) {
                                    binding.etqustion.error = "Please enter valid phone number"
                                    binding.etqustion.requestFocus()
                                } else {
                                    binding.etqustion.error = null
                                    update()
                                }
                            }
                    } else {
                        GlobalScope.launch {
                            if (index == 0)
                                dao.update(mBaseQustion as PreliminaryInfoModel)
                            if (index == 1)
                                dao.update(mBaseQustion as QuestionModel)
                            if (index == 2)
                                dao.update(mBaseQustion as FeedBackModel)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            }
            binding.etqustion.addTextChangedListener(qustionTextWatcher)
        }
        binding.takeImage.setOnClickListener {
             (context as SurveyActivity).takePicture(mBaseQustion,bindingAdapterPosition)
        }
        if (mBaseQustion.getImageuri() == null || mBaseQustion.getQuestionTypeId() == QUSTION_TYPE_SIGNATURE)
            binding.ImageLayout.visibility = GONE
        else {
            binding.ImageLayout.removeAllViews()
            val folder = File(
                Utils.getFolderPath(
                    context,
                    questionModel,
                    questionModel.getToolId()
                )
            )
            val listImages = folder.listFiles()
            if(!listImages.isNullOrEmpty())
                listImages.forEach { file->

                    /*  val source =
                          MediaStore.Images.Media.getBitmap((context as Activity).contentResolver, it)
                      var imageBitmap = Bitmap.createScaledBitmap(source, 100, 150, true)
                   */
                    val imageView = BaseImageView(context)

                    imageView.layoutParams = LinearLayout.LayoutParams(
                        200,
                        200
                    )
                    val marginParams =
                        MarginLayoutParams(imageView.layoutParams as LinearLayout.LayoutParams)
                    marginParams.setMargins(20, 20, 10, 20)
                    val parms = LinearLayout.LayoutParams(marginParams)
                    imageView.layoutParams = parms
                    binding.ImageLayout.visibility = VISIBLE
                    imageView.loadImage(file)
                    imageView.onDelete = {
                       try {
                            mBaseQustion.getImageuri()!!.removeAt(binding.ImageLayout.indexOfChild(imageView))
                        } catch (e: Exception) {
                            e.printStackTrace();
                        }
                        binding.ImageLayout.removeView(imageView)
                        qustionAdapter.notifyItemChanged(bindingAdapterPosition)
                        update()
                        Log.e("TAG", " Image deleted ")
                    }
                    binding.ImageLayout.addView(imageView)
                    //  source.recycle()
                    binding.ImageLayout.visibility = VISIBLE
                }
        }
        binding.etqustion.error = null
        when (mBaseQustion.getQuestionTypeId()) {
            QUESTION_TYPE_YESNO -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.hint = "Comment...."
                try {
                    if ((mBaseQustion as QuestionModel).comments.isNullOrEmpty()) {
                        binding.etqustion.hint = "Comment...."
                        binding.etqustion.setText("")
                    } else
                        binding.etqustion.setText(mBaseQustion.getCurrentComments().toString())
                } catch (e: Exception) {
                }
                binding.etqustion.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        mBaseQustion.setCurrentComment(p0.toString())
                        update()
                    }
                })
                binding.yesNoLayout.visibility = VISIBLE
                /* binding.radioYes.supportButtonTintList=*/
                CompoundButtonCompat.setButtonTintList(
                    binding.radioYes,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey_60))
                );
                if (mBaseQustion.getAnswer() != null) {
                    binding.radioYes.isChecked = mBaseQustion.getAnswer() == "1"
                    binding.radioNo.isChecked = mBaseQustion.getAnswer() == "0"
                    binding.radioNA.isChecked = mBaseQustion.getAnswer() == "-1"
                } else {
                    binding.radioYes.isChecked = false
                    binding.radioNo.isChecked = false
                    binding.radioNA.isChecked = false
                }
                if (mBaseQustion is QuestionModel) {
                    if (!(mBaseQustion as QuestionModel).fail.isNullOrEmpty()) {
                        if ((mBaseQustion as QuestionModel).fail == "0") {
                            CompoundButtonCompat.setButtonTintList(binding.radioNo, redcolor)
                            CompoundButtonCompat.setButtonTintList(binding.radioYes, greencolor)
                        } else {
                            CompoundButtonCompat.setButtonTintList(binding.radioNo, greencolor)
                            CompoundButtonCompat.setButtonTintList(binding.radioYes, redcolor)
                        }
                    } else {
                        CompoundButtonCompat.setButtonTintList(binding.radioNo, redcolor)
                        CompoundButtonCompat.setButtonTintList(binding.radioYes, greencolor)
                    }
                }
            }
            QUSTION_TYPE_DATEPAST -> {
                /*  var name = templateModel.title
                  var fecility = name?.let { dao.getFacility(it) }
                  binding.dateTime.visibility = GONE
                  binding.guidLine.visibility = VISIBLE
                  if (!mBaseQustion.getAnswer().isNullOrEmpty()) {
                      binding.guidLine.text = mBaseQustion.getAnswer()
                  } else {
                      binding.guidLine.text = fecility?.dateOfLastInspection
                  }
                  mBaseQustion.setAnswer(binding.guidLine.text as String?)
                  update()
              } else {*/
                var name = templateModel.title
                var fecility = name?.let { dao.getFacility(it) }
                binding.dateTime.visibility = GONE
                binding.guidLine.visibility = VISIBLE
                if (!mBaseQustion.getAnswer().isNullOrEmpty()) {
                    binding.guidLine.text = mBaseQustion.getAnswer()
                } else {
                    binding.guidLine.text = fecility?.dateOfLastInspection
                }
                if (fecility != null) {
                    if (!fecility.dateOfLastInspection.isNullOrEmpty()) {
                        mBaseQustion.setAnswer(binding.guidLine.text as String?)
                        update()
                    }
                } else {
                    binding.dateTime.visibility = VISIBLE
                }

                binding.dateTime.text = "Select Date-Time"
                if (!mBaseQustion.getAnswer().isNullOrEmpty()) {
                    binding.guidLine.text = mBaseQustion.getAnswer()
                    binding.guidLine.visibility = VISIBLE
                }
                binding.dateTime.setOnClickListener {
                    Utils.dialogDatePickerLight(context, true) {
                        binding.guidLine.text = it
                        binding.guidLine.visibility = VISIBLE
                        mBaseQustion.setAnswer(it)
                        qustionAdapter.updateItem(mBaseQustion, index)
                        update()
                    }
                }
            }
            QUSTION_TYPE_DATEFUTURE -> {
                binding.dateTime.text = "Select Date-Time"
                binding.dateTime.visibility = VISIBLE
                if (!mBaseQustion.getAnswer().isNullOrEmpty()) {
                    binding.guidLine.text = mBaseQustion.getAnswer()
                    binding.guidLine.visibility = VISIBLE
                }
                binding.dateTime.setOnClickListener {
                    Utils.dialogDatePickerLight(context, false) {
                        binding.guidLine.text = it
                        binding.guidLine.visibility = VISIBLE
                        mBaseQustion.setAnswer(it)
                        qustionAdapter.updateItem(mBaseQustion, index)
                        update()
                    }
                }
            }
            QUESTION_TYPE_EDITTEXT -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_CLASS_TEXT
            }
            QUSTION_TYPE_PHONE -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_CLASS_PHONE
            }
            QUSTION_TYPE_EMAIL -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

            }
            QUESTION_TYPE_TEXTAREA -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_CLASS_TEXT
                binding.etqustion.isSingleLine = false
                binding.etqustion.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                binding.etqustion.minHeight = Utils.convertDpToPixel(120f).toInt()
                binding.etqustion.error = null
            }
            QUESTION_TYPE_YESNO_WITH_COMMENT -> {
                binding.etqustion.visibility = VISIBLE
                binding.yesNoLayout.visibility = VISIBLE
            }
            QUSTION_TYPE_DROPDOWN -> {
                try {
                    binding.dateTime.text = "Select Response"
                    binding.dateTime.visibility = VISIBLE
                    if (mBaseQustion.getAnswer() != null) {
                        binding.guidLine.visibility = VISIBLE
                        binding.guidLine.text =
                            mBaseQustion.getDropDownItems()
                                ?.get(Integer.parseInt(mBaseQustion.getAnswer().toString()))
                                ?: ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.dateTime.setOnClickListener {
                    showDialog()
                }
            }
            QUSTION_TYPE_SECTIONTITTLE -> {
                binding.takeImage.visibility = GONE
            }
            QUSTION_TYPE_SIGNATURE -> {

                val folder = File(
                    Utils.getFolderPath(
                        context,
                        questionModel,
                        questionModel.getToolId()
                    )
                )
                val listofImages = folder.listFiles()
                if(!listofImages.isNullOrEmpty())
                    listofImages.forEach {file->

                        /*  val source =
                              MediaStore.Images.Media.getBitmap((context as Activity).contentResolver, it)
                          var imageBitmap = Bitmap.createScaledBitmap(source, 100, 150, true)
                       */
                        binding.signature.scaleType = ImageView.ScaleType.FIT_XY
                        GlideApp.with(binding.signature)
                            .setDefaultRequestOptions(RequestOptions().override(500, 500))
                            .load(file)
                            .into(binding.signature)
                        binding.guidLine.text = mBaseQustion.getAnswer()
                        binding.guidLine.visibility = VISIBLE
                    }
                else{
                    binding.signature.setImageDrawable(null)
                }
                binding.takeImage.visibility = VISIBLE
                binding.signature.visibility = VISIBLE
                binding.takeImage.setImageResource(R.drawable.ic_signature)
                binding.takeImage.setOnClickListener {
                    if(listofImages.isNullOrEmpty())
                        (context as SurveyActivity).startActivityForSignature(bindingAdapterPosition,
                            questionModel,"" )
                    else   (context as SurveyActivity).startActivityForSignature(bindingAdapterPosition,
                        questionModel,listofImages[0].absolutePath )
                } }
            QUSTION_TYPE_MULTICHICE -> {
                try {
                    binding.dateTime.text = "Select Response"
                    binding.dateTime.visibility = VISIBLE
                    if (mBaseQustion.getAnswer() != null) {
                        binding.guidLine.visibility = VISIBLE
                        binding.guidLine.text = mBaseQustion.getAnswer().toString()
                            .split(",").size.toString() + " Optin selected"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.dateTime.setOnClickListener {
                    showDialogMulti()
                }
            }
        }
        binding.radioNo.setOnCheckedChangeListener { _, g ->
            if (g) {
                binding.radioYes.isChecked = false
                binding.radioNA.isChecked = false
                mBaseQustion.setAnswer("0")
                qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)
                GlobalScope.launch {
                    dao.update(mBaseQustion as QuestionModel)
                }
            }
        }
        binding.radioYes.setOnCheckedChangeListener { _, g ->
            if (g) {
                binding.radioNo.isChecked = false
                binding.radioNA.isChecked = false
                mBaseQustion.setAnswer("1")
                qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)
                GlobalScope.launch {
                    dao.update(mBaseQustion as QuestionModel)
                }
            }
        }
        binding.radioNA.setOnCheckedChangeListener { _, g ->
            if (g) {
                binding.radioNo.isChecked = false
                binding.radioYes.isChecked = false
                mBaseQustion.setAnswer("-1")
                qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)
                GlobalScope.launch {
                    dao.update(mBaseQustion as QuestionModel)
                }
            }
        }
        binding.executePendingBindings()
    }


    private fun showDialog() {
        val alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            mBaseQustion.getDropDownItems()?.toTypedArray(),
            if (!mBaseQustion.getAnswer().isNullOrEmpty()) try {
                Integer.parseInt(mBaseQustion.getAnswer().toString())
            } catch (e: Exception) {
                -1
            } else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = mBaseQustion.getDropDownItems()?.get(which) ?: ""
            mBaseQustion.setAnswer("$which")
            qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)

            GlobalScope.launch {
                update()
            }
            dialog.dismiss()
        }
        var dialog = alertDialog.create()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog?.window?.attributes = lp
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    context,
                    android.R.color.white
                )
            )
        )

    }

    private fun showDialogMulti() {
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        var ans = mBaseQustion.getAnswer()?.split(",")?.toList()
        var array = mBaseQustion.getDropDownItems()?.toTypedArray()?.size?.let {
            BooleanArray(
                it
            ) { i ->
                if (!ans.isNullOrEmpty()) false
                else ans?.contains("$i")!!
            }
        }
        try {
            ans?.forEach {
                array?.set(it.toInt(), true)
            }
        } catch (e: Exception) {
        }
        alertDialog.setMultiChoiceItems(
            mBaseQustion.getDropDownItems()?.toTypedArray(),
            array
        ) { p0, p1, p2 ->
            if (p2)
                if (mBaseQustion.getAnswer().isNullOrEmpty()) {
                    mBaseQustion.setAnswer("$p1")
                } else {
                    mBaseQustion.setAnswer(mBaseQustion.getAnswer() + ",$p1")
                }
            else {
                if (mBaseQustion.getAnswer()?.length!! >= 2) {
                    mBaseQustion.setAnswer(mBaseQustion.getAnswer()?.replace(",$p1", ""))
                } else
                    mBaseQustion.setAnswer("")
            }
            binding.guidLine.visibility = VISIBLE

            binding.guidLine.text =
                mBaseQustion.getAnswer()?.split(",")?.size.toString() + " Option selected"
            qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)
            update()
        }
        var dialog = alertDialog.create()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog?.window?.attributes = lp
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    context,
                    android.R.color.white
                )
            )
        )

    }

    override fun onPictureDone(uri: String) {
        if (mBaseQustion.getQuestionTypeId() != QUSTION_TYPE_SIGNATURE) {
            if (mBaseQustion.getImageuri() == null)
                mBaseQustion.setImageuri(ArrayList())
            var uris=mBaseQustion.getImageuri();
           uris?.add(uri)
            mBaseQustion.setImageuri(uris)
            qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)
            qustionAdapter.notifyItemChanged(bindingAdapterPosition)
            GlobalScope.launch {
                update()
            }
        } else {
            binding.signature.setImageBitmap(BitmapFactory.decodeFile(uri))
            mBaseQustion.setImageuri(ArrayList())
            mBaseQustion.setAnswer(Utils.getFormattedDateSimple(Calendar.getInstance().timeInMillis))
            mBaseQustion.getImageuri()?.add(uri)
            qustionAdapter.updateItem(mBaseQustion, bindingAdapterPosition)
            GlobalScope.launch {
                update()
            }
        }
    }

    override fun onImageDelete(uri: String) {
        val file=File(uri);
        if(file.exists())
            file.delete()
        mBaseQustion.getImageuri()!!.remove(uri)
        qustionAdapter.notifyItemChanged(bindingAdapterPosition)
    }

    private fun update() {
        GlobalScope.launch {
            if (index == 0)
                dao.update(mBaseQustion as PreliminaryInfoModel)
            if (index == 1)
                dao.update(mBaseQustion as QuestionModel)
            if (index == 2)
                dao.update(mBaseQustion as FeedBackModel)
        }
    }

}