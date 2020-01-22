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
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.LoginError
import com.example.medicalassesment.ViewModels.LoginState
import com.example.medicalassesment.ViewModels.LoginViewModel
import com.example.medicalassesment.ViewModels.SuccessLogin
import com.example.medicalassesment.databinding.ActivityLoginBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import android.content.Context


class LoginActivity : AppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var loginViewModelProvider: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefHelper: PrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefHelper = PrefHelper(this)
        if (prefHelper.getUserModel().data.isNotEmpty()) {
            startActivity(Intent(this, InspectionActivity::class.java))
            finish()
            return
        }
        (application as MyApplication).appComponent.inject(this)
        GlobalScope.launch {
            var db= MedicalDataBase.getInstance(this@LoginActivity)
           db.clearAllTables()
            truncateTable(this@LoginActivity,db.openHelper,"QuestionModel")
            truncateTable(this@LoginActivity,db.openHelper,"TemplateModel")
        }

        binding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_login
            )
        binding.progressCircular.visibility=GONE
        binding.signin.setOnClickListener {
            // startActivity(Intent(this, InspectionActivity::class.java))
            attemptLogin()
        }
        loginViewModelProvider =
            ViewModelProviders.of(this, viewModelFactory)[LoginViewModel::class.java]
        loginViewModelProvider.getLoginStateLiveData().observe(this, Observer { setViewState(it) })
        //PdfCreater(ArrayList(),this)
    }

    private fun setViewState(loginState: LoginState) {
        when (loginState) {
            is SuccessLogin -> {
                prefHelper.setUserModel(loginState.userModel)
                startActivity(Intent(this, InspectionActivity::class.java))
                finish()
            }
            is LoginError -> {
                Toast.makeText(this, "Error: " + loginState.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun truncateTable(context: Context, openHelper: SupportSQLiteOpenHelper, tableName: String) {
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
                loginViewModelProvider.attemtLogin(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }
        }
    }
}
