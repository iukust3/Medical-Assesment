package com.example.medicalassesment.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.database.DatabaseRepository
import com.example.medicalassesment.models.UserModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Retrofit.APIinterFace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val apIinterFace: APIinterFace,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val loginStateLiveData: MutableLiveData<LoginState> = MutableLiveData()

    fun attemtLogin(email: String, password: String) {
        loginStateLiveData.value = LoadingLogin
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = apIinterFace.Login(email, password);
            var userModel=response.body()
            userModel?.let { success(it) }
        }
    }

    private fun success(userModel: UserModel) {
        if (userModel.data.isNotEmpty()) {
            MyApplication.USER=userModel.data[0]
            loginStateLiveData.value =
                SuccessLogin(userModel)
        }else{
            loginStateLiveData.value =
               LoginError("User name or email is incorrect")
        }
    }

    private fun onError(exception: Throwable) {
        loginStateLiveData.value = LoginError("Error " + exception.message)
        exception.printStackTrace()
    }

    fun getLoginStateLiveData(): LiveData<LoginState> {
        return loginStateLiveData
    }
}