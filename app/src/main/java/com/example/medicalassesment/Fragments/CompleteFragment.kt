package com.example.medicalassesment.Fragments

import android.content.Context
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
import com.example.medicalassesment.adapter.InspectionAdapter
import com.example.medicalassesment.database.TemplateViewModel

import com.example.medicalassesment.R

class CompleteFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,BaseFragment {
    override fun onUpdate() {
    }


    private lateinit var templateViewModel: TemplateViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        })
        templateViewModel.filterData("completed","")

    }
    override fun onTextChange(string: String) {

        templateViewModel.filterData("completed", string)
     }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onRefresh() {
       templateViewModel.filterData("completed","")
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            CompleteFragment()
    }
}
