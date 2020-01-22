package com.example.medicalassesment

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.example.medicalassesment.database.DataBaseModule
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.models.UserModel
import com.example.medicalassesment.Retrofit.APIinterFace
import com.example.medicalassesment.Retrofit.ApiClient
import com.example.medicalassesment.di.AppInjector
import com.example.medicalassesment.di.AppComponent
import com.example.medicalassesment.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        MyApplication.APPLICATION=this;
        Fabric.with(this, Crashlytics())
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .setDatabaseModule(DataBaseModule(this))
            .build()
        appComponent.inject(this)
        AppInjector.init(this)
        try {
            USER = PrefHelper(this).getUserModel().data[0]
        } catch (e: Exception) {
        }
        GlobalScope.launch {
            try {
                ApiClient.getApiClient().create(APIinterFace::class.java).Login("dsgv2?", "332")
            } catch (ex: Exception) {
                Log.e("TAG","error "+ex.message)
            }
        }
    }

    companion object {
        @Volatile
        lateinit var USER: UserModel.Data
        @JvmStatic
        @Volatile
        lateinit var APPLICATION:MyApplication;
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}