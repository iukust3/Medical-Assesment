package com.example.medicalassesment.Activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.R
import com.example.medicalassesment.ReportsActivity
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.service.UploadingService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import java.util.*


open class BaseActivity : AppCompatActivity() {
    private lateinit var navigationItemSelectedListener: NavigationView.OnNavigationItemSelectedListener
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var mainLayout: LinearLayout
    private lateinit var currentActivity: AppCompatActivity
    private lateinit var currentClassName: String
    private lateinit var bottomnavigation: BottomNavigationView
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var timeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        prefHelper = PrefHelper(this)
        timeText = findViewById(R.id.timer)
    }

    fun startTimer() {
        counter = 0
        timer = object : CountDownTimer(400000, 1000) {
            override fun onTick(p0: Long) {
                counter++
            }

            override fun onFinish() {
                timer.start()
            }

        }
        timer.start()
        isStared = true
    }

    fun pauseTimer() {
        try {
            timer.cancel()
            prefHelper.setTimer(templateModel.title.toString(), counter)
            counter = 0
            isStared = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun iniateUi(layout: View) {
        iniateDrarwer()
        mainLayout.addView(layout, 0)
        setSupportActionBar(findViewById(R.id.toolbar))

        this.actionBarDrawerToggle.let { drawerLayout.addDrawerListener(it) }
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    protected fun setCurrantClassName(className: AppCompatActivity) {
        try {
            this.currentActivity = className
            currentClassName = currentActivity.localClassName.split(".")[1]
        } catch (e: Exception) {
            currentClassName = currentActivity.localClassName
        }
        if (currentClassName != SurveyActivity::class.java.simpleName) {
            pauseTimer()
        } else {
            if (currentClassName == SurveyActivity::class.java.simpleName)
                if (!isStared)
                    startTimer()
        }
    }


    private fun iniateDrarwer() {
        drawerLayout = findViewById(R.id.drawerlayout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name)
        navigationView = findViewById(R.id.nav_view)
        mainLayout = findViewById(R.id.main_content)
        bottomnavigation = findViewById(R.id.bottom_nvagation)
        navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.home -> {
                    try {
                        if (currentClassName != InspectionActivity::class.java.simpleName) {
                            currentActivity.finish()
                            isHome = true
                        }
                    } catch (e: Exception) {
                    }
                }
                R.id.inspection -> {
                    if (currentClassName != FecilityActivity::class.java.simpleName || currentClassName != TemplateActivity::class.java.simpleName) {
                        if (currentClassName == InspectionActivity::class.java.simpleName) {
                            startActivity(Intent(this, FecilityActivity::class.java))
                        } else {
                            startActivity(Intent(this, FecilityActivity::class.java))
                            currentActivity.finish()
                        }
                    }
                }
                R.id.report -> {
                    if (currentClassName != ReportsActivity::class.java.simpleName || currentClassName != TemplateActivity::class.java.simpleName) {
                        if (currentClassName == InspectionActivity::class.java.simpleName) {
                            startActivity(Intent(this, ReportsActivity::class.java))
                        } else {
                            startActivity(Intent(this, ReportsActivity::class.java))
                            currentActivity.finish()
                        }
                    }
                }
                R.id.sync -> {
                    upload()
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileSetting::class.java))
                }
            }
            true
        }
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener)
        bottomnavigation.setOnNavigationItemSelectedListener {
            navigationItemSelectedListener.onNavigationItemSelected(it)
            true
        }
    }

    override fun onResume() {
        if (isHome)
            if (currentClassName != InspectionActivity::class.java.simpleName) {
                currentActivity.finish()
            } else isHome = false
        super.onResume()
    }

    protected fun setSelectedNavigationItem(item: Int) {
        bottomnavigation.setOnNavigationItemSelectedListener(null)
        bottomnavigation.selectedItemId = item
        navigationView.setNavigationItemSelectedListener(null)
        navigationView.setCheckedItem(item)
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener)
        bottomnavigation.setOnNavigationItemSelectedListener {
            navigationItemSelectedListener.onNavigationItemSelected(it)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(
            item
        )
    }

    open fun upload() {
        /* uploadViewModel = ViewModelProvider(this).get(UploadViewModel::class.java)
         uploadViewModel.getViewState().observe(this, Observer {
             UploadingService.INSTANCE.getViewState(it)
         })*/
        showLoadingDilog(true)
        try {

            ContextCompat.startForegroundService(this, Intent(this, UploadingService::class.java))
        } catch (ex: Exception) {
        }
        uploadStatus = object : UploadStatus {
            override fun uploadStarting(
                maxProgress: Int,
                templateModel: TemplateModel
            ) {
                //  uploadViewModel.startUpload(templateModel)
                loadingProgress.max = maxProgress
                loadingProgress.progress = 0
            }

            override fun uploading(status: String) {
                runOnUiThread {
                    if (!loadingdilog.isShowing)
                        loadingdilog.show()
                    statusTextView.visibility = VISIBLE
                    statusTextView.text = status
                    loadingProgress.progress = loadingProgress.progress + 1
                }
            }

            override fun uploaded(message: String) {
                runOnUiThread {
                    statusProgress.visibility = GONE
                    statusTextView.visibility = VISIBLE
                    statusTextView.text = message
                    buttonOk.visibility = VISIBLE
                    loadingProgress.progress = loadingProgress.max
                }
            }

            override fun failed() {
                //   loadingdilog.dismiss()
            }

        }
    }

    fun hideLoadingDilog(){
        if(loadingdilog.isShowing)
            loadingdilog.dismiss()
    }
    private fun setViewState(viewState: UploadingViewState) {
        when (viewState) {
            is Uploading -> {
                if (loadingdilog != null && !loadingdilog.isShowing)
                    loadingdilog.show()
                statusTextView.text = viewState.model
                loadingProgress.progress = loadingProgress.progress + 1
            }
            is UploadingDone -> {
                loadingdilog.dismiss()
            }
            is ErrorUploading -> {
                loadingdilog.dismiss()
            }
        }
    }

    private lateinit var statusTextView: TextView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var statusProgress: ProgressBar
    private lateinit var buttonOk: Button
    private lateinit var loadingdilog: BottomSheetDialog
     private fun showLoadingDilog(boolean: Boolean) {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.setCancelable(false)
        statusTextView = loadingdilog.findViewById(R.id.status)!!
        loadingProgress = loadingdilog.findViewById(R.id.loading_progress)!!
        statusProgress = loadingdilog.findViewById(R.id.status_progress)!!
        buttonOk = loadingdilog.findViewById(R.id.button_ok)!!

         buttonOk.setOnClickListener {
            loadingdilog.dismiss()
        }
        if (boolean) {
            loadingProgress.visibility = View.VISIBLE
            statusTextView.visibility = View.VISIBLE
            buttonOk.visibility= VISIBLE
        } else {
            loadingProgress.visibility = GONE
            statusTextView.visibility = GONE
            buttonOk.visibility= GONE
        }
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }

    companion object {
        private var isHome = false
        lateinit var uploadStatus: UploadStatus
        lateinit var timer: CountDownTimer
        private var isStared = false
        private var counter: Long = 0
        private lateinit var prefHelper: PrefHelper
        lateinit var templateModel: TemplateModel
    }

    interface UploadStatus {
        fun uploadStarting(
            maxProgress: Int,
            templateModel: TemplateModel
        )

        fun uploading(status: String)
        fun uploaded(message: String)
        fun failed()
    }
}
