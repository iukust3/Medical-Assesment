package com.example.medicalassesment.Helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.medicalassesment.models.Catogaries
import com.example.medicalassesment.models.Fecilities
import com.example.medicalassesment.models.StateLga
import com.example.medicalassesment.models.UserModel
import com.google.gson.Gson

class PrefHelper(context: Context) {
    private var PrefName = "Medical_Assessment"
    private val USER_KEY = "User";
    private var sharedPreferences: SharedPreferences
    private val STATE = "State"
    private val CATOGARIES = "Categories"

    init {
        sharedPreferences = context.getSharedPreferences(PrefName, 0)
    }

    public fun getString(key: String, default: String): String {
        return sharedPreferences.getString(key, default)
    }

    fun getUserModel(): UserModel {
        return try {
            Log.e("TAG", " " + sharedPreferences.getString(USER_KEY, ""))
            var userModel = Gson().fromJson<UserModel>(
                sharedPreferences.getString(USER_KEY, ""),
                UserModel::class.java
            )
            userModel
        } catch (e: Exception) {
            UserModel()
        }
    }

    fun setUserModel(userModel: UserModel) {
        sharedPreferences.edit().putString(USER_KEY, Gson().toJson(userModel)).apply()
    }

    fun logout() {
        sharedPreferences.edit().putString(USER_KEY, "").apply()
    }

    fun getState(): StateLga {
        return Gson().fromJson(sharedPreferences.getString(STATE, "{}"), StateLga::class.java)
    }


    fun setState(stateLga: String) {
        sharedPreferences.edit().putString(STATE, stateLga).apply()
    }

    fun getCatogaries(): Catogaries {
        return Gson().fromJson(
            sharedPreferences.getString(CATOGARIES, "{}"),
            Catogaries::class.java
        )
    }

    fun getCatogariesArray(): Array<String> {
        var list = getCatogaries().categories
        var array = Array(list.size) {
            ""
        }
        for (i in list.indices) {
            array[i] = list[i].name
        }
        return array
    }

    fun getFecility(): Fecilities {
        return Gson().fromJson(
            sharedPreferences.getString("Fecilites", "{}"),
            Fecilities::class.java
        )
    }

    fun setFecility(fecilities: Fecilities) {
        sharedPreferences.edit().putString("Fecilites", Gson().toJson(fecilities)).apply()
    }

    fun setCatogaries(stateLga: String) {
        sharedPreferences.edit().putString(CATOGARIES, stateLga).apply()
    }

    fun getTempleteData(templete: String): String = sharedPreferences.getString(templete, "{}")
    fun setTempleteData(templete: String, data: String) {
        sharedPreferences.edit().putString(templete, data).apply()
    }
}