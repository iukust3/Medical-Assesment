package com.example.medicalassesment.models

import android.util.Log
import com.google.gson.Gson

class UserModel {
    var data: Data = Data()

    class Data constructor(
        var id: Int = 0,
        var firstname: String,
        var surname: String,
        var email: String,
        var password: String,
        var department:String,
        var zone:String
    ) {

        constructor() : this(0, "", "", "", "","","")

        override fun equals(other: Any?): Boolean {
             return ((other as Data).email == this.email && other.password == this.password)
        }
    }

    override fun equals(other: Any?): Boolean {
        return ((other as Data).email == this.data.email && other.password == this.data.password)
    }
}