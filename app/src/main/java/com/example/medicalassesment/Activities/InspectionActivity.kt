package com.example.medicalassesment.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.medicalassesment.Fragments.BaseFragment
import com.example.medicalassesment.Fragments.CompleteFragment
import com.example.medicalassesment.Fragments.InProgressFragment
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.InspectionViewModel
import com.example.medicalassesment.ViewModels.InspectionViewState
import com.example.medicalassesment.ViewModels.SuccessLoad
import com.example.medicalassesment.ViewModels.onErrorLoad
import com.example.medicalassesment.databinding.ActivityInspectionBinding
import com.example.medicalassesment.service.UploadingService
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class InspectionActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var inspectionViewModel: InspectionViewModel
    private lateinit var mBinding: ActivityInspectionBinding;

    val list = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_inspection)
        (application as MyApplication).appComponent.inject(this)
        inspectionViewModel = ViewModelProviders.of(this, factory)[InspectionViewModel::class.java]
        inspectionViewModel.getViewState().observe(this, Observer { setViewState(it) })
        IniateUi()
        inspectionViewModel.onScreenLoaded()
        mBinding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    var fragment =
                        (mBinding.container.adapter as FragmentAdapter).getItem(mBinding.container.currentItem)
                    (fragment as BaseFragment).onTextChange(s.toString())
                } catch (e: Exception) {
                }
            }

        })
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
            // Show rationale and request permission.
        }
      /*  try{
            ContextCompat.startForegroundService(this,Intent(this, UploadingService::class.java))
        }catch(e: java.lang.Exception){e.printStackTrace()}
*/
    }

    var fromUpdate = false
    private fun setViewState(inspectionViewState: InspectionViewState) {
        when (inspectionViewState) {
            is SuccessLoad -> {
                hidedilog()
                if (!fromUpdate)
                    IniateUi()
                else fromUpdate = false;
            }
            is onErrorLoad -> {
                hidedilog()
                if (!fromUpdate)
                    IniateUi()
                else fromUpdate = false
            }
        }
    }

    private fun hidedilog() {
        try {
            loadingdilog.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun IniateUi() {
        list.add(InProgressFragment.newInstance())
        list.add(CompleteFragment.newInstance())
        setSupportActionBar(mBinding.toolbar)
        mBinding.container.adapter = FragmentAdapter()
        mBinding.tablayout.setupWithViewPager(mBinding.container)
        mBinding.addNew.setOnClickListener {
            startActivity(Intent(this, TemplateActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.logout) {
            PrefHelper(this).logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            fromUpdate = true;
            showLoadingDilog()
            inspectionViewModel.updateStateLgas()
        }
        return true
    }

    inner class FragmentAdapter : FragmentPagerAdapter(
        this@InspectionActivity.supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        override fun getItem(position: Int): Fragment {
            return list[position];
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                1 -> "Complete"
                else -> "InProgress"
            }
        }

        override fun getCount(): Int {
            return 2; }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 200) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    200
                )
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage("GPS permission required. Please allow GPS permission to continue.")
                alertDialog.setCancelable(false)
                alertDialog.setNegativeButton("OK") { b, _ ->
                    b.dismiss()
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        200
                    )
                }
                alertDialog.show()
                // Show rationale and request permission.
            }
        }
    }

    private lateinit var loadingdilog: BottomSheetDialog
    private fun showLoadingDilog() {

        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.setCancelable(false)
        val statusTextView: TextView = loadingdilog.findViewById<TextView>(R.id.status)!!
        val loadingProgress: ProgressBar =
            loadingdilog.findViewById<ProgressBar>(R.id.loading_progress)!!
        statusTextView.visibility = View.GONE
        loadingProgress.visibility = View.GONE
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }
}
