package com.example.medicalassesment.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.R
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.databinding.InspectionPageItemBinding
import com.example.medicalassesment.databinding.InspectionToolsItemBinding

class InspectioToolsAdapter(var list: List<TemplateModel>) :
    RecyclerView.Adapter<InspectioToolsAdapter.ViewHolder>() {
    companion object {
        var onItemClick: InspectioToolsMainAdapter.OnItemClickListnear? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inspectionPageItemBinding = InspectionToolsItemBinding.inflate(
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

    inner class ViewHolder(inspectionPageItemBinding: InspectionToolsItemBinding) :
        RecyclerView.ViewHolder(
            inspectionPageItemBinding.root
        ) {
        val mBinding: InspectionToolsItemBinding = inspectionPageItemBinding
        var templateModel: TemplateModel?=null

        init {
            mBinding.root.setOnClickListener {
                templateModel?.let { it1 -> onItemClick?.onItemClick(it1) }
            }
        }

        fun bind(templateModel: TemplateModel?) {
            this.templateModel = templateModel
            mBinding.discription.text = templateModel?.title
             mBinding.executePendingBindings()
        }
    }


}