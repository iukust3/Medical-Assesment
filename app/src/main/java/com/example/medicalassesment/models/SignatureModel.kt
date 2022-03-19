package com.example.medicalassesment.models

import android.util.Log

class SignatureModel() {
    var indexOfName: Int = -1
   lateinit var feedBackModel:FeedBackModel
   lateinit var nameModel:FeedBackModel
    constructor(feedBackModel: FeedBackModel) : this(){
        this.feedBackModel=feedBackModel
    }
    constructor(int: Int) : this(){
        this.indexOfName=int
    }
    fun nameModelIsNotNullOrEmpty():Boolean{
        return try {
            nameModel.guidlines
            Log.e("TAG","Not Empty or null")
            true
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("TAG"," Empty or null")
            false
        }
    }
}