package com.example.medicalassesment.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.adapter.PreviewAdapter
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.FeedBackModel
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.reportGenration.PdfCreater
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.databinding.ActivityOverViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.medicalassesment.BuildConfig
import com.example.medicalassesment.Utials.Utils
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class OverViewActivity : AppCompatActivity() {
    companion object {
        lateinit var orignalModel: TemplateModel
    }

    private lateinit var templateModel: TemplateModel
    private var list: List<BaseQustion> = ArrayList()
    private var count = 0
    private var dao = MedicalDataBase.getInstance(this).getDao()
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var uploadViewModel: UploadViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databinidng: ActivityOverViewBinding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_over_view
            )
        (application as MyApplication).appComponent.inject(this)
        uploadViewModel = ViewModelProviders.of(this, factory)[UploadViewModel::class.java]
        uploadViewModel.getViewState().observe(this, Observer { setViewState(it) })
        templateModel = intent.getParcelableExtra<TemplateModel>("TemplateModel")
        orignalModel = intent.getParcelableExtra<TemplateModel>("TemplateModel")
        // orignalModel.status = "InProgress"
        Log.e("TAG", " Mark " + orignalModel.status)
        databinidng.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?

        databinidng.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        showLoadingDilog(false)

        GlobalScope.launch {
            var baseQustion = FeedBackModel()
            baseQustion.questiontype = "Header"
            baseQustion.title = "Failed Items"
            var qustionList = dao.getFaildItems(templateModel.id.toString())
            list = list.plus(baseQustion)
            list = list.plus(qustionList)

            var prelimnryList = dao.getPrelimanryInfoNonlive(templateModel.id.toString())
            baseQustion = FeedBackModel()
            baseQustion.questiontype = "Header"
            baseQustion.title = "Preliminary Information"
            list = list.plus(baseQustion)
            list = list.plus(prelimnryList)

            qustionList = dao.getQuestionsNonFail(templateModel.id.toString())
            baseQustion = FeedBackModel()
            baseQustion.questiontype = "Header"
            baseQustion.title = "Questions"
            list = list.plus(baseQustion)
            list = list.plus(qustionList)

            var feedBackList = dao.getFeedBackNonlive(templateModel.id.toString())
            baseQustion = FeedBackModel()
            baseQustion.questiontype = "Header"
            baseQustion.title = "Feedback Summary"
            list = list.plus(baseQustion)
            list = list.plus(feedBackList)
            runOnUiThread {
                databinidng.recyclerView.adapter = PreviewAdapter(list)
                loadingdilog.dismiss()
            }
        }

        databinidng.markComplte.setOnClickListener {
            templateModel.status = "completed"
            markComplete()
        }
    }

    private fun markComplete() {
        var dilog = BottomSheetDialog(this)
        dilog.setContentView(R.layout.mark_complte_bottom_dilog)
        var upload = dilog.findViewById<LinearLayout>(R.id.upload)
        var viewPdf = dilog.findViewById<LinearLayout>(R.id.viewPdf)
        var markComplete = dilog.findViewById<LinearLayout>(R.id.markComplte)
        var dowanload = dilog.findViewById<LinearLayout>(R.id.dowanloadpdf)
        dowanload?.setOnClickListener {
            dilog.dismiss()
            if (EasyPermissions.hasPermissions(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
                download()
            else {
                EasyPermissions.requestPermissions(
                    this,
                    "Please allow permission to dowanload report",
                    800,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        markComplete?.setOnClickListener {
            dilog.dismiss()
            showLoadingDilog(false)
            GlobalScope.launch {
                MedicalDataBase.getInstance(this@OverViewActivity).getDao().update(templateModel)
                runOnUiThread {
                    loadingdilog.dismiss()
                    var intent = Intent(this@OverViewActivity,ReportActivity::class.java)
                    intent.putExtra("templateModel",templateModel)
                    startActivity(intent)
                }
            }
        }
        upload?.setOnClickListener {
            dilog.dismiss()
            upload()
        }
        viewPdf?.setOnClickListener {
            openFile()
        }
        if (!dilog.isShowing)
            dilog.show()
    }

    //Facility Name-TemplateType-Report.pdf
    private fun openFile() {
        val file = File(
            Utils.createReportsFolder(this) + "/" + (templateModel.title?.split("/")?.get(0)
                ?: "") + "-Report.pdf"
        )
        if(file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val photoURI =
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(photoURI, "application/pdf")
            val target = Intent.createChooser(intent, "Open File")
            try {
                startActivity(target)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
            download()
        }

    }


    private fun upload() {
        showLoadingDilog(true)
        loadingProgress.max = list.size
        uploadViewModel.startUpload(templateModel)
        GlobalScope.launch {
            MedicalDataBase.getInstance(this@OverViewActivity).getDao().update(templateModel)
        }
    }

    @AfterPermissionGranted(800)
    private fun download() {
        showLoadingDilog(false)
        GlobalScope.launch {
            PdfCreater(
                templateModel,
                this@OverViewActivity
            )
            runOnUiThread { loadingdilog.dismiss() }
        }
    }

    private lateinit var statusTextView: TextView
    private lateinit var loadingProgress: ProgressBar
    private fun showLoadingDilog(boolean: Boolean) {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.setCancelable(false)
        statusTextView = loadingdilog.findViewById(R.id.status)!!
        loadingProgress = loadingdilog.findViewById(R.id.loading_progress)!!
        if (boolean) {
            loadingProgress.visibility = GONE
            statusTextView.visibility = GONE
        } else {
            loadingProgress.visibility = GONE
            statusTextView.visibility = GONE
        }
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }

    private lateinit var loadingdilog: BottomSheetDialog

    private fun setViewState(viewState: UploadingViewState) {
        when (viewState) {
            is Uploading -> {
                if (loadingdilog != null && !loadingdilog.isShowing)
                    loadingdilog.show()
                statusTextView.text = viewState.model
                loadingProgress.progress = loadingProgress.progress + 1;
            }
            is UploadingDone -> {
                loadingdilog.dismiss()
            }
            is ErrorUploading -> {
                loadingdilog.dismiss()
            }
        }
    }
}
