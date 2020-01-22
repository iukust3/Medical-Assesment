package com.example.medicalassesment.Retrofit

import androidx.annotation.NonNull
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import javax.xml.datatype.DatatypeConstants.SECONDS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import java.util.logging.Level


class ApiClient {
    companion object {
        private val BASE_URL = "http://moonligthing.com/pcninspection/public/api/"
        @Volatile
        @JvmStatic
        private lateinit var retrofit: Retrofit

        fun getApiClient(): Retrofit {
            return try {
                if (retrofit == null) {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .addInterceptor(HttpIntercepter())
                        .readTimeout(100, TimeUnit.SECONDS).build()
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build()
                }
                retrofit
            } catch (e: Exception) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(HttpIntercepter())
                    .addInterceptor(HttpLoggingInterceptor().apply { level=HttpLoggingInterceptor.Level.BODY })
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15,TimeUnit.SECONDS).build()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                retrofit
            }
        }

    }
}