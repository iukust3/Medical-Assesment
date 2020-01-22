package com.example.medicalassesment.models

interface BaseQustion {

    var baseQustionType: Int
    fun getId(): Int
    fun setId(id: Int)
    fun getAnswer(): String?
    fun setAnswer(answers: String?)
    fun getImageuri(): ArrayList<String>?
    fun setImageuri(lsit: ArrayList<String>?)
    fun getGuideline(): String?
    fun getTittle(): String?
    fun getSubTittle(): String?
    fun getImageRequired(): String?
    fun getQuestionTypeId(): String?
    fun getDropDownItems(): ArrayList<String>?
    fun getToolId(): String?
    fun setDropDownItems(list: ArrayList<String>)
    fun setSubTittle(subtitl: String?)
    fun getPriority(): String?
    fun setPriority(prieroty: String?)
    fun getQ_Id(): String?
    fun setQ_Id(string: String);
    fun isUploaded(): Boolean;
    fun setUploaded(uploaded: Boolean);
}