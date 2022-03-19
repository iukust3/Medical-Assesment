package com.example.medicalassesment.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.ViewModels.InspectionViewModel
import com.example.medicalassesment.ViewModels.InspectionViewState
import com.example.medicalassesment.ViewModels.SuccessLoad
import com.example.medicalassesment.ViewModels.onErrorLoad
import com.example.medicalassesment.adapter.CategoriesAdapter
import com.example.medicalassesment.database.TemplateViewModel
import com.example.medicalassesment.databinding.ActivityFecilityBinding
import com.example.medicalassesment.models.InspectionToolsItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class FecilityActivity : BaseActivity() {
    private var fromUpdate = false
    private lateinit var inspectionViewModel: InspectionViewModel
    private lateinit var templateViewModel: TemplateViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var mBinding: ActivityFecilityBinding

    //  var list: ArrayList<InspectionToolsItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFecilityBinding.inflate(layoutInflater, null, false)
        iniateUi(mBinding.root)
        setCurrantClassName(this)
        (application as MyApplication).appComponent.inject(this)
        inspectionViewModel = ViewModelProvider(this, factory)[InspectionViewModel::class.java]
        inspectionViewModel.getViewState().observe(this, Observer { setViewState(it) })
        iniateUi()
        setSelectedNavigationItem(R.id.inspection)

    }

    private fun iniateUi() {
        showLoadingDialog()
        templateViewModel = ViewModelProvider(this)[TemplateViewModel::class.java]
        templateViewModel.inspectionToolsItem.observe(this, Observer {
            /* var categorys = PrefHelper(this).getCatogaries()
             list.clear()
             categorys.categories.forEach { category ->
                 var inspectionToolsItem = InspectionToolsItem();
                 inspectionToolsItem.name = category.name;
                 inspectionToolsItem.icon=Constant.getIcon(category.name)
                 list.add(inspectionToolsItem)
             }
             list.forEach { inspectionItem ->
                 var item = InspectionToolsItem.Inspectiontype("Location inspection", ArrayList())
                 item.list = it.filter { templateModel ->
                     templateModel.category == inspectionItem.name && templateModel.type == "1" && templateModel.status == "New";
                 }
                 inspectionItem.list.add(item)
                 item = InspectionToolsItem.Inspectiontype("Registration inspection", ArrayList())
                 item.list = it.filter { templateModel ->
                     templateModel.category == inspectionItem.name && templateModel.type == "2" && templateModel.status == "New"
                 }
                 inspectionItem.list.add(item)
                 item = InspectionToolsItem.Inspectiontype("Routine inspection", ArrayList())
                 item.list =
                     it.filter { templateModel -> templateModel.category == inspectionItem.name && templateModel.type == "3" && templateModel.status == "New" }
                 inspectionItem.list.add(item)
                 item = InspectionToolsItem.Inspectiontype("Monitoring inspection", ArrayList())
                 item.list =
                     it.filter { templateModel -> templateModel.category == inspectionItem.name && templateModel.type == "4" && templateModel.status == "New" }
                 inspectionItem.list.add(item)
             }
             */
           if (!it.isNullOrEmpty()) {
                var categoriesAdapter = CategoriesAdapter(it, this)
                mBinding.recyclerView.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                mBinding.recyclerView.adapter = categoriesAdapter
            } else {
                Toast.makeText(
                    this,
                    "Please update data there have no tools available now.",
                    Toast.LENGTH_LONG
                ).show()
            }
            hidedilog()
        })
        templateViewModel.getFacilityData("New", "")
        /*  mBinding.comunityParmcy.setOnClickListener {
              if (!list.isNullOrEmpty()) {
                  var intent = Intent(this, TemplateActivity::class.java)
                  intent.putExtra(TemplateActivity.TAG, list[0])
                  intent.putExtra(TemplateActivity.ICON, R.drawable.ic_community_pharmacy)
                  startActivity(intent)
              } else {
                  Toast.makeText(this,"Please update data there have no tools available now.",Toast.LENGTH_LONG).show()
              }
          }
          mBinding.Importation.setOnClickListener {
              if (!list.isNullOrEmpty()) {
                  var intent = Intent(this, TemplateActivity::class.java)
                  intent.putExtra(TemplateActivity.TAG, list[2])
                  intent.putExtra(TemplateActivity.ICON, R.drawable.ic_hospital_pharmacy)
                  startActivity(intent)
              } else {
                  Toast.makeText(this,"Please update data there have no tools available now.",Toast.LENGTH_LONG).show()
              }


          }
          mBinding.drugManufactring.setOnClickListener {
              if (!list.isNullOrEmpty()) {
                  var intent = Intent(this, TemplateActivity::class.java)
                  intent.putExtra(TemplateActivity.TAG, list[1])
                  intent.putExtra(TemplateActivity.ICON, R.drawable.ic_drug_manufacturing)
                  startActivity(intent)
              } else {
                  Toast.makeText(this,"Please update data there have no tools available now.",Toast.LENGTH_LONG).show()
              }
          }
          mBinding.ScientificOffice.setOnClickListener {
              if (!list.isNullOrEmpty()) {
                  val intent = Intent(this, TemplateActivity::class.java)
                  intent.putExtra(TemplateActivity.TAG, list[3])
                  intent.putExtra(TemplateActivity.ICON, R.drawable.ic_drug_distribution)
                  startActivity(intent)
              } else {
                  Toast.makeText(this,"Please update data there have no tools available now.",Toast.LENGTH_LONG).show()
              }
          }
          mBinding.warhouseDistribution.setOnClickListener {
              if (!list.isNullOrEmpty()) {
                  val intent = Intent(this, TemplateActivity::class.java)
                  intent.putExtra(TemplateActivity.TAG, list[4])
                  intent.putExtra(TemplateActivity.ICON, R.drawable.ic_drug_distribution)
                  startActivity(intent)
              } else {
                  Toast.makeText(this,"Please update data there have no tools available now.",Toast.LENGTH_LONG).show()
              }
          }
          mBinding.ppmv.setOnClickListener {
              if (!list.isNullOrEmpty()) {
                  val intent = Intent(this, TemplateActivity::class.java)
                  intent.putExtra(TemplateActivity.TAG, list[5])
                  intent.putExtra(TemplateActivity.ICON, R.drawable.ic_ppmv)
                  startActivity(intent)
              } else {
                  Toast.makeText(this,"Please update data there have no tools available now.",Toast.LENGTH_LONG).show()
              }
          }*/
        if (PrefHelper(this).getCatogariesArray().isEmpty()) {
            fromUpdate = true;
            showLoadingDialog()
            inspectionViewModel.updateStateLgas()
            inspectionViewModel.updateTamplets()

        }
    }

    private fun setViewState(inspectionViewState: InspectionViewState) {
        when (inspectionViewState) {
            is SuccessLoad -> {
                hidedilog()
                if (!fromUpdate)
                    iniateUi()
                else fromUpdate = false;
            }
            is onErrorLoad -> {
                hidedilog()
                if (!fromUpdate)
                    iniateUi()
                else fromUpdate = false
            }
        }
    }

    private fun hidedilog() {
        try {
            loadingdilog.dismiss()
        } catch (e: Exception) {
        }
    }

    private lateinit var loadingdilog: BottomSheetDialog
    private fun showLoadingDialog() {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.setCancelable(false)
        loadingdilog.findViewById<Button>(R.id.button_ok)?.visibility = GONE
        val statusTextView: TextView = loadingdilog.findViewById(R.id.status)!!
        val loadingProgress: ProgressBar =
            loadingdilog.findViewById(R.id.loading_progress)!!
        statusTextView.visibility = GONE
        loadingProgress.visibility = GONE
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }


}
