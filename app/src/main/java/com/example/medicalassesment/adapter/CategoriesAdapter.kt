package com.example.medicalassesment.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Activities.TemplateActivity
import com.example.medicalassesment.R
import com.example.medicalassesment.databinding.FeciltyItemBinding
import com.example.medicalassesment.databinding.InspectionToolsMainTittleBinding
import com.example.medicalassesment.models.InspectionToolsItem
import java.security.AccessControlContext
import kotlin.coroutines.coroutineContext

class CategoriesAdapter(list: ArrayList<InspectionToolsItem>, var context: Context) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    val mList = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val feciltyItemBinding = FeciltyItemBinding.inflate(
            layoutInflater, parent, false
        )
        return ViewHolder(feciltyItemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: FeciltyItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val feciltyItemBinding: FeciltyItemBinding = itemView
        lateinit var mInspectionToolsItem: InspectionToolsItem

        init {
            feciltyItemBinding.root.setOnClickListener {
                val intent = Intent(feciltyItemBinding.root.context, TemplateActivity::class.java)
                TemplateActivity.inspectionToolsItem=mInspectionToolsItem
                feciltyItemBinding.root.context.startActivity(intent)
            }
        }

        fun bind(inspectionToolsItem: InspectionToolsItem) {
            mInspectionToolsItem=inspectionToolsItem
            feciltyItemBinding.icon.setImageResource(inspectionToolsItem.icon)
            feciltyItemBinding.title.text = inspectionToolsItem.name
            feciltyItemBinding.executePendingBindings()
        }
    }
}