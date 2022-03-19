package com.example.medicalassesment.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.medicalassesment.Activities.TemplateActivity
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.adapter.InspectionAdapter
import com.example.medicalassesment.database.TemplateViewModel

import com.example.medicalassesment.R
import com.example.medicalassesment.adapter.InspectioToolsMainAdapter
import com.example.medicalassesment.databinding.FragmentSavedBinding
import com.example.medicalassesment.models.InspectionToolsItem

class NewFragment : Fragment(), BaseFragment, SwipeRefreshLayout.OnRefreshListener {
    override fun onUpdate() {
        try {
             templateViewModel.filterData("New", "")
        } catch (e: Exception) {
        }

    }

    override fun onRefresh() {
        templateActivity.updateTemplete()
    }

    override fun onTextChange(string: String) {
        templateViewModel.filterData("New", string)
    }

    private lateinit var templateViewModel: TemplateViewModel
    private lateinit var mBinding: FragmentSavedBinding
    private lateinit var templateActivity: TemplateActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_saved, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
        templateViewModel = ViewModelProviders.of(this)[TemplateViewModel::class.java]
        templateViewModel.templateModels.observe(viewLifecycleOwner, Observer {
            var categorys = context?.let { it1 -> PrefHelper(it1).getCatogaries() }
            var list: ArrayList<InspectionToolsItem> = ArrayList()
            categorys?.categories?.forEach { category ->
                var inspectionToolsItem = InspectionToolsItem();
                inspectionToolsItem.name = category.name;
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

            var inspectinAdapter = InspectioToolsMainAdapter(list)
            mBinding.recyclerView.adapter = inspectinAdapter

        })
        templateViewModel.filterData("New", "")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        templateActivity = context as TemplateActivity

    }

    override fun onDetach() {
        super.onDetach()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            NewFragment().apply {

            }
    }

}
