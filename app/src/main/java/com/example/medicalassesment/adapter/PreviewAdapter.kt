package com.example.medicalassesment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Fragments.QustionAdapertInterface
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.FeedBackModel
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewHolder.OverViewHolder
import com.example.medicalassesment.databinding.PreviewItemBinding

class PreviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var qustionList: List<BaseQustion> = ArrayList()
    private lateinit var qustionAdapertInterface: QustionAdapertInterface
    private var isFromOverView = false
    private var index = 0;

    constructor(list: List<BaseQustion>) : this() {

        this.qustionList = list;
    }

    constructor(list: List<QuestionModel>, isfromOverview: Boolean) : this() {
        this.qustionList = list;
        this.isFromOverView = isfromOverview
    }

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.mContext = parent.context
        val layoutInflater = LayoutInflater.from(mContext);
        return when (viewType) {
            0 ->
                OverViewHolder(
                    PreviewItemBinding.inflate(layoutInflater, parent, false),
                    this
                )
            else -> {
                HeaderViewHolder(layoutInflater.inflate(R.layout.preview_header, parent, false))
            }
        }
    }
    override fun getItemCount(): Int {
        return qustionList.size
    }

    fun onNextClick() {

    }

    override fun getItemViewType(position: Int): Int {
        var qustion = qustionList[position]
        return when (qustion) {
            is FeedBackModel -> {
                if (qustion.questiontype == "Header")
                    1
                else 0
            }
            else -> 0;
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            (holder as OverViewHolder).bindViewHolder(qustionList[position] )
        } else {
            var headerViewHolder = holder as HeaderViewHolder
            headerViewHolder.title.text = qustionList[position].getTittle()
        }
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

    class HeaderViewHolder(var iteview: View) : RecyclerView.ViewHolder(iteview) {
        val title: TextView = iteview.findViewById(R.id.title)
    }
}