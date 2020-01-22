package com.example.medicalassesment.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.medicalassesment.R
import com.example.medicalassesment.service.UploadingService
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        android.os.Handler().postDelayed({
               startActivity(Intent(this, SurveyActivity::class.java))
        },3000)

    }
}
