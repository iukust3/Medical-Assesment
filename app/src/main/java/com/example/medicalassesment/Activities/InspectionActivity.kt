package com.example.medicalassesment.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.ViewModels.InspectionViewModel
import com.example.medicalassesment.ViewModels.InspectionViewState
import com.example.medicalassesment.ViewModels.SuccessLoad
import com.example.medicalassesment.ViewModels.onErrorLoad
import com.example.medicalassesment.database.DatabaseRepository
import com.example.medicalassesment.database.TemplateViewModel
import com.example.medicalassesment.databinding.ActivityInspectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import javax.inject.Inject


class InspectionActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {
    private var isComplete: Boolean = false
    private var count: Int = 0
    private lateinit var templateViewModel: TemplateViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var inspectionViewModel: InspectionViewModel
    private lateinit var mBinding: ActivityInspectionBinding

    val list = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInspectionBinding.inflate(layoutInflater, null, false)
        iniateUi(mBinding.root)
        setCurrantClassName(this)
        IniateUi()
        (application as MyApplication).appComponent.inject(this)
        inspectionViewModel = ViewModelProvider(this, factory)[InspectionViewModel::class.java]
        inspectionViewModel.getViewState().observe(this, Observer { setViewState(it) })

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
        isComplete = true
        count = 0
        templateViewModel = ViewModelProviders.of(this)[TemplateViewModel::class.java]
        templateViewModel.templateModels.observe(this, Observer {
            if (count != 0) {
                if (isComplete) {
                    mBinding.countCompleted.text = "${it.size}"
                    templateViewModel.filterData("InProgress", "")
                    isComplete = false
                } else {
                    mBinding.countInprogress.text = "${it.size}"
                }
            } else count++
        })
        templateViewModel.filterData("completed", "")


        /* try{
             ContextCompat.startForegroundService(this,Intent(this, UploadingService::class.java))
         }catch(e: java.lang.Exception){e.printStackTrace()}*/
    }


    var fromUpdate = false
    private fun setViewState(inspectionViewState: InspectionViewState) {
        when (inspectionViewState) {
            is SuccessLoad -> {
                hidedilog()
                if (!fromUpdate)
                    IniateUi()
                else fromUpdate = false
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
            startActivity(Intent(this, FecilityActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item?.itemId == R.id.logout -> {
                PrefHelper(this).setIsLogined(false)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
            item?.itemId == R.id.update_templates -> {
                fromUpdate = true
                showLoadingDialog()
                inspectionViewModel.updateTamplets()
                return true
            }
            item?.itemId == R.id.clearData -> {
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Confirm")
                alertDialog.setMessage("Are you sure you want to clear all data?\nYou will need to re-login after clear data.")
                alertDialog.setNegativeButton(
                    "Yes"
                ) { p0, _ ->
                    run {
                        p0.dismiss()
                        PrefHelper(this).clear()
                        var databaseRepository = DatabaseRepository(application)
                        databaseRepository.clearAllTable()
                        if (hasStoragePermission()) {
                            deleteRecursive(Utils.getFolderPath(this@InspectionActivity))
                            deleteRecursive(File(Utils.getReportsFolder(this@InspectionActivity)))
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            EasyPermissions.requestPermissions(
                                this,
                                "Require Storage permission to delete inspection Images",
                                400,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                    }
                }
                alertDialog.setPositiveButton(
                    "NO"
                ) { p0, _ -> p0.dismiss() }
                var dialog = alertDialog.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).background =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.option_green
                    )
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
                setMargins(dialog.getButton(AlertDialog.BUTTON_NEGATIVE), 0, 0, 10, 0)
                setMargins(dialog.getButton(AlertDialog.BUTTON_POSITIVE), 10, 0, 0, 0)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).background =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.option_red
                    )

            }
            item?.itemId != android.R.id.home -> {
                fromUpdate = true
                showLoadingDialog()
                inspectionViewModel.updateStateLgas()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    inner class FragmentAdapter : FragmentPagerAdapter(
        this@InspectionActivity.supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        override fun getItem(position: Int): Fragment {
            return list[position]
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
        } else {
            EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == 400) {
            deleteRecursive(Utils.getFolderPath(this))
            deleteRecursive(File(Utils.getReportsFolder(this)))
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onResume() {
        setSelectedNavigationItem(R.id.home)
        isComplete = true
        count = 1
        templateViewModel.filterData("completed", "")
        super.onResume()
    }

    private lateinit var loadingdilog: BottomSheetDialog
    private fun showLoadingDialog() {
        loadingdilog = BottomSheetDialog(this)
        loadingdilog.setContentView(R.layout.loading_dilog)
        loadingdilog.setCancelable(false)
        loadingdilog.findViewById<Button>(R.id.button_ok)?.visibility = View.GONE
        val statusTextView: TextView = loadingdilog.findViewById(R.id.status)!!
        val loadingProgress: ProgressBar =
            loadingdilog.findViewById(R.id.loading_progress)!!
        statusTextView.visibility = View.GONE
        loadingProgress.visibility = View.GONE
        if (!loadingdilog.isShowing)
            loadingdilog.show()
    }
}
