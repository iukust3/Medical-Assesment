package com.example.medicalassesment.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.medicalassesment.adapter.InspectionAdapter
import com.example.medicalassesment.database.QuestionViewModel
import com.example.medicalassesment.Fragments.BaseFragment
import com.example.medicalassesment.Fragments.NewFragment
import com.example.medicalassesment.Fragments.SavedFragment
import com.example.medicalassesment.Fragments.TempleteModelDialogFragment
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.databinding.ActivityTamplateBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class TemplateActivity : AppCompatActivity(), InspectionAdapter.OnItemClickListnear {


    private val list = ArrayList<Fragment>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var mBionding: ActivityTamplateBinding
    lateinit var inspectionViewModel: InspectionViewModel
    private lateinit var surveyViewModel: SurveyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        mBionding = DataBindingUtil.setContentView(this, R.layout.activity_tamplate)
        surveyViewModel = ViewModelProviders.of(this, factory)[SurveyViewModel::class.java]
        surveyViewModel.getsurvayViewStateLiveData().observe(this, Observer { setViewState(it) })

        inspectionViewModel = ViewModelProviders.of(this, factory)[InspectionViewModel::class.java]
        inspectionViewModel.getViewState().observe(this, Observer {
            var fragment =
                (mBionding.container.adapter as FragmentPagerAdapter).getItem(mBionding.tablayout.selectedTabPosition) as BaseFragment
            fragment.onUpdate()

        })
/*
        mBionding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBionding.recyclerView.adapter = InspectionAdapter(ArrayList())

        inspectionViewModel = ViewModelProviders.of(this, factory)[InspectionViewModel::class.java]
        inspectionViewModel.getViewState().observe(this, Observer {
            when (it) {
                is SuccessLoad -> {
                    if (!it.templateModel.isNullOrEmpty())
                        mBionding.recyclerView.adapter = InspectionAdapter(it.templateModel)
                }
            }
            mBionding.swipRefresh.isRefreshing = false
        })
        GlobalScope.launch {
            MedicalDataBase.getInstance(this@TemplateActivity).getDao().getTemplate("New")
                .let {
                    Log.e("TAG", "size  " + it.size)
                    runOnUiThread {
                        mBionding.recyclerView.adapter = InspectionAdapter(
                            it
                        )
                    }
                }
        }
        mBionding.swipRefresh.setOnRefreshListener {
            inspectionViewModel.updateTamplets()
        }*/
        iniateUi()
        // bottomSheetBehavior=BottomSheetBehavior.from()
        InspectionAdapter.onItemClick = this
    }

    private fun setViewState(it: SurvayViewState?) {
        isClickHandel = false

        when (it) {
            is Loading -> {
            }
            is NetworkError -> {
                loadingdilog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Error " + it.message)
            }
            is Success -> {
                loadingdilog.dismiss()
                isClickHandel = false
                QuestionViewModel.tamplateID = it.templateModel.id
                var intent = Intent(this, SurveyActivity::class.java)
                intent.putExtra("TemplateModel", it.templateModel)
                startActivity(intent)
            }
        }
    }

    var isClickHandel = false
    override fun onItemClick(templateModel: TemplateModel) {
        var fragment =
            (mBionding.container.adapter as FragmentPagerAdapter).getItem(mBionding.tablayout.selectedTabPosition)

        if (fragment is NewFragment) {
            isClickHandel = true
            Log.e("TAG", " Onclick ")
            showLoadingDilog()
            surveyViewModel.onScreenLoaded(templateModel)
        } else {
            var templeteModelDialogFragment = TempleteModelDialogFragment.newInstance(templateModel)
            templeteModelDialogFragment.show(supportFragmentManager, "BottomSeet")
        }
    }

    private lateinit var statusTextView: TextView
    private lateinit var loading_progress: ProgressBar
    private fun showLoadingDilog() {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.setCancelable(false)
        statusTextView = loadingdilog.findViewById<TextView>(R.id.status)!!
        loading_progress = loadingdilog.findViewById<ProgressBar>(R.id.loading_progress)!!
        statusTextView.visibility = GONE
        loading_progress.visibility = GONE
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }


    private lateinit var loadingdilog: BottomSheetDialog
    private fun iniateUi() {
        list.add(NewFragment.newInstance())
        list.add(SavedFragment.newInstance())
        setSupportActionBar(mBionding.toolbar)
        mBionding.container.adapter = FragmentAdapter()
        mBionding.tablayout.setupWithViewPager(mBionding.container)
        mBionding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    Log.e("TAG"," On text $s")

                    var fragment =
                        list[mBionding.tablayout.selectedTabPosition]
                    (fragment as BaseFragment).onTextChange(s.toString())
                } catch (e: Exception) {
                }
            }

        })
    }

    fun updateTemplete() {
        Log.e("TAG"," Update")
        inspectionViewModel.updateTamplets()
    }

    inner class FragmentAdapter : FragmentPagerAdapter {
        constructor() : super(
            this@TemplateActivity.supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        override fun getItem(position: Int): Fragment {
            return list[position];
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "New"
                else -> "Saved"
            }
        }

        override fun getCount(): Int {
            return 2; }
    }
}
