package com.example.medicalassesment.Fragments


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Activities.MapsActivity
import com.example.medicalassesment.Activities.SurveyActivity
import com.example.medicalassesment.adapter.StartAdapter
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.Helper.MyLocation
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.Helper.QuestionValidator
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.MyApplication

import com.example.medicalassesment.R
import com.example.medicalassesment.Retrofit.Output
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.databinding.FragmentStartBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() {

    private lateinit var fragmentInterction: FragmentInterction
    private lateinit var mBinding: FragmentStartBinding;
    private lateinit var templateModel: TemplateModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        templateModel = (context as SurveyActivity).templateModel
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
        if (templateModel.inspectionConductedOn.isNullOrEmpty())
            mBinding.inspectionDate.text = Utils.getFormattedDateSimple(curCalender.timeInMillis)
        else mBinding.inspectionDate.text = templateModel.inspectionConductedOn
        mBinding.pickLocation.setOnClickListener {
            var intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("TemplateModel", (context as SurveyActivity).templateModel)
            startActivityForResult(intent, 400)
        }
        if (templateModel.inspectionConductedAt.isNullOrEmpty())
            getLocation()
        else mBinding.location.text = templateModel.inspectionConductedAt

        context?.let {
            var list =
                MedicalDataBase.getInstance(it).getDao().getDefaultInfo(templateModel.id.toString())
            var startAdapter = StartAdapter(list, 0)
            mBinding.recyclerView.layoutManager =
                LinearLayoutManager(it, RecyclerView.VERTICAL, false)
            mBinding.recyclerView.adapter = startAdapter
        }
    }

    private fun getLocation() {
        activity?.let {
            if (
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                MyLocation().getLocation(context, object : MyLocation.LocationResult() {
                    override fun gotLocation(location: Location?) {
                        if (location != null) {
                            it.runOnUiThread {
                                mBinding.location.text = Utils.getCompleteAddressString(
                                    it,
                                    location.latitude,
                                    location.longitude
                                )
                            }
                        }
                    }
                })
            } else {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    200
                )
                // Show rationale and request permission.
            }
        }
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
            context?.let {
                var output =
                    QuestionValidator(it, templateModel.id, -1).validateQuestions()
                when (output) {
                    is Output.Success -> {
                        var prefHelper = PrefHelper(it)
                        var preliminaryInfoModel = MedicalDataBase.getInstance(it).getDao()
                            .getPrelemnoryInfo(templateModel.id.toString(), "Facility Name:")
                        fragmentInterction.onNextClick(0)
                        if(templateModel.type=="1"){
                            templateModel.title =
                                preliminaryInfoModel.getAnswer() +
                                        "-"+Constant.getType(templateModel.type!!) +" / " + Utils.getFormattedDateSimple()
                        }else
                        templateModel.title =
                            prefHelper.getFecility().getName(Integer.parseInt(preliminaryInfoModel.getAnswer())) +
                                   "-"+Constant.getType(templateModel.type!!) +" / " + Utils.getFormattedDateSimple()
                        // templateModel.title=Utils.getFormattedDateSimple()+" / "+
                        MedicalDataBase.getInstance(it).getDao().update(templateModel)
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
        Log.e("TAG", "index $position")
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
                            Log.e("TAG", "Chiled " + drawableItems.size)
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
        fragmentInterction = context as FragmentInterction
    }
}
