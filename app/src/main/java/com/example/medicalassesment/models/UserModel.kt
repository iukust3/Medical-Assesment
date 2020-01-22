package com.example.medicalassesment.models

class UserModel() {
    var data: List<Data> = ArrayList()
    class Data() {
        var id: Int = 0
        lateinit var firstname: String
        lateinit var surname: String
        lateinit var email: String
    }
}