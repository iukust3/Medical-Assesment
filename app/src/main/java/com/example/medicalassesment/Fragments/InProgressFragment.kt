package com.example.medicalassesment.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.medicalassesment.Activities.OverViewActivity
import com.example.medicalassesment.Activities.SurveyActivity
import com.example.medicalassesment.adapter.InspectionAdapter
import com.example.medicalassesment.database.QuestionViewModel
import com.example.medicalassesment.database.TemplateViewModel
import com.example.medicalassesment.models.TemplateModel

import com.example.medicalassesment.R

class InProgressFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    InspectionAdapter.OnItemClickListnear, BaseFragment {
    override fun onUpdate() {

    }

    override fun onItemClick(templateModel: TemplateModel) {
        if (templateModel.status == "InProgress") {
            QuestionViewModel.tamplateID = templateModel.id
            var intent = Intent(context, SurveyActivity::class.java)
            intent.putExtra("TemplateModel", templateModel)
            startActivity(intent)
        } else {
            var intent = Intent(context, OverViewActivity::class.java)
            intent.putExtra("TemplateModel", templateModel)
            startActivity(intent)
        }
    }

    override fun onRefresh() {
        templateViewModel.filterData("InProgress", "")
    }

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var templateViewModel: TemplateViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_complete, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swip_refresh)
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        loadData()
        return view
    }

    private fun loadData() {
        templateViewModel = ViewModelProviders.of(this)[TemplateViewModel::class.java]
        templateViewModel.templateModels.observe(this, Observer {
            recyclerView.adapter = InspectionAdapter(
                it
            )
            swipeRefreshLayout.isRefreshing = false
            InspectionAdapter.onItemClick = this
        })
        templateViewModel.filterData("InProgress", "")
    }

    override fun onResume() {
        InspectionAdapter.onItemClick = this
        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            InProgressFragment()
    }

    override fun onTextChange(string: String) {
        templateViewModel.filterData("InProgress", string)
    }
}
