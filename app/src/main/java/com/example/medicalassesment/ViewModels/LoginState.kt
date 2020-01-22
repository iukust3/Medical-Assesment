package com.example.medicalassesment.ViewModels

import com.example.medicalassesment.models.UserModel

sealed class LoginState
data class LoginError(val message: String?) : LoginState()
object LoadingLogin : LoginState()
data class SuccessLogin(val userModel: UserModel) : LoginState()