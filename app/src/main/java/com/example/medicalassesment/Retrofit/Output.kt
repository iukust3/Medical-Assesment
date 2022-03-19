package com.example.medicalassesment.Retrofit

sealed class Output {
    object Success : Output()
    data class Error(val error: Int) : Output()
}