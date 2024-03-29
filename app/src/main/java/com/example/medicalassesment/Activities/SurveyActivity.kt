package com.example.medicalassesment.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.medicalassesment.Fragments.FragmentInteraction
import com.example.medicalassesment.Fragments.StartFragment
import com.example.medicalassesment.Fragments.SurveyFragment
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.databinding.ActivitySurvayBinding
import java.lang.Exception
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.medicalassesment.BuildConfig
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.database.QuestionViewModel
import com.example.medicalassesment.Helper.ScoreCalculator
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.ViewHolder.QustionViewHolder
import com.example.medicalassesment.ViewHolder.StartViewHolder
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.database.Template_questions
import com.example.medicalassesment.models.PreliminaryInfoModel
import com.example.medicalassesment.uIItems.BaseImageView
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.Arrays.toString
import javax.inject.Inject

class SurveyActivity : BaseActivity(), FragmentInteraction {

    companion object {
        @JvmStatic
        val ISFromEdit = "isFromEdit"
        lateinit var file: File
    }

    private var isFromEdit = false;
    private var startFragment: StartFragment? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private  var uri: Uri?=null
    lateinit var dataBiding: ActivitySurvayBinding
    lateinit var surveyViewModel: SurveyViewModel
    private var templetQustion: Template_questions? = null
    private lateinit var myActivityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBiding = ActivitySurvayBinding.inflate(
            layoutInflater, null, false
        )
        myActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
          onActivityResults(requestCode,it.resultCode,it.data)
        }
        iniateUi(dataBiding.root)
        setCurrantClassName(this)
        setSelectedNavigationItem(R.id.inspection)
        templateModel = intent.getParcelableExtra("TemplateModel")!!
        try {
            templetQustion =
                Gson().fromJson(intent.getStringExtra("Questions"), Template_questions::class.java)
            StartViewHolder.isFirstTime = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ScoreCalculator(this, dataBiding.score)
        QuestionViewModel.tamplateID = templateModel.id
        (application as MyApplication).appComponent.inject(this)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        isFromEdit = intent.getBooleanExtra(ISFromEdit, false)
        if (!isFromEdit) {
            startFragment = if (templetQustion == null) {
                StartFragment(listOf())
            } else templetQustion?.defultPreliminaryInfo?.let { StartFragment(it) }
            if (startFragment != null) {
                changeFragment(startFragment!!, "StartFragment")
            }
        } else {
            isFromEdit = false
            onNextClick(2)
        }
        surveyViewModel =
            ViewModelProvider(this, viewModelFactory).get(SurveyViewModel::class.java)
        surveyViewModel.getsurvayViewStateLiveData().observe(this, androidx.lifecycle.Observer {
            setViewState(it)
        })
        startTimer()
        // printAll()
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
            .disallowAddToBackStack()
            .commit()
    }

    private fun changeFragmentBack(fragment: Fragment, tag: String) {

        var transition = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )
            .replace(R.id.container, fragment, tag)
            .disallowAddToBackStack()
            .commit()
    }

    private fun setViewState(survayViewState: SurvayViewState) {
        when (survayViewState) {
            is Loading -> setProgress(3)
            is NetworkError -> {
                Log.e("TAG", "On Error " + survayViewState.message)
                //progre
                //  setProgress(false)
                //  showError(homeViewState.message!!)
            }
            is Success -> {
                templateModel = survayViewState.templateModel
                Log.e("TAG", "Id " + survayViewState.templateModel.id)
                dataBiding.title.text = "Preliminary Information"
                dataBiding.score.visibility = GONE
                //   setProgress(false)
                //   showPopularShows(homeViewState.homeViewData)
                dataBiding.title.text = "Preliminary Information"
                dataBiding.score.visibility = GONE
                QuestionViewModel.tamplateID = survayViewState.templateModel.id
                changeFragment(SurveyFragment(0), "SurvayFragment_0")
            }
        }
    }

    var index = -1;
    override fun onNextClick(index: Int) {
        this.index = index
        runOnUiThread {
            when (index) {
                0 -> {
                    dataBiding.title.text = "Preliminary Information"
                    dataBiding.score.visibility = GONE
                    changeFragment(SurveyFragment(0), "SurvayFragment_0")
                }
                1 -> {
                    dataBiding.title.text = "Questions"
                    dataBiding.score.visibility = View.VISIBLE
                    changeFragment(SurveyFragment(index), "SurvayFragment_$index")
                }
                2 -> {
                    dataBiding.title.text = "Feedback Summary"
                    dataBiding.score.visibility = GONE
                    changeFragment(SurveyFragment(index), "SurvayFragment_$index")
                }
            }
        }

    }

    override fun onBackPressed() {
        index--;
        try {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment is StartFragment) {
                finish()
            } else {
                when (index) {
                    -1 -> {
                        dataBiding.score.visibility = GONE
                        dataBiding.title.text = "Inspection Overview"
                        changeFragmentBack(StartFragment(listOf()), "StartFragment")
                    }
                    0 -> {
                        dataBiding.score.visibility = GONE
                        dataBiding.title.text = "Preliminary Information"
                        changeFragmentBack(SurveyFragment(0), "SurvayFragment_$index")
                    }
                    1 -> {
                        dataBiding.title.text = "Questions"
                        dataBiding.score.visibility = View.VISIBLE
                        changeFragmentBack(SurveyFragment(1), "SurvayFragment_$index")
                    }
                    2 -> {
                        dataBiding.title.text = "Feedback Summary"
                        dataBiding.score.visibility = GONE
                        changeFragmentBack(SurveyFragment(2), "SurvayFragment_$index")
                    }
                }
            }
        } catch (ex: Exception) {
            super.onBackPressed()
        }
    }

    private lateinit var questionModel: BaseQustion
    private lateinit var imageLayout: LinearLayout
    private  var position: Int=-1
    override fun takePicture(questionModel: BaseQustion,position: Int) {
        Log.e("TAG","Position $position")
        this.questionModel = questionModel
        this.position=position;
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

    override fun onBackClick(index: Int) {
        this.index = index
        onBackPressed()
    }

    private fun launchCameraActivity(questionModel: BaseQustion) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                var number = 0;
                if (questionModel.getImageuri()?.size!! > 0) {
                    val split =
                       File( questionModel.getImageuri()!![questionModel.getImageuri()?.size!! - 1]).name.split(
                            "_"
                        )
                    try {
                        number=split[1].toInt()+1
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                uri = Utils.createImageFile(
                    this@SurveyActivity,
                    questionModel,
                    number,
                    questionModel.getToolId()
                ).let {
                    it.let { it1 ->
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
Log.e("TAG","Null error")
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        this.requestCode=requestCode;
        myActivityResultLauncher.launch(intent);
        Log.e("TAG","Start activity with code "+requestCode);
    }
private var requestCode =-10;
    private fun onActivityResults(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragment = supportFragmentManager.findFragmentById(R.id.container);
        if(fragment is SurveyFragment) {
            if(uri==null)
                uri=Uri.parse(data?.extras?.getString("Image"))
            fragment.onActivityResults(requestCode, resultCode, data, position, uri!!)
        }else if(fragment is StartFragment)
            fragment.onActivityResult(requestCode,resultCode,data)
    }

    fun startActivityForSignature(bindingAdapterPosition: Int, questionModel: BaseQustion,path:String) {
        position=bindingAdapterPosition;
        val intent = Intent(this, SignatureActivity::class.java)
        Log.e("TAG","Title ${questionModel.getTittle()}")
        try {
            intent.putExtra("Image",path)
            intent.putExtra("TemplateID", questionModel.getToolId())
            intent.putExtra("QuestionID", questionModel.getId())
        } catch (e: Exception) {
            intent.putExtra("Image", "")
            intent.putExtra("TemplateID", questionModel.getToolId())
            intent.putExtra("QuestionID", questionModel.getId())
        }
        intent.putExtra("QuestionID", questionModel.getId())
        startActivityForResult(intent, 300)
    }

    fun insertData(
        templateModel: TemplateModel,
        defaultPreliminaryInfo: List<PreliminaryInfoModel>
    ) {
        this.templetQustion?.defultPreliminaryInfo = defaultPreliminaryInfo
        templetQustion?.let {
            GlobalScope.launch {
                surveyViewModel.insertData(it, templateModel)
            }
        }
    }

}

