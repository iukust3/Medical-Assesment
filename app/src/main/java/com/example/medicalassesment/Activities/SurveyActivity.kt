package com.example.medicalassesment.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.medicalassesment.Fragments.FragmentInterction
import com.example.medicalassesment.Fragments.StartFragment
import com.example.medicalassesment.Fragments.SurveyFragment
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.databinding.ActivitySurvayBinding
import java.lang.Exception
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.medicalassesment.BuildConfig
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.database.QuestionViewModel
import com.example.medicalassesment.Helper.ScoreCalcualtor
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.ViewHolder.QustionViewHolder
import com.example.medicalassesment.ViewModels.*
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SurveyActivity : AppCompatActivity(), FragmentInterction {


    companion object {
        lateinit var file: File
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var uri: Uri
    lateinit var dataBiding: ActivitySurvayBinding
    private lateinit var surveyViewModel: SurveyViewModel
    lateinit var templateModel: TemplateModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBiding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_survay
        )
        templateModel = intent.getParcelableExtra("TemplateModel")
        ScoreCalcualtor(this, dataBiding.score)
        QuestionViewModel.tamplateID = templateModel.id
        (application as MyApplication).appComponent.inject(this)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var startFragment = StartFragment()
        changeFragment(startFragment, "StartFragment")
        surveyViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SurveyViewModel::class.java)
    }

    private fun printAll() {
        var dao = MedicalDataBase.getInstance(this).getDao()
        Log.e("TAG", "All Qustions " + Gson().toJson(dao.getQuestions()))
        GlobalScope.launch {
            Log.e("TAG", "All Qustions " + Gson().toJson(dao.getTemplate()))
        }
    }

    private fun changeFragment(fragment: Fragment, tag: String) {

        var transition = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.container, fragment, tag)
            .addToBackStack(fragment.tag)
            .commit()
    }

    private fun setViewState(survayViewState: SurvayViewState) {
        when (survayViewState) {
            is Loading -> setProgress(3)
            is NetworkError -> {
                //progre
                //  setProgress(false)
                //  showError(homeViewState.message!!)
            }
            is Success -> {
                //   setProgress(false)
                //   showPopularShows(homeViewState.homeViewData)
            }
        }
    }

    override fun onNextClick(index: Int) {
        runOnUiThread {
            when (index) {
                0 -> {
                    dataBiding.title.text = "Preliminary Information"
                    dataBiding.score.visibility = GONE
                }
                1 -> {
                    dataBiding.title.text = "Questions"
                    dataBiding.score.visibility = View.VISIBLE
                }
                2 -> {
                    dataBiding.title.text = "Feedback Summary"
                    dataBiding.score.visibility = GONE
                }
            }
        }
        changeFragment(SurveyFragment(index), "SurvayFragment_$index")
    }

    override fun onBackPressed() {
        try {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment is StartFragment) {
                finish()
            } else{
                supportFragmentManager.popBackStackImmediate()
            }
        } catch (ex: Exception) {
            super.onBackPressed()
        }
    }

    private lateinit var questionModel: BaseQustion
    private lateinit var imageLayout: LinearLayout
    override fun takePicture(questionModel: BaseQustion, linearLayout: LinearLayout) {
        this.questionModel = questionModel
        this.imageLayout = linearLayout
        when {
            ActivityCompat.checkSelfPermission(
                this@SurveyActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this@SurveyActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    200
                )
                return
            }
            ActivityCompat.checkSelfPermission(
                this@SurveyActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this@SurveyActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    200
                )
                return
            }
            else -> launchCameraActivity(questionModel)
        }
    }

    private fun launchCameraActivity(questionModel: BaseQustion) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                uri = Utils.createImageFile(
                    this@SurveyActivity,
                    questionModel.getId(),
                    questionModel.getToolId()
                ).let {
                    it?.let { it1 ->
                        FileProvider.getUriForFile(
                            this@SurveyActivity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it1
                        )
                    }
                }!!
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(takePictureIntent, 200)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            var imageBitmap: Bitmap? = null
            try {
                val source =
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageBitmap = Bitmap.createScaledBitmap(source, 100, 150, true)
                var imageView = ImageView(this)
                imageView.setImageBitmap(imageBitmap)
                imageView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                imageLayout.visibility = View.VISIBLE
                val marginParams =
                    ViewGroup.MarginLayoutParams(imageView.layoutParams as LinearLayout.LayoutParams)
                marginParams.setMargins(20, 20, 10, 20)
                var parms = LinearLayout.LayoutParams(marginParams)
                imageView.layoutParams = parms
                imageView.setImageBitmap(imageBitmap)
                imageLayout.addView(imageView)

                QustionViewHolder.mQustionViewHolderInterface.onPictureDone(uri.toString())

                source.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
                //Try to recover
            }
        } else if (requestCode == 300 && resultCode == Activity.RESULT_OK) {
            QustionViewHolder.mQustionViewHolderInterface.onPictureDone(file.absolutePath)
        } else if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment is StartFragment)
                fragment.onActivityResult(requestCode,resultCode,data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

