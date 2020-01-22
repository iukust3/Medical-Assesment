package com.example.medicalassesment.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.medicalassesment.R
import com.example.medicalassesment.databinding.ActivityReportBinding
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.reportGenration.PdfCreater
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ReportActivity : AppCompatActivity() {
    private lateinit var templateModel: TemplateModel
    private lateinit var dataBainding: ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBainding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        dataBainding.generateReport.setOnClickListener {
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
        templateModel = intent.getParcelableExtra("templateModel")
    }

    @AfterPermissionGranted(800)
    private fun download() {
        showLoadingDilog(false)
        GlobalScope.launch {
            PdfCreater(
                templateModel,
                this@ReportActivity
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
            loadingProgress.visibility = View.GONE
            statusTextView.visibility = View.GONE
        } else {
            loadingProgress.visibility = View.GONE
            statusTextView.visibility = View.GONE
        }
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }

    private lateinit var loadingdilog: BottomSheetDialog

}
