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
    private val IsLogin = "IsLogin"
    private val TIMER = "Timer"

    init {
        sharedPreferences = context.getSharedPreferences(PrefName, 0)
    }

    public fun getString(key: String, default: String): String {
        return sharedPreferences.getString(key, default).toString()
    }

    fun getIsLogined(): Boolean {
        return sharedPreferences.getBoolean(IsLogin, false)
    }

    fun setIsLogined(value: Boolean) {
        sharedPreferences.edit().putBoolean(IsLogin, value).apply()
    }

    fun getTimer(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }
    fun getTime(key: String):String{
        val  counter=sharedPreferences.getLong(key,0)
        var sec= counter / 2

        return String.format(
            "%02d:%02d:%02d",
            sec/ 3600,
            sec % 3600 / 60,
            sec % 60
        );
    }
    fun getTime( long: Long):String{
       var sec= long / 2

        return String.format(
            "%02d:%02d:%02d",
            sec/ 3600,
            sec % 3600 / 60,
            sec % 60
        );
    }
    fun setTimer(key:String,value: Long) {
        val preVoisValue=sharedPreferences.getLong(key,0)
        sharedPreferences.edit().putLong(key,(value+preVoisValue)).apply()
    }

    fun getUserModel(): UserModel {
        return try {
            var userModel = Gson().fromJson<UserModel>(
                sharedPreferences.getString(USER_KEY, ""),
                UserModel::class.java
            )
            userModel
        } catch (e: Exception) {
            UserModel()
        }
    }

    fun getUserModel(email: String): UserModel {
        return try {
            var userModel = Gson().fromJson<UserModel>(
                sharedPreferences.getString(email, ""),
                UserModel::class.java
            )
            userModel
        } catch (e: Exception) {
            UserModel()
        }
    }

    fun setUserModel(userModel: UserModel) {
        sharedPreferences.edit().putString(USER_KEY, Gson().toJson(userModel)).apply()
        sharedPreferences.edit().putString(userModel.data.email, Gson().toJson(userModel)).apply()
    }

    fun logout() {
        //sharedPreferences.edit().putString(USER_KEY, "").apply()
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

    /*  fun getFecility(): Fecilities {
          return Gson().fromJson(
              sharedPreferences.getString("Fecilites", "{}"),
              Fecilities::class.java
          )
      }*/
    fun getFecility(catogarie: String): Fecilities {
        val facilities: Fecilities = Gson().fromJson(
            sharedPreferences.getString("Fecilites", "{}"),
            Fecilities::class.java
        )
        facilities.facilities = facilities.facilities.filter { it.category.equals(catogarie, true) }
        return facilities
    }

    fun setFacility(fecilities: Fecilities) {
        sharedPreferences.edit().putString("Fecilites", Gson().toJson(fecilities)).apply()
    }

    fun setCategories(stateLga: String) {
        sharedPreferences.edit().putString(CATOGARIES, stateLga).apply()
    }

    fun getTempleteData(templete: String): String =
        sharedPreferences.getString(templete, "{}").toString()

    fun setTempleteData(templete: String, data: String) {
        sharedPreferences.edit().putString(templete, data).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}