package com.example.medicalassesment.Retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class HttpIntercepter : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var stringurl = request.url.toString()
        stringurl = stringurl.replace("%26", "&")
        stringurl = stringurl.replace("%40", "@")

        var newRequest =
            request.newBuilder().url(stringurl).build()

        return chain.proceed(newRequest)
    }

}