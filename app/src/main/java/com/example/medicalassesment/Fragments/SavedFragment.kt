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
import com.example.medicalassesment.adapter.InspectionAdapter
import com.example.medicalassesment.database.TemplateViewModel

import com.example.medicalassesment.R
import com.example.medicalassesment.databinding.FragmentSavedBinding

class SavedFragment : Fragment(),BaseFragment,SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        templateActivity.updateTemplete()
        Log.e("TAG"," Update saved")

    }

    override fun onTextChange(string: String) {
        mBinding.swipRefresh.isRefreshing=false
        templateViewModel.filterData("saved", string)
    }

    private lateinit var templateViewModel: TemplateViewModel
    private lateinit var mBinding: FragmentSavedBinding
    private lateinit var templateActivity: TemplateActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onUpdate() {
        Log.e("TAG","On Update saved")

        mBinding.swipRefresh.isRefreshing=false
        templateViewModel.filterData("saved", "")

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_saved, container, false)
        mBinding.swipRefresh.setOnRefreshListener(this)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        templateViewModel = ViewModelProviders.of(this)[TemplateViewModel::class.java]
        templateViewModel.templateModels.observe(this, Observer {
            var inspectinAdapter = InspectionAdapter(it)
            mBinding.recyclerView.adapter = inspectinAdapter
        })
        templateViewModel.filterData("saved", "")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        templateActivity=context as TemplateActivity

    }

    override fun onDetach() {
        super.onDetach()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            SavedFragment().apply {

            }
    }
}
