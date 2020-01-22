package com.example.medicalassesment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.databinding.InspectionPageItemBinding

class InspectionAdapter(var list: List<TemplateModel>) :
    RecyclerView.Adapter<InspectionAdapter.ViewHolder>() {
    companion object {
        var onItemClick: OnItemClickListnear? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val inspectionPageItemBinding = InspectionPageItemBinding.inflate(
            layoutInflater, parent, false
        )
        return ViewHolder(inspectionPageItemBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val templateModel = list[position]
        holder.bind(templateModel)
    }

    inner class ViewHolder(inspectionPageItemBinding: InspectionPageItemBinding) :
        RecyclerView.ViewHolder(
            inspectionPageItemBinding.root
        ) {
        val mBinding: InspectionPageItemBinding = inspectionPageItemBinding
        lateinit var templateModel: TemplateModel

        init {
            mBinding.root.setOnClickListener {
                onItemClick?.onItemClick(templateModel)
            }
        }

        fun bind(templateModel: TemplateModel) {
            this.templateModel = templateModel
            mBinding.discription.text = templateModel.description
            mBinding.title.text = templateModel.title
            mBinding.status.text = templateModel.status
            mBinding.uploadStatus.text = if (templateModel.isUploaded) "Synced" else "Not Synced"
            if (templateModel.status.equals("completed")) {
                mBinding.uploadStatus.visibility = View.VISIBLE
            } else mBinding.uploadStatus.visibility = View.GONE
            mBinding.executePendingBindings()
        }
    }

    interface OnItemClickListnear {
        fun onItemClick(templateModel: TemplateModel)
    }
}