package com.example.medicalassesment.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.PixelCopy.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.databinding.ActivitySignatureBinding
import com.example.medicalassesment.models.FeedBackModel
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class SignatureActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignatureBinding
    private   var dao=MedicalDataBase.getInstance()?.getDao();
    private lateinit var feedBackModel: FeedBackModel;
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signature)
        setSupportActionBar(mBinding.toolbar)
        var q_id = intent.getIntExtra("QuestionID", -1);
        var templateId = intent.getStringExtra("TemplateID")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    200
                )
            }
        }

        feedBackModel= dao!!.getFeedBack(q_id);
        val path = intent.getStringExtra("Image")
        var id = intent.getIntExtra("QuestionID", 0);
        if (path != null) {
            /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                   mBinding.drawingView.background = BitmapFactory.
               } else mBinding.drawingView.setBackgroundDrawable(resource)
   */
            mBinding.drawingView.background = BitmapDrawable.createFromPath(path)
        }

        mBinding.save.setOnClickListener {
            val newIntent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getBitmapFromView {
                    var file: File?
                    file = if (path.isNullOrEmpty())
                        Utils.createImageFile(this, feedBackModel, templateId)
                    else File(path)
                    try {
                        try {
                            file?.delete()
                        } catch (e: Exception) {
                        }
                        file=   Utils.createImageFile(this, feedBackModel, templateId)
                        val ostream = FileOutputStream(file);
                        it.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                        ostream.close();
                        SurveyActivity.file = file as File

                        newIntent.putExtra("Image", file.absolutePath)
                        mBinding.drawingView.invalidate();
                    } catch (e: Exception) {
                        e.printStackTrace();
                    } finally {
                        setResult(Activity.RESULT_OK, newIntent)
                        finish()
                    }
                }
            } else {
                try {
                    convertViewToDrawable {
                        var file: File? = null

                        file = if (path.isNullOrEmpty())
                            Utils.createImageFile(this, feedBackModel, templateId)
                        else File(path)
                        try {
                            try {
                                file?.delete()
                            } catch (e: Exception) {
                            }
                            file=   Utils.createImageFile(this, feedBackModel, templateId)
                            val ostream = FileOutputStream(file);
                            it.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                            ostream.close();
                            intent.putExtra("Image", file?.absolutePath)
                            if (file != null) {
                                SurveyActivity.file = file as File
                            }
                            /* QuestionTypeSignatureHandler.imageView.setImageBitmap(it)
                                     QuestionTypeSignatureHandler.questionModel.imageUris = ArrayList()
                                     QuestionTypeSignatureHandler.questionModel.imageUris?.add(file.absolutePath)
                              */
                        } catch (e: Exception) {
                            e.printStackTrace();
                        } finally {
                            setResult(Activity.RESULT_OK, newIntent)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mBinding.clear.setOnClickListener {
            mBinding.drawingView.clearImage()
            mBinding.drawingView.invalidate()
            mBinding.drawingView.setBackgroundResource(R.color.white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.drawingView.background =
                    ColorDrawable(ContextCompat.getColor(this, R.color.white))
            }
        }
    }

    private fun convertViewToDrawable(callback: (Bitmap) -> Unit) {
        val b = Bitmap.createBitmap(
            mBinding.drawingView.width, mBinding.drawingView.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        c.translate(
            (-mBinding.drawingView.scrollX).toFloat(),
            (-mBinding.drawingView.scrollY).toFloat()
        )
        mBinding.drawingView.draw(c)
        callback(getResizedBitmap(b, 500))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBitmapFromView(callback: (Bitmap) -> Unit) {
     var bitmap=   mBinding.drawingView.drawToBitmap()
        callback(getResizedBitmap(bitmap, 500))
      /*  window?.let { window ->
            val bitmap = Bitmap.createBitmap(
                mBinding.drawingView.width,
                mBinding.drawingView.height,
                Bitmap.Config.ARGB_8888
            )
            val locationOfViewInWindow = IntArray(2)
            mBinding.drawingView.getLocationInWindow(locationOfViewInWindow)
            try {
                request(
                    window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + mBinding.drawingView.width,
                        locationOfViewInWindow[1] + mBinding.drawingView.height
                    ),
                    bitmap,
                    { copyResult ->
                        if (copyResult == SUCCESS) {
                            callback(getResizedBitmap(bitmap, 500))
                        }
                        // possible to handle other result codes ...
                    },
                    Handler()
                )
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }*/
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

}
