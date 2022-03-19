package com.example.medicalassesment.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.databinding.DataBindingUtil
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.databinding.ActivityProfileSettingBinding

class ProfileSetting : AppCompatActivity() {
    private lateinit var profileSettingBinding: ActivityProfileSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileSettingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile_setting)
        profileSettingBinding.userModel = MyApplication.USER
        setSupportActionBar(profileSettingBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}