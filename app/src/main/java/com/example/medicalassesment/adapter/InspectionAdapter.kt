package com.example.medicalassesment.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.databinding.InspectionPageItemBinding
import com.google.gson.Gson

class InspectionAdapter(var list: List<TemplateModel>) :
    RecyclerView.Adapter<InspectionAdapter.ViewHolder>() {
    lateinit var inspectionPageItemBinding: InspectionPageItemBinding

    companion object {
        public var onItemClick: OnItemClickListnear? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        inspectionPageItemBinding = InspectionPageItemBinding.inflate(
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
            mBinding.templete = templateModel
            /* mBinding.discription.text = templateModel.category
             mBinding.title.text = templateModel.title
             mBinding.status.text = templateModel.status
             mBinding.date.text=templateModel.inspectionConductedOn
 */
            mBinding.inspectionType.text= templateModel.type?.let { Constant.getType(it) }
            mBinding.uploadStatus.visibility = GONE
            // mBinding.uploadStatus.text = if (templateModel.isUploaded) "Synced" else "Not Synced"
            if (templateModel.status.equals("completed")) {
                mBinding.uploadStatus.visibility = View.VISIBLE
                if (templateModel.isUploaded)
                    mBinding.uploadStatus.setTextColor(
                        ContextCompat.getColor(
                            inspectionPageItemBinding.root.context,
                            R.color.green_200
                        )
                    )
                else mBinding.uploadStatus.setTextColor(
                    ContextCompat.getColor(
                        inspectionPageItemBinding.root.context,
                        R.color.red_200
                    )
                )
                mBinding.status.setBackgroundResource(R.drawable.option_complete)
            } else {
                mBinding.status.setBackgroundResource(R.drawable.option_inprogress)

            }

            mBinding.executePendingBindings()
        }
    }

    interface OnItemClickListnear {
        fun onItemClick(templateModel: TemplateModel)
    }
}