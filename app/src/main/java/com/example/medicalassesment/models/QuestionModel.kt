package com.example.medicalassesment.models

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class QuestionModel : BaseQustion {
    @Ignore
    override var baseQustionType = 1
    @Expose
    @SerializedName("questiontype")
    var questiontype: String? = null
    @Expose
    @SerializedName("template")
    var template: String? = null
    @Expose
    @SerializedName("fail")
    var fail: String? = null
    @Expose
    @SerializedName("defaultAnswer")
    var defaultAnswer: String? = null
    @Expose
    @SerializedName("isimagerequired")
    var isimagerequired: String? = null
    @Expose
    @SerializedName("priority")
    private var priority: String? = null
    @Expose
    @SerializedName("comments")
    var comments: String? = null
    @Expose
    @SerializedName("question_type_id")
    var type: String? = null
    @Expose
    @SerializedName("guidlines")
    var guidlines: String? = null
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
    @SerializedName("question_id")
    var question_id: String? = null
    @Expose
    @SerializedName("dealBreaker")
    var dealBreaker: String? = null
    @Expose
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0
    private var answer: String = "";

    var comment: String? = null;
    private var isUploaded = false;
    override fun isUploaded(): Boolean {
        return this.isUploaded;
    }

    override fun setUploaded(uploaded: Boolean) {
        this.isUploaded = uploaded;
    }

    @TypeConverters(value = [listToStringConverter::class])
    var imageUris: ArrayList<String>? = ArrayList()
    @TypeConverters(value = [listToStringConverter::class])
    private var dropDownItems: ArrayList<String>? = ArrayList()

    override fun getQ_Id(): String? {
        return question_id
    }

    override fun setQ_Id(string: String) {
        this.question_id = string
    }

    override fun getId(): Int {
        return id
    }

    override fun getAnswer(): String? {
        return this.answer
    }

    override fun setPriority(prieroty: String?) {
        this.priority = prieroty
    }

    override fun getPriority(): String? {
        return this.priority
    }

    override fun setAnswer(answers: String?) {
        this.answer = answers.toString()
    }

    override fun getImageuri(): ArrayList<String>? {
        this.imageUris.let { return it ?: ArrayList() }
    }

    override fun setImageuri(lsit: ArrayList<String>?) {
        this.imageUris = lsit
    }

    override fun setId(id: Int) {
        this.id = id
    }

    override fun getGuideline(): String? {
        return this.guidlines
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
        return isimagerequired
    }

    override fun getQuestionTypeId(): String? {
        return this.type
    }

    override fun getDropDownItems(): ArrayList<String>? {
        return this.dropDownItems
    }

    override fun setDropDownItems(list: ArrayList<String>) {
        this.dropDownItems = list
    }

    override fun getToolId(): String? {
        return this.template_id
    }

    class listToStringConverter {
        @TypeConverter
        fun stringToList(value: String): ArrayList<String> {
            return Gson().fromJson<ArrayList<String>>(value, ArrayList<String>()::class.java)
        }

        @TypeConverter
        fun ListToString(value: List<String>): String {
            try {
                if (value == null)
                    return "[]"
                return Gson().toJson(value)
            } catch (e: Exception) {
                return "[]"
            }
        }
    }

    class ArrayToStringConverter {
        @TypeConverter
        fun stringToArray(value: String): ArrayList<String> {
            return Gson().fromJson<ArrayList<String>>(value, ArrayList<String>()::class.java)
        }

        @TypeConverter
        fun ListToString(value: Array<String>): String {
            return Gson().toJson(value.asList())
        }
    }
}


