package com.example.medicalassesment.Fragments


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Activities.BaseActivity
import com.example.medicalassesment.Activities.MapsActivity
import com.example.medicalassesment.Activities.SurveyActivity
import com.example.medicalassesment.adapter.StartAdapter
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.Helper.QuestionValidator
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication

import com.example.medicalassesment.R
import com.example.medicalassesment.Retrofit.Output
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.ViewHolder.StartViewHolder
import com.example.medicalassesment.databinding.FragmentStartBinding
import com.example.medicalassesment.models.PreliminaryInfoModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class StartFragment(private var defultPreliminaryInfo: List<PreliminaryInfoModel>) : Fragment() {

    private lateinit var fragmentInteraction: FragmentInteraction
    private lateinit var mBinding: FragmentStartBinding;
    private lateinit var templateModel: TemplateModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        templateModel = BaseActivity.templateModel
        // Inflate the layout for this fragment
        mBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_start, container, false)
        mBinding.next.setOnClickListener {
            onNext()
        }
        return mBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (templateModel.inspectionConductedBy.isNullOrEmpty())
            mBinding.userName.text = MyApplication.USER.firstname + " " + MyApplication.USER.surname
        else mBinding.userName.text = templateModel.inspectionConductedBy
        val curCalender = Calendar.getInstance()
        if (StartViewHolder.isFirstTime)
            mBinding.inspectionDate.text = Utils.getFormattedDateSimple()
        else mBinding.inspectionDate.text = templateModel.inspectionConductedOn
        mBinding.pickLocation.setOnClickListener {
            var intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("TemplateModel", BaseActivity.templateModel)
            startActivityForResult(intent, 400)
        }
        if (templateModel.inspectionConductedAt.isNullOrEmpty())
            getLocation()
        else mBinding.location.text = templateModel.inspectionConductedAt

        context?.let {
            val list: List<PreliminaryInfoModel> = if (defultPreliminaryInfo.isNullOrEmpty()) {
                MedicalDataBase.getInstance(it).getDao().getDefaultInfo(templateModel.id.toString())
            } else {
                defultPreliminaryInfo
            }
            val startAdapter = StartAdapter(list, 0, templateModel)
            mBinding.recyclerView.layoutManager =
                LinearLayoutManager(it, RecyclerView.VERTICAL, false)
            mBinding.recyclerView.adapter = startAdapter
        }
    }

    private fun getLocation() {
        var addresReciver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                mBinding.location.text = p1?.getStringExtra(Utils.ADDRES)

            }
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            addresReciver,
            IntentFilter(Utils.ADDRES_RECIVER)
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            mBinding.location.text = data?.getStringExtra("Address")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onNext() {
        templateModel.inspectionConductedBy = mBinding.userName.text.toString()
        templateModel.inspectionConductedOn = mBinding.inspectionDate.text.toString()
        templateModel.inspectionConductedAt = mBinding.location.text.toString()

        GlobalScope.launch {
            context?.let { it ->
                var output = if (!defultPreliminaryInfo.isNullOrEmpty()) {
                    QuestionValidator(
                        it,
                        templateModel.id,
                        -1,
                        defultPreliminaryInfo
                    ).validateQuestions()
                } else {
                    QuestionValidator(it, templateModel.id, -1).validateQuestions()
                }
                when (output) {
                    is Output.Success -> {
                        var prefHelper = PrefHelper(it)
                        var preliminaryInfoModel: PreliminaryInfoModel =
                            if (!defultPreliminaryInfo.isNullOrEmpty())
                                defultPreliminaryInfo.find {
                                    it.title!!.contains("Facility Name:", true)
                                }!!
                            else MedicalDataBase.getInstance(it).getDao()
                                .getPrelemnoryInfo(templateModel.id.toString(), "Facility Name:")
                        if (templateModel.type != "1") {
                            try {
                                templateModel.title =
                                    prefHelper.getFecility(templateModel.category.toString())
                                        .getName(
                                            Integer.parseInt(
                                                preliminaryInfoModel.getAnswer().toString()
                                            )
                                        )
                            } catch (e: Exception) {
                                templateModel.title = preliminaryInfoModel.getAnswer()
                            }
                        } else {
                            templateModel.title = preliminaryInfoModel.getAnswer()
                        }
                        if (defultPreliminaryInfo.isNullOrEmpty())
                            MedicalDataBase.getInstance(it).getDao().update(templateModel)
                        else {
                            (requireContext() as SurveyActivity).insertData(
                                templateModel,
                                defultPreliminaryInfo
                            )
                        }
                        if (defultPreliminaryInfo.isNullOrEmpty()) {
                            fragmentInteraction.onNextClick(0)
                        } else {
                        }
                    }
                    is Output.Error -> {
                        activity?.runOnUiThread {
                            try {
                                setRecyclerViewScrollListener(output.error)
                                mBinding.recyclerView.scrollToPosition(
                                    output.error
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        }

    }

    private fun setRecyclerViewScrollListener(position: Int) {
        var linearLayoutManager: LinearLayoutManager =
            mBinding.recyclerView.layoutManager as LinearLayoutManager
        if (linearLayoutManager.findLastVisibleItemPosition() >= position && linearLayoutManager.findFirstVisibleItemPosition() <= position) {
            var view =
                mBinding.recyclerView.findViewHolderForAdapterPosition(position)
            var layout =
                view?.itemView?.findViewById<LinearLayout>(R.id.main_layout)
            if (lastView != null) {
                val drawable = lastView.let {
                    it?.background
                }
                context?.let { ContextCompat.getColor(it, R.color.grey_20) }?.let {
                    when (drawable) {
                        is GradientDrawable ->
                            drawable.setStroke(
                                1,
                                resources.getColor(R.color.grey_20)
                            ) // set stroke width and stroke color
                        is StateListDrawable -> {
                            val dcs =
                                drawable.constantState as DrawableContainer.DrawableContainerState
                            val drawableItems = dcs.children
                            val gradientDrawableChecked =
                                drawableItems[0] as GradientDrawable // item 1
                            gradientDrawableChecked.setStroke(
                                1,
                                resources.getColor(R.color.grey_20)
                            );
                            //  gradientDrawableUnChecked.setStroke(1, Color.BLUE);
                        }
                    }
                } // set stroke width and stroke color
            }
            lastView = layout
            when (val drawable = layout?.background) {
                is GradientDrawable ->
                    drawable.setStroke(
                        2,
                        Color.RED
                    ) // set stroke width and stroke color
                is StateListDrawable -> {
                    val dcs = drawable.constantState as DrawableContainer.DrawableContainerState
                    val drawableItems = dcs.children
                    Log.e("TAG", "Chiled " + drawableItems.size)
                    val gradientDrawableChecked =
                        drawableItems[0] as GradientDrawable // item 1
                    gradientDrawableChecked.setStroke(2, Color.RED);
                    //  gradientDrawableUnChecked.setStroke(1, Color.BLUE);
                }
            }
        } else {
            var scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    var view =
                        mBinding.recyclerView.findViewHolderForAdapterPosition(position)
                    var layout =
                        view?.itemView?.findViewById<LinearLayout>(R.id.main_layout)
                    if (lastView != null) {
                        val drawable = lastView.let {
                            it?.background
                        }
                        context?.let { ContextCompat.getColor(it, R.color.grey_20) }?.let {
                            when (drawable) {
                                is GradientDrawable ->
                                    drawable.setStroke(
                                        1,
                                        resources.getColor(R.color.grey_20)
                                    ) // set stroke width and stroke color
                                is StateListDrawable -> {
                                    val dcs =
                                        drawable.constantState as DrawableContainer.DrawableContainerState
                                    val drawableItems = dcs.children
                                    Log.e("TAG", "Chiled " + drawableItems.size)
                                    val gradientDrawableChecked =
                                        drawableItems[0] as GradientDrawable // item 1
                                    gradientDrawableChecked.setStroke(
                                        1,
                                        resources.getColor(R.color.grey_20)
                                    );
                                    //  gradientDrawableUnChecked.setStroke(1, Color.BLUE);
                                }
                            }
                        } // set stroke width and stroke color
                    }
                    lastView = layout
                    when (val drawable = layout?.background) {
                        is GradientDrawable ->
                            drawable.setStroke(
                                2,
                                Color.RED
                            ) // set stroke width and stroke color
                        is StateListDrawable -> {
                            val dcs =
                                drawable.constantState as DrawableContainer.DrawableContainerState
                            val drawableItems = dcs.children
                            val gradientDrawableChecked =
                                drawableItems[0] as GradientDrawable // item 1
                            gradientDrawableChecked.setStroke(2, Color.RED);
                            //  gradientDrawableUnChecked.setStroke(1, Color.BLUE);
                        }
                    }
                    mBinding.recyclerView.removeOnScrollListener(this)
                }
            }
            mBinding.recyclerView.addOnScrollListener(scrollListener)
        }
    }

    companion object {
        var lastView: LinearLayout? = null
        fun getInstance(index: Int): SurveyFragment {
            return SurveyFragment(index)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentInteraction = context as FragmentInteraction
    }

    override fun onResume() {
        super.onResume()

    }
}
