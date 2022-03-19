package com.example.medicalassesment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Fragments.QustionAdapertInterface
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.ViewHolder.OverViewHolder
import com.example.medicalassesment.ViewHolder.StartViewHolder
import com.example.medicalassesment.databinding.NewItemLayoutBinding
import com.example.medicalassesment.models.TemplateModel

class StartAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var qustionList: List<BaseQustion> = ArrayList()
    private lateinit var qustionAdapertInterface: QustionAdapertInterface
    private var isFromOverView = false
    private var index = 0;
    private lateinit var templateModel: TemplateModel

    constructor(list: List<BaseQustion>, index: Int, templateModel: TemplateModel) : this() {
        this.qustionList = list;
        this.index = index
        this.templateModel = templateModel
    }

    constructor(
        list: List<QuestionModel>,
        isfromOverview: Boolean,
        templateModel: TemplateModel
    ) : this() {
        this.qustionList = list;
        this.isFromOverView = isfromOverview
        this.templateModel = templateModel
    }

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.mContext = parent.context
        val layoutInflater = LayoutInflater.from(mContext);
        return StartViewHolder(
            NewItemLayoutBinding.inflate(layoutInflater, parent, false),
            this,
            templateModel
        )
    }

    override fun getItemCount(): Int {
        return qustionList.size
    }

    fun getItem(position: Int): BaseQustion {
        return qustionList[position]
    }

    fun onNextClick() {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isFromOverView) {
            (holder as OverViewHolder).bindViewHolder(qustionList[position] as QuestionModel)
        } else
            (holder as StartViewHolder).bindViewHolder(qustionList[position], index)
    }

    fun updateItem(questionModel: BaseQustion, index: Int) {
        var qustion = qustionList.get(index);
        questionModel.setAnswer(qustion.getAnswer())
        questionModel.getImageuri()?.let { it1 -> qustion.setImageuri(it1) }
        var mutableList = qustionList.toMutableList()
        mutableList[index] = qustion
        qustionList=mutableList.toList()
    }


    fun updateItem(questionModel: BaseQustion) {
        var index=qustionList.indexOf(questionModel)
        var qustion = qustionList[index];
        questionModel.setAnswer(qustion.getAnswer())
        questionModel.getImageuri()?.let { it1 -> qustion.setImageuri(it1) }
        var mutableList = qustionList.toMutableList()
        mutableList[index] = qustion
        qustionList=mutableList.toList()
    }

}