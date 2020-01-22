package com.example.medicalassesment.di



import com.example.medicalassesment.Activities.LoginActivity
import com.example.medicalassesment.Activities.OverViewActivity
import com.example.medicalassesment.Activities.SurveyActivity
import com.example.medicalassesment.Activities.TemplateActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class  ActivityBuildersModule {
    @ContributesAndroidInjector
    abstract fun bindSurvayActivity(): SurveyActivity

    @ContributesAndroidInjector
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun bindTamplateActivity(): TemplateActivity

    @ContributesAndroidInjector
    abstract fun bindOverViewActivity(): OverViewActivity
}
