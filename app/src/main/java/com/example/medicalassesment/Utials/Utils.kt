package com.example.medicalassesment.Utials

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Environment
import android.util.Log
import com.example.medicalassesment.models.QuestionModel
import com.example.medicalassesment.R
import java.io.File
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.location.Geocoder
import java.text.ParseException
import android.util.DisplayMetrics
import android.app.ActivityManager




class Utils {
    companion object {



        fun convertDpToPixel(dp: Float): Float {
            return dp * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }



        private var Startlist = ArrayList<QuestionModel>();


        fun createImageFile(context: Context, qustion_id: Int, tampletId: String?): File? {

            val storageDir: File
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                storageDir =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/" + qustion_id + "_images")
            } else {
                storageDir =
                    File(context.filesDir.absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/" + qustion_id + "_images")
                Log.e("TAG", "Name " + storageDir.absolutePath)
                if (!storageDir.exists()) {
                    storageDir.mkdir()
                }
            }
            storageDir.mkdirs()
            var image: File? = File(storageDir,"${qustion_id}_image.png")
            if(image?.exists()!!)
                return image
            try {
                image = File.createTempFile(
                    "${qustion_id}_image", /* prefix */
                    ".png", /* suffix */
                    storageDir      /* directory */
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.e("TAG", "File " + image?.absolutePath)
            return image
        }
        fun createReportsFolder(context: Context): String? {

            val storageDir: File
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                storageDir =
                    File(Environment.getExternalStorageDirectory().absolutePath +  "/PCN Reports")
            } else {
                storageDir =
                    File(context.filesDir.absolutePath + "/" +"PCN Reports")
                Log.e("TAG", "Name " + storageDir.absolutePath)
                if (!storageDir.exists()) {
                    storageDir.mkdir()
                }
            }
            storageDir.mkdirs()
            return storageDir.absolutePath
        }

        fun getFolderPath(context: Context, question_id: Int, tampletId: String?): String {
            val storageDir: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/" + question_id + "_images")
            } else {
                File(context.filesDir.absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/" + question_id + "_images")
            }

            return storageDir.absolutePath
        }

        fun dialogDatePickerLight(context: Context,past:Boolean, callbacks: (String?) -> Unit) {
            val curCalender = Calendar.getInstance()
            val datePicker = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateShipMillis = calendar.timeInMillis
                    callbacks(
                        getFormattedDateSimple(
                            dateShipMillis
                        )
                    )

                },
                curCalender.get(Calendar.YEAR),
                curCalender.get(Calendar.MONTH),
                curCalender.get(Calendar.DAY_OF_MONTH)
            )
            //set dark light
            datePicker.isThemeDark = false
            datePicker.accentColor = context.resources.getColor(com.example.medicalassesment.R.color.colorPrimary)
            if(past) {
                curCalender.add(Calendar.DAY_OF_MONTH,-1)
                datePicker.maxDate=curCalender
            }else datePicker.minDate=curCalender
            datePicker.show((context as AppCompatActivity).supportFragmentManager, "Datepickerdialog")
        }

        fun getFormattedDateSimple(dateTime: Long?): String {
            val newFormat = SimpleDateFormat("MMM dd, yyyy")
            return newFormat.format(Date(dateTime!!))
        }

        fun getFormattedDateSimple(): String {
            val newFormat = SimpleDateFormat("MMM dd, yyyy")
            return newFormat.format(Calendar.getInstance().time)
        }

        fun getFormattedDateSimple(string: String): Date {
            val format = SimpleDateFormat("MMM dd, yyyy")
            try {
                val date = format.parse(string)
                System.out.println(date)
                return date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return Date()
        }

        fun getFormattedDateSimple(date: Date): String {
            val newFormat = SimpleDateFormat("yyyy-MM-dd")
            return newFormat.format(date)
        }
        fun getCompleteAddressString(
            context: Context,
            LATITUDE: Double,
            LONGITUDE: Double
        ): String {
            var strAdd = ""
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
                if (addresses != null) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress = StringBuilder("")

                    for (i in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                    }
                    strAdd = strReturnedAddress.toString()
                    strAdd += "\n($LATITUDE , $LONGITUDE)"
                    Log.w("TAG", strReturnedAddress.toString())
                } else {
                    Log.w("TAG", "No Address returned!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.w("TAG", "Canont get Address!")
            }
            return strAdd
        }
        fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }




}