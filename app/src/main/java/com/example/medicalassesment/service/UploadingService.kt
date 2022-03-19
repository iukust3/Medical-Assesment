package com.example.medicalassesment.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.*
import com.example.medicalassesment.Activities.BaseActivity
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.database.Dao
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.FeedBackModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.max

class UploadingService : Service(),LifecycleOwner {


    private var notification: NotificationCompat.Builder? = null
    private lateinit var manager: NotificationManager
    private val CHANNEL_ONE_ID = "Package.Service"
    private val CHANNEL_ONE_NAME = "Medical Assessment";

    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var dataBaseDao :Dao
    override fun getLifecycle(): Lifecycle {
        val mDispatcher = ServiceLifecycleDispatcher(this)
        return mDispatcher.lifecycle
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        dataBaseDao=MedicalDataBase.getInstance(this).getDao();
        try {
            uploadStatus=BaseActivity.uploadStatus
        } catch (e: Exception) {
        }
        if (Build.VERSION.SDK_INT >= 26) {
            //if more than 26
            when {
                Build.VERSION.SDK_INT > 26 -> {
                    var notificationChannel: NotificationChannel? = null
                    notificationChannel = NotificationChannel(
                        CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN
                    )
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.BLUE
                    notificationChannel.setShowBadge(true)
                    notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(notificationChannel)

                    val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                    notification = NotificationCompat.Builder(applicationContext, CHANNEL_ONE_ID)
                        .setChannelId(CHANNEL_ONE_ID)
                        .setContentTitle("Checking for Inspection Tools")
                        .setContentText("Inspection Tools are uploading in background")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(icon);
                    startForeground(101, notification?.build())
                }
                Build.VERSION.SDK_INT == 26 -> {
                    run { startForeground(101, updateNotification()) }
                }
                else -> {

                }
            }
            //if version 26
        }else {
            with(NotificationManagerCompat.from(this)){
                updateNotification()?.let { notify(101, it) }
            }
        }


    }


    private fun updateNotification(): Notification? {
        notification = NotificationCompat.Builder(this, CHANNEL_ONE_ID)
            .setContentTitle("Activity log")
            .setTicker("Checking for Inspection Tools")
            .setContentText("Inspection Tools are uploading in background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
        return notification?.build()
    }

    var isUploading: Boolean = false
        private set
    private val isOnline: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return isInternetAvailable(this)
            }
            val connMgr =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo != null) {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI ||
                    networkInfo.type == ConnectivityManager.TYPE_ETHERNET ||
                    networkInfo.type == ConnectivityManager.TYPE_MOBILE ) {
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

    init {
        isUploading = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        INSTANCE=this
        uploadViewModel =
            AndroidViewModelFactory.getInstance(MyApplication.APPLICATION)
                .create(UploadViewModel::class.java)
        uploadViewModel.getViewState().observeForever {
            Log.e("TAG","For ever")
            getViewState(it)
        }
       /* uploadViewModel.getViewState().observe(this, Observer {
            getViewState(it)
        })*/
        startUpload()
        return super.onStartCommand(intent, flags, startId)
    }

    private var currentProgress = 0;
    fun getViewState(uplodaingViewState: UploadingViewState) {
        when (uplodaingViewState) {
            is Uploading -> {
                Log.e("TAG"," state ${uplodaingViewState.model}")
                isUploading = true
                currentProgress++;
                uploadStatus?.uploading(uplodaingViewState.model)
                notification?.setContentTitle(uplodaingViewState.model)
                notification?.setProgress(maxProgress, currentProgress, true)
            }
            is UploadingDone -> {
                uplodaingViewState.templateModel.isUploaded = true;
                GlobalScope.launch {
                    dataBaseDao.update(uplodaingViewState.templateModel)
                    currentProgress = 0;
                    getNextUpload()
                }
                uploadStatus?.uploaded("All completed Inspection successfully uploaded.")
            }
            is ErrorUploading -> {
                val error="Error while Uploading inspection\n"+uplodaingViewState.message
                aboartUpaload(error)
                uploadStatus?.uploaded(error)
            }
        }
    }

    private fun aboartUpaload(message: String) {
        Log.e("TAG", "No templete")
        uploadStatus?.uploaded(message)
        try {
            stopForeground(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            manager.cancel(101)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopSelf()
    }
var count=0;
    private var maxProgress = 0;
    private fun getNextUpload() {
        //count++;
        GlobalScope.launch {
          //  if(count<=2) {
                val listtemplete = dataBaseDao.getTemplate(false)
               Log.e("TAG","size ${listtemplete.size}")
                if (!listtemplete.isNullOrEmpty()) {
                        var templateModel = listtemplete[0];
                        var list: List<BaseQustion> = ArrayList()
                        var dao = MedicalDataBase.getInstance(this@UploadingService).getDao()
                        var baseQustion = FeedBackModel()
                        baseQustion.questiontype = "Header"
                        baseQustion.title = "Failed Items"
                        var qustionList = dao.getFaildItems(templateModel.id.toString())
                        list = list.plus(baseQustion)
                        list = list.plus(qustionList)

                        var prelimnryList =
                            dao.getPrelimanryInfoNonlive(templateModel.id.toString())
                        baseQustion = FeedBackModel()
                        baseQustion.questiontype = "Header"
                        baseQustion.title = "Preliminary Information"
                        list = list.plus(baseQustion)
                        list = list.plus(prelimnryList)

                        qustionList = dao.getQuestionsNonlive(templateModel.id.toString())
                        baseQustion = FeedBackModel()
                        baseQustion.questiontype = "Header"
                        baseQustion.title = "Questions"
                        list = list.plus(baseQustion)
                        list = list.plus(qustionList)
                        var feedBackList = dao.getFeedBackNonlive(templateModel.id.toString())
                        baseQustion = FeedBackModel()
                        baseQustion.questiontype = "Header"
                        baseQustion.title = "Feedback Summary"
                        list = list.plus(baseQustion)
                        list = list.plus(feedBackList)
                        maxProgress = list.size
                        uploadStatus?.uploadStarting(maxProgress, templateModel)
                        uploadViewModel.startUpload(templateModel)
                } else {
                    //uploadStatus?.uploaded(1)
                    aboartUpaload("All completed Inspection successfully uploaded.")
                }
          /*  }else{
                aboartUpaload("All completed Inspection successfully uploaded.")
            }*/
        }
    }

    private fun startUpload() {
        if (isOnline) {
            //sendBroadcast(Intent(UploadStart))
            getNextUpload()
            isUploading = true
        } else aboartUpaload("Wifi bot available...")
    }


    fun stopUploading() {
        try {
            isUploading = false
            aboartUpaload("Done")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
companion object {
     var uploadStatus: BaseActivity.UploadStatus? =null
    lateinit var INSTANCE:UploadingService
}
}
