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

class StartAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var qustionList: List<BaseQustion> = ArrayList()
    private lateinit var qustionAdapertInterface: QustionAdapertInterface
    private var isFromOverView = false
    private var index = 0;

    constructor(list: List<BaseQustion>, index: Int) : this() {
        this.qustionList = list;
        this.index = index
    }

    constructor(list: List<QuestionModel>, isfromOverview: Boolean) : this() {
        this.qustionList = list;
        this.isFromOverView = isfromOverview
    }

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.mContext = parent.context
        val layoutInflater = LayoutInflater.from(mContext);
        return StartViewHolder(NewItemLayoutBinding.inflate(layoutInflater, parent, false), this)
    }

    override fun getItemCount(): Int {
        return qustionList.size
    }
    fun getItem(position:Int):BaseQustion{
        return qustionList[position]
    }
    fun onNextClick() {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isFromOverView) {
            (holder as OverViewHolder).bindViewHolder(qustionList[position] as QuestionModel)
        } else
            (holder as StartViewHolder).bindViewHolder(qustionList[position] ,index)
    }

    fun updateItem(questionModel: BaseQustion, index: Int) {
        qustionList.forEach {
            if (it.getId() == questionModel.getId()) {
                questionModel.setAnswer(it.getAnswer())
                questionModel.getImageuri()?.let { it1 -> it.setImageuri(it1) }
            }
        }
    }

    fun setList(it: List<QuestionModel>) {
        this.qustionList = it;
        notifyDataSetChanged()
    }

    fun getItemPosition(error: BaseQustion): Int {
        return qustionList.indexOf(error)
    }

}