package com.example.medicalassesment.Fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalassesment.Activities.OverViewActivity
import com.example.medicalassesment.Activities.SurveyActivity
import com.example.medicalassesment.adapter.QustionAdapter
import com.example.medicalassesment.database.QuestionViewModel
import com.example.medicalassesment.Helper.QuestionValidator
import com.example.medicalassesment.models.QuestionModel

import com.example.medicalassesment.R
import com.example.medicalassesment.Retrofit.Output
import com.example.medicalassesment.databinding.FragmentSurvayBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.core.content.ContextCompat
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.medicalassesment.Activities.BaseActivity
import com.example.medicalassesment.Helper.ScoreCalculator
import com.example.medicalassesment.Interfaces.QustionViewHolderInterface


/**
 * A simple [Fragment] subclass.
 */
class SurveyFragment() : Fragment(), QustionAdapertInterface {
    private lateinit var surveyActivity: SurveyActivity
    private lateinit var questionViewModel: QuestionViewModel
    private var index = 0;
    private var currentTamlpetID = 0
    override fun takePicture(questionModel: QuestionModel) {
        // fragmentInterction.takePicture(questionModel)
    }

    private lateinit var questionList: ArrayList<QuestionModel>
    public lateinit var BackStateName: String
    lateinit var fragmentInteraction: FragmentInteraction

    constructor(list: ArrayList<QuestionModel>, name: String) : this() {
        this.questionList = list;
        this.BackStateName = name;
    }

    constructor(index: Int) : this() {
        this.index = index
    }

    private lateinit var mBinding: FragmentSurvayBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentTamlpetID = QuestionViewModel.tamplateID
        Log.e("TAG", " " + QuestionViewModel.tamplateID)
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_survay, container, false)
        questionViewModel = ViewModelProvider(
            this
        )[QuestionViewModel::class.java]

        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var removeObserver = false;
        when (index) {
            0 -> questionViewModel.prelimanryInfo.observe(viewLifecycleOwner, Observer {
                 if (!removeObserver)
                    mBinding.recyclerView.adapter = QustionAdapter(it, index)
                removeObserver = true

            })
            1 -> questionViewModel.qustions.observe(viewLifecycleOwner, Observer {

                if (!removeObserver)
                    mBinding.recyclerView.adapter = QustionAdapter(it, index)
                removeObserver = true
                try {
                    GlobalScope.launch {
                        ScoreCalculator.scoreCalculator?.calcualteScore(it)
                    }
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }

            })
            2 -> questionViewModel.feedback.observe(viewLifecycleOwner, Observer {
                if (!removeObserver)
                    mBinding.recyclerView.adapter = QustionAdapter(it, index)
                removeObserver = true

            })
        }
        questionViewModel.insertQustion()
        mBinding.next.setOnClickListener {
            onNext()
        }
        mBinding.back.setOnClickListener {
            fragmentInteraction.onBackClick(index)
        }
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        fragmentInteraction = context as FragmentInteraction;
        super.onAttach(context)
        surveyActivity = context as SurveyActivity
    }

    private fun onNext() {
        GlobalScope.launch {
            var output =
                context?.let { QuestionValidator(it, currentTamlpetID, index).validateQuestions() }
            when (output) {
                is Output.Success -> {
                    if (index >= 2) {
                        var intent = Intent(context, OverViewActivity::class.java)
                        intent.putExtra("TemplateModel", BaseActivity.templateModel)
                        startActivity(intent)
                        (requireActivity()).finish()
                    } else {
                        index++;
                        fragmentInteraction.onNextClick(index)
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
                                drawable.constantState as DrawableContainerState
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
                    val dcs = drawable.constantState as DrawableContainerState
                    val drawableItems = dcs.children
                    val gradientDrawableChecked =
                        drawableItems[0] as GradientDrawable // item 1
                    gradientDrawableChecked.setStroke(2, Color.RED);
                    //  gradientDrawableUnChecked.setStroke(1, Color.BLUE);
                }
            }
            if(layout!=null){
                var editText=view?.itemView?.findViewById<EditText>(R.id.etqustion)
                if(editText?.visibility==View.VISIBLE){
                    editText.requestFocus()
                }
            }
        } else {
            var scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val view =
                        mBinding.recyclerView.findViewHolderForAdapterPosition(position)
                    val layout =
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
                                        drawable.constantState as DrawableContainerState
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
                            val dcs = drawable.constantState as DrawableContainerState
                            val drawableItems = dcs.children
                            Log.e("TAG", "Chiled " + drawableItems.size)
                            val gradientDrawableChecked =
                                drawableItems[0] as GradientDrawable // item 1
                            gradientDrawableChecked.setStroke(2, Color.RED);
                            //  gradientDrawableUnChecked.setStroke(1, Color.BLUE);
                        }
                    }
                    if(layout!=null){
                        var editText=view?.itemView?.findViewById<EditText>(R.id.etqustion)
                        if(editText?.visibility==View.VISIBLE){
                            editText.requestFocus()
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

    override fun processNextClick() {
        fragmentInteraction.onNextClick(index)
    }
    fun onActivityResults(requestCode: Int, resultCode: Int, data: Intent?,position: Int,uri: Uri) {
        if ( resultCode == Activity.RESULT_OK) {
            (mBinding.recyclerView.findViewHolderForLayoutPosition(position) as QustionViewHolderInterface).onPictureDone(
                uri.toString()
            )
        }
    }

}
