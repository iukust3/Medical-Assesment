package com.example.medicalassesment.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.database.DatabaseRepository
import com.example.medicalassesment.models.UserModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Retrofit.APIinterFace
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val apIinterFace: APIinterFace,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val loginStateLiveData: MutableLiveData<LoginState> = MutableLiveData()
    private val prefHelper = PrefHelper(MyApplication.APPLICATION)
    fun attemptLogin(email: String, password: String) {
        loginStateLiveData.value = LoadingLogin
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            if (prefHelper.getUserModel(email).equals(UserModel.Data(0, "", "", email, password,"",""))) {
                val userModel = prefHelper.getUserModel(email)
                success(userModel)
             } else {
                val response = apIinterFace.Login(email, password);
                if(response.code()==200) {
                    var userModel = response.body()
                    userModel?.data?.password = password
                    databaseRepository.medicalDataBase.clearAllTables()
                    PrefHelper(databaseRepository.application).clear()
                    /*databaseRepository.medicalDataBase.clearAllTables()
                databaseRepository.truncateTable(
                    databaseRepository.medicalDataBase.openHelper,
                    "QuestionModel"
                )
                databaseRepository.truncateTable(
                    databaseRepository.medicalDataBase.openHelper,
                    "TemplateModel"
                )
                databaseRepository.truncateTable(
                    databaseRepository.medicalDataBase.openHelper,
                    "FeedBackModel"
                )
                databaseRepository.truncateTable(
                    databaseRepository.medicalDataBase.openHelper,
                    "PreliminaryInfoModel"
                )
                databaseRepository.truncateTable(
                    databaseRepository.medicalDataBase.openHelper,
                    "Facility"
                )
                databaseRepository.truncateTable(
                    databaseRepository.medicalDataBase.openHelper,
                    "room_master_table"
                )*/
                    userModel?.let { success(it) }
                }else {
                    success(UserModel())
                }

            }
        }
    }

    private fun success(userModel: UserModel) {
        if (userModel.data.id != 0) {
            MyApplication.USER = userModel.data
            loginStateLiveData.value =
                SuccessLogin(userModel)
        } else {
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