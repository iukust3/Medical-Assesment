package com.example.medicalassesment.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.LoginError
import com.example.medicalassesment.ViewModels.LoginState
import com.example.medicalassesment.ViewModels.LoginViewModel
import com.example.medicalassesment.ViewModels.SuccessLogin
import com.example.medicalassesment.databinding.ActivityLoginBinding
import javax.inject.Inject
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog


class LoginActivity : AppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var loginViewModelProvider: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefHelper: PrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefHelper = PrefHelper(this)
        if (prefHelper.getIsLogined()) {
            startActivity(Intent(this, InspectionActivity::class.java))
            finish()
            return
        }
        (application as MyApplication).appComponent.inject(this)
        /* GlobalScope.launch {
             var db= MedicalDataBase.getInstance(this@LoginActivity)
            db.clearAllTables()
             truncateTable(this@LoginActivity,db.openHelper,"QuestionModel")
             truncateTable(this@LoginActivity,db.openHelper,"TemplateModel")
         }*/

        binding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_login
            )
        binding.progressCircular.visibility = GONE
        binding.signin.setOnClickListener {
            // startActivity(Intent(this, InspectionActivity::class.java))

            attemptLogin()
        }
        loginViewModelProvider =
            ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        loginViewModelProvider.getLoginStateLiveData().observe(this, Observer { setViewState(it) })
        //PdfCreater(ArrayList(),this)
    }

    private fun setViewState(loginState: LoginState) {
        when (loginState) {
            is SuccessLogin -> {
                loadingdilog.dismiss()
                prefHelper.setUserModel(loginState.userModel)
                prefHelper.setIsLogined(true)
                MyApplication.USER = loginState.userModel.data
                startActivity(Intent(this, InspectionActivity::class.java))
                finish()
            }
            is LoginError -> {
                loadingdilog.dismiss()
                Toast.makeText(this, "Error: " + loginState.message, Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Error " + loginState.message)
            }
        }
    }

    private fun truncateTable(
        context: Context,
        openHelper: SupportSQLiteOpenHelper,
        tableName: String
    ) {
        val database = SQLiteDatabase.openOrCreateDatabase(
            context.getDatabasePath(openHelper.databaseName), null
        )

        if (database != null) {
            database.execSQL(String.format("DELETE FROM %s;", tableName))
            database.execSQL(
                "UPDATE sqlite_sequence SET seq = 0 WHERE name = ?;",
                arrayOf(tableName)
            )
        }
    }

    private fun attemptLogin() {
        when {
            binding.email.text.isEmpty() -> binding.email.error = "Please enter Email"
            binding.password.text.isEmpty() -> binding.email.error = "Please enter Password"
            else -> {
                showLoadingDialog(false)
                loginViewModelProvider.attemptLogin(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }
        }
    }

    private lateinit var statusTextView: TextView
    private lateinit var loadingProgress: ProgressBar
    private fun showLoadingDialog(boolean: Boolean) {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.findViewById<Button>(R.id.button_ok)?.visibility = GONE
        loadingdilog.setCancelable(false)
        statusTextView = loadingdilog.findViewById(R.id.status)!!
        loadingProgress = loadingdilog.findViewById(R.id.loading_progress)!!
        if (boolean) {
            loadingProgress.visibility = View.VISIBLE
            statusTextView.visibility = View.VISIBLE
        } else {
            loadingProgress.visibility = GONE
            statusTextView.visibility = GONE
        }
        if (!loadingdilog.isShowing)
            loadingdilog.show()

    }

    private lateinit var loadingdilog: BottomSheetDialog

}
