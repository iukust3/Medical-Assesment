package com.example.medicalassesment.di

import android.app.Application
import com.example.medicalassesment.Activities.*
import com.example.medicalassesment.database.DataBaseModule
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Retrofit.NetworkModule
import com.example.medicalassesment.service.UploadingService
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBuildersModule::class, NetworkModule::class, DataBaseModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(application: MyApplication)
    fun inject(activity: LoginActivity)
    fun inject(activity: SurveyActivity)
    fun inject(inspectionActivity: InspectionActivity)
    fun inject(templateActivity: TemplateActivity)
    fun inject(overViewActivity: OverViewActivity)
    fun inject(fecilityActivity: FecilityActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun setDatabaseModule(dataBaseModule: DataBaseModule): Builder
        fun build(): AppComponent
    }
}
