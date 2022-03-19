package com.example.medicalassesment.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.database.QuestionViewModel
import com.example.medicalassesment.Fragments.NewFragment
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.adapter.InspectioToolsMainAdapter
import com.example.medicalassesment.adapter.InspectioToolssubAdapter
import com.example.medicalassesment.databinding.ActivityTamplateBinding
import com.example.medicalassesment.models.InspectionToolsItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import javax.inject.Inject
import kotlin.properties.Delegates

class TemplateActivity : BaseActivity(), InspectioToolsMainAdapter.OnItemClickListnear {


    private val list = ArrayList<Fragment>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityTamplateBinding
    private lateinit var inspectionViewModel: InspectionViewModel
    private lateinit var surveyViewModel: SurveyViewModel

    var icon by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        mBinding = ActivityTamplateBinding.inflate(layoutInflater, null, false)
        iniateUi(mBinding.root)
        setCurrantClassName(this)
        setSelectedNavigationItem(R.id.inspection)
        surveyViewModel = ViewModelProvider(this, factory)[SurveyViewModel::class.java]
        surveyViewModel.getsurvayViewStateLiveData().observe(this, Observer { setViewState(it) })

        try {
            icon = inspectionToolsItem?.icon!!
            mBinding.title.text = inspectionToolsItem?.name
            mBinding.icon.setImageResource(icon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        inspectionViewModel = ViewModelProvider(this, factory)[InspectionViewModel::class.java]
        inspectionViewModel.getViewState().observe(this) {
            /*  var fragment =
                  (mBionding.container.adapter as FragmentPagerAdapter).getItem(0) as BaseFragment
              fragment.onUpdate()*/

        }
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
        InspectioToolsMainAdapter.onItemClick = this
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
                val intent = Intent(this, SurveyActivity::class.java)
                intent.putExtra("TemplateModel", it.templateModel)
                intent.putExtra("Questions", Gson().toJson(it.templateQuestions))
                startActivity(intent)
                finish()
                try {
                    parent.finish()
                } catch (e: Exception) {
                }
            }
        }
    }

    var isClickHandel = false
    override fun onItemClick(templateModel: TemplateModel) {
        /*  var fragment =
              (mBionding.container.adapter as FragmentPagerAdapter).getItem(0)
  */   showLoadingDilog()
        surveyViewModel.onScreenLoaded(templateModel)
        isClickHandel = true;
        /* if (fragment is NewFragment) {
             isClickHandel = true
             Log.e("TAG", " Onclick ")
         } else {
             var templeteModelDialogFragment = TempleteModelDialogFragment.newInstance(templateModel)
             templeteModelDialogFragment.show(supportFragmentManager, "BottomSeet")
         }*/
    }

    private lateinit var statusTextView: TextView
    private lateinit var loading_progress: ProgressBar
    private fun showLoadingDilog() {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.findViewById<Button>(R.id.button_ok)?.visibility = GONE
        loadingdilog.setCancelable(false)
        statusTextView = loadingdilog.findViewById(R.id.status)!!
        loading_progress = loadingdilog.findViewById(R.id.loading_progress)!!
        statusTextView.visibility = GONE
        loading_progress.visibility = GONE
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }


    private lateinit var loadingdilog: BottomSheetDialog
    private fun iniateUi() {
        list.add(NewFragment.newInstance())
        // list.add(SavedFragment.newInstance())
        setSupportActionBar(mBinding.toolbar)
        /*   mBionding.container.adapter = FragmentAdapter()
           // mBionding.tablayout.setupWithViewPager(mBionding.container)
           mBionding.search.addTextChangedListener(object : TextWatcher {
               override fun afterTextChanged(s: Editable?) {

               }

               override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
               }

               override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   try {
                       Log.e("TAG", " On text $s")

                       var fragment =
                           list[0]
                       (fragment as BaseFragment).onTextChange(s.toString())
                   } catch (e: Exception) {
                   }
               }

           })*/
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.recyclerView.adapter = inspectionToolsItem?.list?.let {
            InspectioToolssubAdapter(
                it
            )
        }
    }

    fun updateTemplete() {
        inspectionViewModel.updateTamplets()
    }

    companion object {
        val TAG: String = "Felicity"
        val ICON: String = "Icon"
        @JvmStatic
        @Volatile
        var inspectionToolsItem: InspectionToolsItem? = null
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
                else -> "New"
            }
        }

        override fun getCount(): Int {
            return 1
        }
    }
}
