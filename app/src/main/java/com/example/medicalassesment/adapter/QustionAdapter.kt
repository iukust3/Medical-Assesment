package com.example.medicalassesment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Fragments.QustionAdapertInterface
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.ViewHolder.OverViewHolder
import com.example.medicalassesment.ViewHolder.QustionViewHolder
import com.example.medicalassesment.databinding.NewItemLayoutBinding

class QustionAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        Log.e("TAG", "Item View Type $viewType")
        if (viewType == 13) {
            return HeaderViewHolder(layoutInflater.inflate(R.layout.preview_header, parent, false))

        }
        return QustionViewHolder(NewItemLayoutBinding.inflate(layoutInflater, parent, false), this)
    }

    override fun getItemCount(): Int {
        return qustionList.size
    }

    fun onNextClick() {

    }

    override fun getItemViewType(position: Int): Int =
        Integer.parseInt(qustionList[position].getQuestionTypeId().let { it
            ?: "1" })

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e("TAG", "type ${qustionList[position].getQuestionTypeId()}")
        if (qustionList[position].getQuestionTypeId() == Constant.QUSTION_TYPE_SECTIONTITTLE) {
            val viewHolder = holder as HeaderViewHolder;
            viewHolder.title.text = qustionList[position].getTittle()
        } else {
            if (isFromOverView) {
                (holder as OverViewHolder).bindViewHolder(qustionList[position] as QuestionModel)
            } else
                (holder as QustionViewHolder).bindViewHolder(qustionList[position], index)
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

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)

    }
}