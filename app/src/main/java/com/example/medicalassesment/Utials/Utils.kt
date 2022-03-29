package com.example.medicalassesment.Utials

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Environment
import android.util.Log
import com.example.medicalassesment.models.QuestionModel
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
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.medicalassesment.models.BaseQustion


class Utils {
    companion object {
        fun convertDpToPixel(dp: Float): Float {
            return dp * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }


        private var Startlist = ArrayList<QuestionModel>();




        fun deleteRecursive(fileOrDirectory: File) {
            if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
                child
            )
            if(!fileOrDirectory.isDirectory){
                fileOrDirectory.delete()
            }
        }

        fun createReportsFolder(context: Context): String? {
            val storageDir: File
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                storageDir =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Reports")
            } else {
                storageDir =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/" + "PCN Reports")
                Log.e("TAG", "Name " + storageDir.absolutePath)
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
            }
            storageDir.mkdirs()
            return storageDir.absolutePath
        }

        fun getReportsFolder(context: Context): String? {
            val storageDir: File
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                storageDir =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Reports")
            } else {
                storageDir =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/" + "PCN Reports")
                Log.e("TAG", "Name " + storageDir.absolutePath)
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
            }
            return storageDir.absolutePath
        }

        fun getFolderPath(context: Context, question_id: BaseQustion, tampletId: String?): String {
            val storageDir: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/"+ question_id.getId()+"_"+question_id.getTittle()?.replace(" ","_") + "_images")
            } else {
                File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/" + question_id.getId()+"_"+question_id.getTittle()?.replace(" ","_") + "_images")
            }

            return storageDir.absolutePath
        }

        fun getFolderPath(context: Context, tampletId: String?): File {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images/" + tampletId + "_images")
            } else {
                File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/PCN Inspection Images/" + tampletId + "_images")
            }
        }

        fun getFolderPath(context: Context): File {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images")
            } else {
                File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/PCN Inspection Images")
            }
        }

        fun dialogDatePickerLight(context: Context, past: Boolean, callbacks: (String?) -> Unit) {
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
            datePicker.accentColor =
                context.resources.getColor(com.example.medicalassesment.R.color.colorPrimary)
            if (past) {
                curCalender.add(Calendar.DAY_OF_MONTH, -1)
                datePicker.maxDate = curCalender
            } else datePicker.minDate = curCalender
            datePicker.show(
                (context as AppCompatActivity).supportFragmentManager,
                "Datepickerdialog"
            )
        }

        fun getFormattedDateSimple(dateTime: Long?): String {
            val newFormat = SimpleDateFormat("MMM dd, yyyy")
            return newFormat.format(Date(dateTime!!))
        }

        fun getFormattedDateSimple(): String {
            val newFormat = SimpleDateFormat("MMM dd, yyyy")
            return newFormat.format(Date())
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

        val ADDRES_RECIVER = "Addres_Recived"
        val ADDRES = "Addres"
        val LATLANG = "LAtLang"
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
                    var intentet = Intent()
                    intentet.action = ADDRES_RECIVER
                    intentet.putExtra(ADDRES, strAdd)
                    intentet.putExtra(LATLANG, "$LATITUDE , $LONGITUDE")
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentet)
                    Log.w("TAG", strReturnedAddress.toString())
                } else {
                    Log.w("TAG", "No Address returned!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                strAdd += "\n($LATITUDE , $LONGITUDE)"
                var intentet = Intent()
                intentet.action = ADDRES_RECIVER
                intentet.putExtra(ADDRES, strAdd)
                intentet.putExtra(LATLANG, "$LATITUDE , $LONGITUDE")
                LocalBroadcastManager.getInstance(context).sendBroadcast(intentet)

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

        public fun isOnline(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return isInternetAvailable(context)
            }
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo != null) {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return true
                }
            } else {
                return false
            }
            return false
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
            return result
        }

        fun createImageFile(
            context: Context,
            qustion_id: BaseQustion,
            number: Int,
            tampletId: String?
        ): File {
            val storageDir: File
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                storageDir =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/" + qustion_id.getId()+"_"+qustion_id.getTittle()?.replace(" ","_") + "_images")
            } else {
                storageDir =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/"+ qustion_id.getId()+"_"+qustion_id.getTittle()?.replace(" ","_") + "_images")
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
            }
            storageDir.mkdirs()
            val image: File? = File(storageDir, "${qustion_id.getId()}_${number}_image.png")
            Log.e("TAG", "File  " + image?.absolutePath)
            if (image?.exists()!!)
                return image
            try {
                image.createNewFile()

            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.e("TAG", "File after create" + image?.absolutePath)
            return image
        }

        fun createImageFile(context: Context, qustion_id: BaseQustion, tampletId: String?): File? {
            val storageDir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                var base=    File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + "/PCN Inspection Images/")
                base.mkdir();
                base=File(base.absolutePath+"/"+ tampletId + "_images" + "/" + qustion_id.getId()+"_"+qustion_id.getTittle()?.replace(" ","_") + "_images")
                base.mkdirs()
                base
            }  else {
                File(Environment.getExternalStorageDirectory().absolutePath + "/PCN Inspection Images/" + tampletId + "_images" + "/"+ qustion_id.getId()+"_"+qustion_id.getTittle()?.replace(" ","_") + "_images");
            }
            deleteRecursive(storageDir)
            storageDir.mkdirs()
            val image: File? = File(storageDir, "${qustion_id.getId()}_image.png")
            Log.e("TAG", "File  " + image?.absolutePath)
            if (image?.exists()!!)
                return image
            try {
                image.createNewFile()
            } catch (e: IOException) {
                storageDir.mkdir()
                image.createNewFile()
                e.printStackTrace()
            }
            Log.e("TAG", "File after create" + image.absolutePath)
            return image
        }

    }

}