package com.example.medicalassesment.models

import android.util.Log
import androidx.room.*
import com.example.medicalassesment.Utials.Constant
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class PreliminaryInfoModel : BaseQustion {
    @Ignore
    override var baseQustionType: Int = 0
    @Expose
    @SerializedName("questiontype")
    var questiontype: String? = null
    @Expose
    @SerializedName("template")
    var template: String? = null
    @Expose
    @SerializedName("preliminery_info_id")
    var preliminery_info_id: Int = 0
    @Expose
    @SerializedName("priority")
    private var priority: String? = null
    @Expose
    @SerializedName("question_type_id")
    var question_type_id: String? = null
    @Expose
    @SerializedName("subtitle")
    private var subTittle: String? = null
    @Expose
    @SerializedName("title")
    var title: String? = null
    @Expose
    @SerializedName("template_id")
    var template_id: String? = null
    @Expose
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0
    private var answer: String = "";
    private var isUploaded = false;
    @TypeConverters(value = [ListToStringConverter::class])
    var imageUris: ArrayList<String>? = ArrayList()
    @TypeConverters(value = [ListToStringConverter::class])
    @SerializedName("dropdown")
    private var dropDownItems: ArrayList<String>? = ArrayList()

    override fun getQ_Id(): String? {
        return preliminery_info_id.toString()
    }

    override fun isUploaded(): Boolean {
        return this.isUploaded;
    }

    override fun setUploaded(uploaded: Boolean) {
        this.isUploaded = uploaded;
    }

    override fun setQ_Id(string: String) {
        this.preliminery_info_id = string.toInt()
    }

    override fun getId(): Int {
        return id
    }

    override fun setPriority(prieroty: String?) {
        this.priority = prieroty
    }

    override fun getPriority(): String? {
        return this.priority
    }

    override fun getAnswer(): String? {
        return this.answer
    }

    override fun setAnswer(answers: String?) {
        this.answer = answers.toString()
    }

    override fun getImageuri(): ArrayList<String> {
        this.imageUris.let { return it ?: ArrayList() }
    }

    override fun setImageuri(lsit: ArrayList<String>?) {
        this.imageUris = lsit
    }

    override fun setId(id: Int) {
        this.id = id
    }

    override fun getGuideline(): String? {
        return null
    }

    override fun getTittle(): String? {
        return this.title
    }

    override fun getSubTittle(): String? {
        return this.subTittle
    }

    override fun setSubTittle(subtitl: String?) {
        this.subTittle = subtitl
    }

    override fun getImageRequired(): String? {
        return "0"
    }


    override fun getQuestionTypeId(): String? {
        if (this.question_type_id == null) {
            when (this.questiontype) {
                "textarea"-> return Constant.QUESTION_TYPE_TEXTAREA
                "signature"-> return Constant.QUSTION_TYPE_SIGNATURE
                "dropdown"-> return Constant.QUSTION_TYPE_DROPDOWN
                "textbox"-> return Constant.QUESTION_TYPE_EDITTEXT
                "past date"-> return Constant.QUSTION_TYPE_DATEPAST
                "future date"-> return Constant.QUSTION_TYPE_DATEFUTURE
                "phone"-> return Constant.QUSTION_TYPE_PHONE
                "email"-> return Constant.QUSTION_TYPE_EMAIL
                "Section Title"-> return Constant.QUSTION_TYPE_SECTIONTITTLE
            }
        }
        return this.question_type_id
    }

    override fun getDropDownItems(): ArrayList<String>? {
        try {
            if (this.dropDownItems.isNullOrEmpty())
                return ArrayList()
        } catch (e: Exception) {
            Log.e("TAG", "excepte " + e.message)
            return ArrayList()
        }
        return this.dropDownItems
    }

    override fun setDropDownItems(list: ArrayList<String>) {
        this.dropDownItems = list
    }

    override fun getToolId(): String? {
        return this.template_id
    }

    class ListToStringConverter {
        @TypeConverter
        fun stringToList(value: String): ArrayList<String> {
            return Gson().fromJson<ArrayList<String>>(value, ArrayList<String>()::class.java)
        }

        @TypeConverter
        fun listToString(value: List<String>): String {
            try {
                if (value == null)
                    return "[]"
                return Gson().toJson(value)
            } catch (e: Exception) {
                e.printStackTrace()
                return "[]"
            }
        }
    }

}


