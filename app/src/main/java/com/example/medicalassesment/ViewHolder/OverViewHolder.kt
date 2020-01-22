package com.example.medicalassesment.ViewHolder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.adapter.PreviewAdapter
import com.example.medicalassesment.Interfaces.QustionViewHolderInterface
import com.example.medicalassesment.database.Dao
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.databinding.PreviewItemBinding


class OverViewHolder : RecyclerView.ViewHolder {
    private val dao: Dao

    companion object {
        lateinit var mQustionViewHolderInterface: QustionViewHolderInterface
        fun getQustionInterface(): QustionViewHolderInterface {
            return mQustionViewHolderInterface;
        }
    }

    private var binding: PreviewItemBinding;
    private var context: Context;
    private lateinit var baseQuestion:BaseQustion
    private val previewAdapter: PreviewAdapter

    constructor(
        view: PreviewItemBinding,
        previewAdapter: PreviewAdapter
    ) : super(view.root) {
        dao = MedicalDataBase.getInstance(view.root.context).getDao()
     //   mQustionViewHolderInterface = this
        this.binding = view
        this.context = view.root.context
        this.previewAdapter = previewAdapter
    }

    fun bindViewHolder(
        baseQustion: BaseQustion
    ) {
        this.baseQuestion = baseQustion;
        binding.previWItem.setValues(baseQustion)
     /*   binding.guidLine.text = baseQuestion.getGuideline()
        binding.title.text = baseQuestion.getTittle()
        binding.answerLable.text = baseQuestion.getAnswer()
        binding.signature.visibility = GONE



        binding.edit.setOnClickListener {
            mQustionViewHolderInterface=this
            (context as SurveyActivity).takePicture(baseQuestion, binding.ImageLayout)
        }
        if (baseQuestion.imageUris == null || baseQuestion.type== QUSTION_TYPE_SIGNATURE)
            binding.ImageLayout.visibility = GONE
        else {
            binding.ImageLayout.removeAllViews()
            baseQuestion.imageUris?.forEach {
                  val source =
                      MediaStore.Images.Media.getBitmap((context as Activity).contentResolver, it)
                  var imageBitmap = Bitmap.createScaledBitmap(source, 100, 150, true)

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
                    .signature(ObjectKey("" + baseQuestion.id + "_" + baseQuestion.title))
                    .into(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                binding.ImageLayout.addView(imageView)
                //  source.recycle()
                binding.ImageLayout.visibility = VISIBLE
            }
        }
        when (baseQuestion.type) {
            QUESTION_TYPE_YESNO -> {
                binding..visibility = VISIBLE
                if (baseQuestion.answer != null) {
                    binding.radioYes.isChecked = Integer.parseInt(baseQuestion.answer) == 1
                    binding.radioNo.isChecked = Integer.parseInt(baseQuestion.answer) == 0
                }
            }
            QUESTION_TYPE_DATE -> {
                binding.dateTime.text = "Select Date-Time"
                binding.dateTime.visibility = VISIBLE
                binding.dateTime.setOnClickListener {
                    Utils.dialogDatePickerLight(
                        binding.guidLine,
                        baseQuestion
                    )
                }
            }
            QUESTION_TYPE_EDITTEXT -> {
                binding.etqustion.visibility = VISIBLE
            }
            QUESTION_TYPE_YESNO_WITH_COMMENT -> {
                binding.etqustion.visibility = VISIBLE
                binding.yesNoLayout.visibility = VISIBLE
            }
            QUSTION_TYPE_DROPDOWN -> {
                binding.dateTime.text = "Select Response"
                binding.dateTime.visibility = VISIBLE
                if (baseQuestion.answer != null) {
                    binding.guidLine.visibility = VISIBLE
                    binding.guidLine.text =
                        baseQuestion.dropDownItems?.get(Integer.parseInt(baseQuestion.answer))
                            ?: ""
                }
                binding.dateTime.setOnClickListener {
                    showDialog(baseQuestion)
                }
            }
            QUESTION_TYPE_MAINTITLE -> {
                binding.takeImage.visibility = GONE
            }
            QUSTION_TYPE_SIGNATURE -> {
                if(!baseQuestion.imageUris.isNullOrEmpty()){
                    binding.signature.scaleType=ImageView.ScaleType.FIT_XY
                    Glide.with(binding.signature)
                        .load(baseQuestion.imageUris!![0])
                        .into(binding.signature)

                }
                binding.signature.visibility = VISIBLE
                binding.guidLine.visibility = VISIBLE
                binding.guidLine.text =baseQuestion.answer
                baseQuestion.answer = binding.guidLine.text.toString()
                binding.takeImage.setImageResource(R.drawable.ic_signature)
                binding.takeImage.setOnClickListener {
                    mQustionViewHolderInterface=this
                    var intent = Intent(context, SignatureActivity::class.java)
                    intent.putExtra("Image", baseQuestion.imageUris?.get(0))
                    intent.putExtra("QuestionID",baseQuestion.id)
                    (context as Activity).startActivityForResult(intent, 300)
                }
            }
        }
        binding.radioNo.setOnCheckedChangeListener { _, g ->
            if (g) {
                baseQuestion.answer = "0"
                previewAdapter.updateItem(baseQuestion, adapterPosition)

                GlobalScope.launch {
                    dao.update(baseQuestion)
                }
            }
        }
        binding.radioYes.setOnCheckedChangeListener { _, g ->
            if (g) {
                baseQuestion.answer = "1"
                previewAdapter.updateItem(baseQuestion, adapterPosition)

                GlobalScope.launch {
                    dao.update(baseQuestion)

                }
            }
        }*/
        binding.executePendingBindings()
    }

   /* private fun showDialog(questionModel: QuestionModel) {
        var alertDialog = AlertDialog.Builder(binding.root.context)
        alertDialog.setTitle(questionModel.title)
        val lp = WindowManager.LayoutParams()
        alertDialog.setSingleChoiceItems(
            questionModel.dropDownItems,
            if (questionModel.answer != null) Integer.parseInt(questionModel.answer) else -1
        ) { dialog, which ->
            binding.guidLine.visibility = VISIBLE
            binding.guidLine.text = questionModel.dropDownItems?.get(which) ?: ""
            questionModel.answer = "$which"
            previewAdapter.updateItem(questionModel, adapterPosition)

            GlobalScope.launch {
                dao.update(questionModel)
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
        if (questionModel.type != QUSTION_TYPE_SIGNATURE) {
            if (questionModel.imageUris == null)
                questionModel.imageUris = ArrayList()
            questionModel.imageUris?.add(uri)
            previewAdapter.updateItem(questionModel, adapterPosition)
            GlobalScope.launch {
                dao.update(questionModel)
            }
        } else {
           binding.signature.setImageBitmap(BitmapFactory.decodeFile(uri))
                questionModel.imageUris = ArrayList()
            questionModel.answer=Utils.getFormattedDateSimple(Calendar.getInstance().timeInMillis)
            questionModel.imageUris?.add(uri)
            previewAdapter.updateItem(questionModel, adapterPosition)
            GlobalScope.launch {
                dao.update(questionModel)
            }
        }
    }*/


}