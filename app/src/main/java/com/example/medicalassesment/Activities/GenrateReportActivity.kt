package com.example.medicalassesment.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.medicalassesment.BuildConfig
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.database.Dao
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.databinding.ActivityReportBinding
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.reportGenration.PdfCreater
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class GenrateReportActivity : BaseActivity() {
    private lateinit var bainding: ActivityReportBinding
    private lateinit var dao: Dao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = MedicalDataBase.getInstance(this).getDao()
        bainding =
            ActivityReportBinding.inflate(
                layoutInflater,
                null, false
            )
        iniateUi(bainding.root)
        setCurrantClassName(this)
        bainding.generateReport.setOnClickListener {
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
                    "Please allow permission to download report",
                    800,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        bainding.openPdf.setOnClickListener {
            if (EasyPermissions.hasPermissions(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
                openFile()
            else {
                EasyPermissions.requestPermissions(
                    this,
                    "Please allow permission to download report",
                    800,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        bainding.sendReport.setOnClickListener {
            upload()
        }

        templateModel = intent.getParcelableExtra("TemplateModel")!!
        checkRecommendation()
        val file = File(
            Utils.createReportsFolder(this) + "/" + (templateModel.title?.split("/")?.get(0)
                ?: "") + "-Report.pdf"
        )
        if (file.exists()) {
            bainding.openPdf.visibility = VISIBLE
           // bainding.generateReport.visibility= GONE
            bainding.sendReport.visibility = VISIBLE
        } else {
            bainding.openPdf.visibility = GONE
            bainding.sendReport.visibility = GONE
        }
    }

    @AfterPermissionGranted(800)
    private fun download() {
        showLoadingDialog(false)
        GlobalScope.launch {
            PdfCreater(
                templateModel,
                this@GenrateReportActivity
            )
            runOnUiThread {
                val file = File(
                    Utils.createReportsFolder(this@GenrateReportActivity) + "/" + (templateModel.title?.split(
                        "/"
                    )?.get(0)
                        ?: "") + "-Report.pdf"
                )
                if (file.exists()) {
                    bainding.openPdf.visibility = VISIBLE
                    bainding.sendReport.visibility = VISIBLE
                   bainding.generateReport.visibility= GONE
                } else {
                    bainding.openPdf.visibility = GONE
                    bainding.sendReport.visibility = GONE
                }
                loadingdilog.dismiss()
            }
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
            loadingProgress.visibility = VISIBLE
            statusTextView.visibility = VISIBLE
        } else {
            loadingProgress.visibility = GONE
            statusTextView.visibility = GONE
        }
        if (!loadingdilog.isShowing)
            loadingdilog.show()

    }

    private lateinit var loadingdilog: BottomSheetDialog

    private fun checkRecommendation() {
        val list = dao.getFaildItems(templateModel.id.toString(), Constant.HARD_DEAL_BREKER)
        val listsoft = dao.getFaildItems(templateModel.id.toString(), Constant.SOFT_DEAL_BREKER)
        val listNormal = dao.getFaildItems(templateModel.id.toString(), Constant.NORMAL_DEAL_BREKER)

        when {
            list.isNotEmpty() -> {
                bainding.recomendationResult.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorRed
                    )
                )
                bainding.recomendationResult.text = "NO RECOMMENDATION"
            }

            listsoft.isNotEmpty()
            -> {
                bainding.recomendationResult.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.yellow_600
                    )
                )
                bainding.recomendationResult.text = "PROVISIONAL RECOMMENDATION"
            }
            listNormal.isNotEmpty()
            -> {
                bainding.recomendationResult.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.green_500
                    )
                )
                bainding.recomendationResult.text = "FULL RECOMMENDATION"
            }
            else-> {
                bainding.recomendationResult.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.green_500
                    )
                )
                bainding.recomendationResult.text = "FULL RECOMMENDATION"
            }
        }
    }

    @AfterPermissionGranted(800)
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


}
