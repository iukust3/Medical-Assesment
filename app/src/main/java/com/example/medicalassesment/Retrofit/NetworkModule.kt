package com.example.medicalassesment.Retrofit

import com.example.medicalassesment.di.AppInjector
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NetworkModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideApiInterface(): APIinterFace= ApiClient.getApiClient().create(APIinterFace::class.java)
}