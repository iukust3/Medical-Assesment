package com.example.medicalassesment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.databinding.InspectionToolsMainTittleBinding
import com.example.medicalassesment.models.InspectionToolsItem

class InspectioToolsMainAdapter(var list: List<InspectionToolsItem>) :
    RecyclerView.Adapter<InspectioToolsMainAdapter.ViewHolder>() {
    companion object {
        var onItemClick: OnItemClickListnear? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val inspectionPageItemBinding = InspectionToolsMainTittleBinding.inflate(
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

    inner class ViewHolder(inspectionPageItemBinding: InspectionToolsMainTittleBinding) :
        RecyclerView.ViewHolder(
            inspectionPageItemBinding.root
        ) {
        val mBinding: InspectionToolsMainTittleBinding = inspectionPageItemBinding
        lateinit var inspectionToolsItem: InspectionToolsItem


        fun bind(inspectionToolsItem: InspectionToolsItem) {
            this.inspectionToolsItem = inspectionToolsItem
            mBinding.title.text = inspectionToolsItem.name
            mBinding.recyclerview.layoutManager = LinearLayoutManager(mBinding.root.context)
            mBinding.recyclerview.setHasFixedSize(true)
           var adapter=InspectioToolssubAdapter(inspectionToolsItem.list)
            InspectioToolssubAdapter.onItemClick= onItemClick
            mBinding.recyclerview.adapter=adapter
            adapter.notifyDataSetChanged()
            mBinding.executePendingBindings()
        }
    }

    interface OnItemClickListnear {
        fun onItemClick(templateModel: TemplateModel)
    }

}