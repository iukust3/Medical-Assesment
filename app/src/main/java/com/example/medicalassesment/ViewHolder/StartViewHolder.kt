package com.example.medicalassesment.ViewHolder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.location.Location
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
import com.example.medicalassesment.databinding.NewItemLayoutBinding
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.medicalassesment.Activities.SignatureActivity
import com.example.medicalassesment.adapter.CustomArrayAdpaterFecilities
import com.example.medicalassesment.adapter.StartAdapter
import com.example.medicalassesment.database.Dao
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.Helper.MyLocation
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_READONLY
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_SECTIONTITTLE
import com.example.medicalassesment.Utials.Constant.Companion.QUSTION_TYPE_SIGNATURE
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.adapter.CustomArrayAdpater
import com.example.medicalassesment.models.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class StartViewHolder(
    view: NewItemLayoutBinding,
    private val qustionAdapter: StartAdapter
) : RecyclerView.ViewHolder(view.root), QustionViewHolderInterface {
    val dao: Dao = MedicalDataBase.getInstance(view.root.context).getDao()

    companion object {
        lateinit var previousLayout: LinearLayout;
        private lateinit var addresText: TextView
        lateinit var mQustionViewHolderInterface: QustionViewHolderInterface
        fun getQustionInterface(): QustionViewHolderInterface {
            return mQustionViewHolderInterface;
        }
    }

    private var binding: NewItemLayoutBinding = view;
    private var context: Context;
    private lateinit var mBaseQustion: BaseQustion
    private var index = 0;
    lateinit var redcolor: ColorStateList
    lateinit var greencolor: ColorStateList
    private lateinit var prefHelper: PrefHelper

    init {
        mQustionViewHolderInterface = this
        this.context = view.root.context
        redcolor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_800))
        greencolor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_600))
        prefHelper = PrefHelper(context)
    }

    @SuppressLint("DefaultLocale")
    fun bindViewHolder(
        questionModel: BaseQustion,
        index: Int
    ) {

        this.index = index;
        this.mBaseQustion = questionModel;
        binding.guidLine.text = mBaseQustion.getGuideline()
        binding.qustionTittle.text = mBaseQustion.getTittle()
        if (mBaseQustion.getGuideline() == null) {
            binding.guidLine.visibility = GONE
            binding.etqustion.hint = mBaseQustion.getSubTittle()
        } else {
            binding.guidLine.visibility = VISIBLE
            binding.etqustion.hint = mBaseQustion.getTittle()
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
        if (mBaseQustion.getAnswer().isNullOrEmpty())
            binding.etqustion.setText("")
        else binding.etqustion.setText(mBaseQustion.getAnswer())
        binding.etqustion.error = null
        binding.etqustion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("TAG", "on text change")
                mBaseQustion.setAnswer(s.toString())
                qustionAdapter.updateItem(mBaseQustion, adapterPosition)
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

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.takeImage.setOnClickListener {
            mQustionViewHolderInterface = this
            (context as SurveyActivity).takePicture(mBaseQustion, binding.ImageLayout)
        }
        if (mBaseQustion.getImageuri() == null || mBaseQustion.getQuestionTypeId() == QUSTION_TYPE_SIGNATURE)
            binding.ImageLayout.visibility = GONE
        else {
            binding.ImageLayout.removeAllViews()
            mBaseQustion.getImageuri()?.forEach {
                /*  val source =
                      MediaStore.Images.Media.getBitmap((context as Activity).contentResolver, it)
                  var imageBitmap = Bitmap.createScaledBitmap(source, 100, 150, true)
               */
                var imageView = ImageView(context)

                imageView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val marginParams =
                    MarginLayoutParams(imageView.layoutParams as LinearLayout.LayoutParams)
                marginParams.setMargins(20, 20, 10, 20)
                var parms = LinearLayout.LayoutParams(marginParams)
                imageView.layoutParams = parms
                binding.ImageLayout.visibility = VISIBLE
                Glide.with(imageView)
                    .applyDefaultRequestOptions(RequestOptions().override(100, 100))
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(ObjectKey("" + mBaseQustion.getId() + "_" + mBaseQustion.getTittle()))
                    .into(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                binding.ImageLayout.addView(imageView)
                //  source.recycle()
                binding.ImageLayout.visibility = VISIBLE
            }
        }
        binding.etqustion.error = null
        binding.etqustion.isEnabled = true;
        when (mBaseQustion.getQuestionTypeId()) {

            QUESTION_TYPE_EDITTEXT -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_CLASS_TEXT
            }

            QUESTION_TYPE_TEXTAREA -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_CLASS_TEXT
                binding.etqustion.setSingleLine(false)
                binding.etqustion.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                binding.etqustion.minHeight = Utils.convertDpToPixel(120f).toInt()
                binding.etqustion.error = null
            }
            QUSTION_TYPE_READONLY -> {
                binding.etqustion.visibility = VISIBLE
                binding.etqustion.inputType = InputType.TYPE_CLASS_TEXT
                binding.etqustion.setSingleLine(false)
                binding.etqustion.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                binding.etqustion.minHeight = Utils.convertDpToPixel(120f).toInt()
                binding.etqustion.error = null
                binding.etqustion.isEnabled = false;
                addresText=binding.etqustion;
            }
            QUSTION_TYPE_DROPDOWN -> {
                try {
                    binding.dateTime.text = "Select Response"
                    binding.dateTime.visibility = VISIBLE
                    if (mBaseQustion.getAnswer() != null) {
                        binding.guidLine.visibility = VISIBLE
                        binding.guidLine.text =
                            mBaseQustion.getDropDownItems()?.get(Integer.parseInt(mBaseQustion.getAnswer()))
                                ?: ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                when {
                    mBaseQustion.getTittle()?.toLowerCase()?.contains("state")!! -> {
                        binding.dateTime.text = "Select State"
                        try {

                            binding.dateTime.visibility = VISIBLE
                            if (mBaseQustion.getAnswer() != null) {
                                binding.guidLine.visibility = VISIBLE
                                binding.guidLine.text = prefHelper.getState()
                                    .getName(Integer.parseInt(mBaseQustion.getAnswer()))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        binding.dateTime.setOnClickListener {
                            showStateDilog()
                        }
                    }
                    mBaseQustion.getTittle()?.equals(
                        "category",
                        true
                    )!! -> {
                        try {
                            binding.dateTime.visibility = VISIBLE
                            if (mBaseQustion.getAnswer() != null) {
                                binding.guidLine.visibility = VISIBLE
                                binding.guidLine.text =
                                    prefHelper.getCatogariesArray()[Integer.parseInt(mBaseQustion.getAnswer())]
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        binding.dateTime.text = "Select Category"
                        binding.dateTime.setOnClickListener {
                            showDialog()
                        }
                    }
                    mBaseQustion.getTittle()?.toLowerCase()?.contains("lga")!! -> {
                        binding.dateTime.setOnClickListener {
                            if (qustionAdapter.getItem(adapterPosition - 1).getAnswer().isNullOrEmpty()) {
                                Snackbar.make(
                                    it,
                                    "PLease select state first.",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                showLgaDilog()
                            }
                        }
                        binding.dateTime.text = "Select LGA"
                        try {
                            binding.dateTime.visibility = VISIBLE
                            if (mBaseQustion.getAnswer() != null) {
                                binding.guidLine.visibility = VISIBLE
                                binding.guidLine.text =
                                    prefHelper.getState().state[Integer.parseInt(
                                        qustionAdapter.getItem(adapterPosition - 1).getAnswer()
                                    )].lgas[Integer.parseInt(mBaseQustion.getAnswer())].name
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mBaseQustion.getTittle()?.toLowerCase()?.contains("ward")!! -> {
                        binding.dateTime.setOnClickListener {
                            if (qustionAdapter.getItem(adapterPosition - 1).getAnswer().isNullOrEmpty()) {
                                Snackbar.make(
                                    it,
                                    "PLease select LGA first.",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                showWardDilog()
                            }
                        }
                        binding.dateTime.text = "Select Ward"
                        try {
                            binding.dateTime.visibility = VISIBLE
                            if (mBaseQustion.getAnswer() != null) {
                                binding.guidLine.visibility = VISIBLE
                                binding.guidLine.text =
                                    prefHelper.getState().state[Integer.parseInt(
                                        qustionAdapter.getItem(adapterPosition - 2).getAnswer()
                                    )].lgas[Integer.parseInt(qustionAdapter.getItem(adapterPosition - 1).getAnswer())].wards[Integer.parseInt(
                                        mBaseQustion.getAnswer()
                                    )].name
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mBaseQustion.getTittle()?.contains("Facility Name", true)!! -> {
                        binding.dateTime.setOnClickListener {
                            showFecility()
                        }
                        binding.dateTime.text = "Select Fecility"
                        try {
                            binding.dateTime.visibility = VISIBLE
                            if (mBaseQustion.getAnswer() != null) {
                                binding.guidLine.visibility = VISIBLE
                                binding.guidLine.text = prefHelper.getFecility()
                                    .getName(Integer.parseInt(mBaseQustion.getAnswer()))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    else -> binding.dateTime.setOnClickListener {

                        showDialog()
                    }
                }
            }
            QUSTION_TYPE_SECTIONTITTLE -> {
                binding.takeImage.visibility = GONE
            }
            QUSTION_TYPE_SIGNATURE -> {
                if (!mBaseQustion.getImageuri().isNullOrEmpty()) {
                    binding.signature.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(binding.signature)
                        .setDefaultRequestOptions(RequestOptions().override(500, 500))
                        .load(mBaseQustion.getImageuri()!![0])
                        .into(binding.signature)
                    binding.guidLine.text = mBaseQustion.getAnswer()
                    binding.guidLine.visibility = VISIBLE
                }
                binding.takeImage.visibility = VISIBLE
                binding.signature.visibility = VISIBLE
                binding.takeImage.setImageResource(R.drawable.ic_signature)
                binding.takeImage.setOnClickListener {
                    mQustionViewHolderInterface = this
                    var intent = Intent(context, SignatureActivity::class.java)
                    try {
                        intent.putExtra("Image", mBaseQustion.getImageuri()?.get(0))
                        intent.putExtra("TemplateID", mBaseQustion.getToolId())
                        intent.putExtra("QuestionID", questionModel.getId())
                    } catch (e: Exception) {
                        intent.putExtra("Image", "")
                        intent.putExtra("TemplateID", mBaseQustion.getToolId())
                        intent.putExtra("QuestionID", questionModel.getId())
                    }
                    intent.putExtra("QuestionID", mBaseQustion.getId())
                    (context as Activity).startActivityForResult(intent, 300)
                }
            }
        }
        binding.radioNo.setOnCheckedChangeListener { _, g ->
            if (g) {
                binding.radioYes.isChecked = false
                mBaseQustion.setAnswer("0")
                qustionAdapter.updateItem(mBaseQustion, adapterPosition)
                GlobalScope.launch {
                    dao.update(mBaseQustion as QuestionModel)
                }
            }
        }
        binding.radioYes.setOnCheckedChangeListener { _, g ->
            if (g) {
                binding.radioNo.isChecked = false
                mBaseQustion.setAnswer("1")
                qustionAdapter.updateItem(mBaseQustion, adapterPosition)
                GlobalScope.launch {
                    dao.update(mBaseQustion as QuestionModel)
                }
            }
        }
        if (mBaseQustion.getTittle()?.contains("Coordinates")!!) {
            if (mBaseQustion.getAnswer().isNullOrEmpty()) {
                getLocation()
            } else binding.etqustion.setText(mBaseQustion.getAnswer())
        }

        binding.executePendingBindings()
    }

    private fun getLocation() {
        (context as Activity)?.let {
            if (
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                MyLocation().getLocation(context, object : MyLocation.LocationResult() {
                    override fun gotLocation(location: Location?) {
                        if (location != null) {
                            it.runOnUiThread {
                                binding.etqustion.setText("( ${location.latitude} , " + location.longitude + ")")
                            }
                        }
                    }
                })
            } else {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    200
                )
                // Show rationale and request permission.
            }
        }
    }

    private fun showDialog() {
        var category = prefHelper.getCatogaries().categories
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            CustomArrayAdpater(context, android.R.layout.select_dialog_singlechoice,
                prefHelper.getCatogaries().categories,CustomArrayAdpater.CATOGARY),
            if (!mBaseQustion.getAnswer().isNullOrEmpty()) try {
                Integer.parseInt(mBaseQustion.getAnswer())
            } catch (e: Exception) {
                -1
            } else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = category[which].name
            mBaseQustion.setAnswer("$which")
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)

            GlobalScope.launch {
                if (index == 0)
                    dao.update(mBaseQustion as PreliminaryInfoModel)
                if (index == 1)
                    dao.update(mBaseQustion as QuestionModel)
                if (index == 2)
                    dao.update(mBaseQustion as FeedBackModel)
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


    private fun showFecility() {
        var category = prefHelper.getFecility()
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            CustomArrayAdpaterFecilities(
                context,
                android.R.layout.select_dialog_singlechoice,
                category.facilities.toTypedArray()
            ),
            if (!mBaseQustion.getAnswer().isNullOrEmpty()) try {
                category.getIndex(Integer.parseInt(mBaseQustion.getAnswer()))
            } catch (e: Exception) {
                -1
            } else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = category.facilities[which].name
            mBaseQustion.setAnswer("${category.facilities[which].id}")
            if (qustionAdapter.getItem(adapterPosition + 1).getQuestionTypeId() == QUSTION_TYPE_READONLY) {
                addresText.text = category.facilities[which].address
            }
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)
            GlobalScope.launch {
                if (index == 0)
                    dao.update(mBaseQustion as PreliminaryInfoModel)
                if (index == 1)
                    dao.update(mBaseQustion as QuestionModel)
                if (index == 2)
                    dao.update(mBaseQustion as FeedBackModel)
                var fecility=category.facilities[which]
                fecility.dateOfLastInspection=Utils.getFormattedDateSimple()
                if(dao.getFacility(fecility.id).dateOfLastInspection == ""){
                    dao.insert(fecility)
                }
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

    override fun onPictureDone(uri: String) {
        if (mBaseQustion.getQuestionTypeId() != QUSTION_TYPE_SIGNATURE) {
            if (mBaseQustion.getImageuri() == null)
                mBaseQustion.setImageuri(ArrayList())
            mBaseQustion.getImageuri()?.add(uri)
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)
            GlobalScope.launch {
                if (index == 0)
                    dao.update(mBaseQustion as PreliminaryInfoModel)
                if (index == 1)
                    dao.update(mBaseQustion as QuestionModel)
                if (index == 2)
                    dao.update(mBaseQustion as FeedBackModel)

            }
        } else {
            binding.signature.setImageBitmap(BitmapFactory.decodeFile(uri))
            mBaseQustion.setImageuri(ArrayList())
            mBaseQustion.setAnswer(Utils.getFormattedDateSimple(Calendar.getInstance().timeInMillis))
            mBaseQustion.getImageuri()?.add(uri)
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)
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

    private fun showStateDilog() {
        var state = prefHelper.getState().state
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            CustomArrayAdpater(context, android.R.layout.select_dialog_singlechoice,
                state,CustomArrayAdpater.STATE),
            if (!mBaseQustion.getAnswer().isNullOrEmpty() && mBaseQustion.getAnswer() != "") try {
                Integer.parseInt(mBaseQustion.getAnswer())
            } catch (e: Exception) {
                -1
            } else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = state[which].name
            mBaseQustion.setAnswer("$which")
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)
            GlobalScope.launch {
                if (index == 0)
                    dao.update(mBaseQustion as PreliminaryInfoModel)
                if (index == 1)
                    dao.update(mBaseQustion as QuestionModel)
                if (index == 2)
                    dao.update(mBaseQustion as FeedBackModel)
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

    private fun showLgaDilog() {
        var lgas =
            prefHelper.getState().state[Integer.parseInt(qustionAdapter.getItem(adapterPosition - 1).getAnswer())].lgas
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            CustomArrayAdpater(context, android.R.layout.select_dialog_singlechoice,
                lgas,CustomArrayAdpater.LGA),
            if (!mBaseQustion.getAnswer().isNullOrEmpty()) try {
                Integer.parseInt(mBaseQustion.getAnswer())
            } catch (e: Exception) {
                -1
            } else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = lgas[which].name
            mBaseQustion.setAnswer("$which")
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)
            GlobalScope.launch {
                if (index == 0)
                    dao.update(mBaseQustion as PreliminaryInfoModel)
                if (index == 1)
                    dao.update(mBaseQustion as QuestionModel)
                if (index == 2)
                    dao.update(mBaseQustion as FeedBackModel)
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

    private fun showWardDilog() {
        var wards =
            prefHelper.getState().state[Integer.parseInt(qustionAdapter.getItem(adapterPosition - 2).getAnswer())].lgas[Integer.parseInt(
                qustionAdapter.getItem(adapterPosition - 1).getAnswer()
            )].wards
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(mBaseQustion.getTittle())
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            CustomArrayAdpater(context, android.R.layout.select_dialog_singlechoice,
                wards,CustomArrayAdpater.WARD),
            if (!mBaseQustion.getAnswer().isNullOrEmpty()) try {
                Integer.parseInt(mBaseQustion.getAnswer())
            } catch (e: Exception) {
                -1
            } else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = wards[which].name
            mBaseQustion.setAnswer("$which")
            qustionAdapter.updateItem(mBaseQustion, adapterPosition)
            GlobalScope.launch {
                if (index == 0)
                    dao.update(mBaseQustion as PreliminaryInfoModel)
                if (index == 1)
                    dao.update(mBaseQustion as QuestionModel)
                if (index == 2)
                    dao.update(mBaseQustion as FeedBackModel)
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
}