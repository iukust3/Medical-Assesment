package com.example.medicalassesment.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.BuildConfig
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.ViewModels.ErrorUploading
import com.example.medicalassesment.ViewModels.Uploading
import com.example.medicalassesment.ViewModels.UploadingDone
import com.example.medicalassesment.ViewModels.UploadingViewState
import com.example.medicalassesment.adapter.PreviewAdapter
import com.example.medicalassesment.database.Dao
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.databinding.ActivityOverViewBinding
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.FeedBackModel
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.reportGenration.PdfCreater
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import java.io.File


class OverViewActivity : BaseActivity() {
    companion object {
        lateinit var orignalModel: TemplateModel
    }
    private var list: List<BaseQustion> = ArrayList()
     private lateinit var dao: Dao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = MedicalDataBase.getInstance(this).getDao()
        val databinding: ActivityOverViewBinding =
            ActivityOverViewBinding.inflate(
                layoutInflater,
                null, false
            )
        iniateUi(databinding.root)
        pauseTimer()
        setCurrantClassName(this)
        (application as MyApplication).appComponent.inject(this)
         templateModel = intent.getParcelableExtra("TemplateModel")!!
        orignalModel = intent.getParcelableExtra("TemplateModel")!!
        // orignalModel.status = "InProgress"
        databinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?

        databinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        showLoadingDialog(false)

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
                databinding.recyclerView.adapter = PreviewAdapter(list)
                loadingdilog.dismiss()
            }
        }

        databinding.markComplte.setOnClickListener {
            var alertDialog = AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm")
            alertDialog.setMessage("Are you sure you want to complete this inspection?\n" +
                    "Note that you will not be able to make changes to this inspection once it is complete.\n\n")
            alertDialog.setPositiveButton(
                "Yes"
            ) { p0, _ ->
                run {
                    templateModel.status = "completed"
                    markComplete()
                    p0.dismiss()
                }
            }
            alertDialog.setNegativeButton(
                "NO"
            ) { p0, _ -> p0.dismiss() }
            var dialog=alertDialog.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).background=ContextCompat.getDrawable(
                this,
                R.drawable.option_green
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            setMargins( dialog.getButton(AlertDialog.BUTTON_NEGATIVE),0,0,10,0)
            setMargins( dialog.getButton(AlertDialog.BUTTON_POSITIVE),10,0,0,0)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).background=ContextCompat.getDrawable(
                this,
                R.drawable.option_red
            )

        }
        databinding.makeChanges.setOnClickListener { onBackPressed() }
    }
    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }
    private fun markComplete() {
        showLoadingDialog(false)
        GlobalScope.launch {
            MedicalDataBase.getInstance(this@OverViewActivity).getDao().update(templateModel)
        }
        loadingdilog.dismiss()
        var intent = Intent(this@OverViewActivity, GenrateReportActivity::class.java)
        intent.putExtra("TemplateModel", templateModel)
        startActivity(intent)
        finish()
        /*val intent = Intent(this, ReportActivity::class.java)
        intent.putExtra("templateModel", templateModel)
        startActivity(intent)*/
        /*   var dilog = BottomSheetDialog(this)
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
               dilog.show()*/
    }

    override fun onBackPressed() {
       var intent=Intent(this,SurveyActivity::class.java)
        intent.putExtra("TemplateModel", orignalModel)
        intent.putExtra(SurveyActivity.ISFromEdit, true)
        startActivity(intent)
        finish()
    }
    //Facility Name-TemplateType-Report.pdf
    private fun openFile() {
        val file = File(
            Utils.createReportsFolder(this) + "/" + (templateModel.title?.split("/")?.get(0)
                ?: "") + "-Report.pdf"
        )
        if (file.exists()) {
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
        } else {
            download()
        }

    }


    override fun upload() {
        showLoadingDialog(true)
        loadingProgress.max = list.size
        GlobalScope.launch {
            MedicalDataBase.getInstance(this@OverViewActivity).getDao().update(templateModel)
        }
    }

    @AfterPermissionGranted(800)
    private fun download() {
        showLoadingDialog(false)
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
    private fun showLoadingDialog(boolean: Boolean) {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.findViewById<Button>(R.id.button_ok)?.visibility= GONE
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
