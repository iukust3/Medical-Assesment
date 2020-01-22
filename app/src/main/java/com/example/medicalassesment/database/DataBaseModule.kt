package com.example.medicalassesment.database

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule(val application: Application) {
    @Provides
    @Singleton
    fun provideDataBaseRepositroy():DatabaseRepository=DatabaseRepository(application)
}