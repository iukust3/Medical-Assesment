package com.example.medicalassesment.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.R
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.databinding.InspectionPageItemBinding
import com.example.medicalassesment.databinding.InspectionToolsSubTittleBinding
import com.example.medicalassesment.models.InspectionToolsItem

class InspectioToolssubAdapter(var list: ArrayList<InspectionToolsItem.Inspectiontype>) :
    RecyclerView.Adapter<InspectioToolssubAdapter.ViewHolder>() {
    companion object {
        var onItemClick: InspectioToolsMainAdapter.OnItemClickListnear? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val inspectionPageItemBinding = InspectionToolsSubTittleBinding.inflate(
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

    inner class ViewHolder(inspectionPageItemBinding: InspectionToolsSubTittleBinding) :
        RecyclerView.ViewHolder(
            inspectionPageItemBinding.root
        ) {
        val mBinding: InspectionToolsSubTittleBinding = inspectionPageItemBinding
        lateinit var inspectiontype: InspectionToolsItem.Inspectiontype


        fun bind(inspectiontype: InspectionToolsItem.Inspectiontype) {
            this.inspectiontype = inspectiontype
            mBinding.discription.text = inspectiontype.name
            mBinding.recyclerview.layoutManager = LinearLayoutManager(mBinding.root.context)
            mBinding.recyclerview.setHasFixedSize(true)
            var adapter=InspectioToolsAdapter(inspectiontype.list)
            InspectioToolsAdapter.onItemClick= InspectioToolsMainAdapter.onItemClick
            mBinding.recyclerview.adapter=adapter
            adapter.notifyDataSetChanged()
            mBinding.executePendingBindings()
        }
    }



}