package com.example.medicalassesment.Fragments

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.R
import com.example.medicalassesment.databinding.FragmentTempletemodelListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class TempleteModelDialogFragment : BottomSheetDialogFragment() {
    private lateinit var mBehavior: BottomSheetBehavior<View>
    private lateinit var mBinding: FragmentTempletemodelListDialogBinding
    private  var templateModel: TemplateModel?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_templetemodel_list_dialog,
            container,
            false
        )
        templateModel.let {
            mBinding.templete =it
        }
        mBinding.btClose.setOnClickListener { dismiss() }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }


    companion object {

        // TODO: Customize parameters
        fun newInstance(template: TemplateModel): TempleteModelDialogFragment =
            TempleteModelDialogFragment().apply {
                templateModel = template
            }

    }
}
