package com.example.medicalassesment

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Activities.BaseActivity
import com.example.medicalassesment.Activities.GenrateReportActivity
import com.example.medicalassesment.adapter.InspectionAdapter
import com.example.medicalassesment.database.TemplateViewModel
import com.example.medicalassesment.databinding.ActivityReport2Binding
import com.example.medicalassesment.models.TemplateModel

class ReportsActivity : BaseActivity() {
    private lateinit var templateViewModel: TemplateViewModel
    private lateinit var mBinding: ActivityReport2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityReport2Binding.inflate(layoutInflater, null, false)
        iniateUi(mBinding.root)
        setCurrantClassName(this)
        setSelectedNavigationItem(R.id.report)
        loadData()
    }

    private fun loadData() {
        mBinding.recyclerView.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        InspectionAdapter.onItemClick=object :InspectionAdapter.OnItemClickListnear{
            override fun onItemClick(templateModel: TemplateModel) {
                var intent = Intent(this@ReportsActivity, GenrateReportActivity::class.java)
                intent.putExtra("TemplateModel", templateModel)
                startActivity(intent)
            }

        }

        templateViewModel = ViewModelProvider(this)[TemplateViewModel::class.java]
        templateViewModel.templateModels.observe(this, Observer {
            mBinding.recyclerView.adapter = InspectionAdapter(
                it
            )
        })

        templateViewModel.filterData("completed","")
    }
}