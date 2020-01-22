package com.example.medicalassesment.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.R
import com.example.medicalassesment.ViewModels.*
import com.example.medicalassesment.adapter.PreviewAdapter
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.FeedBackModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UploadingService : Service(), LifecycleOwner {
    override fun getLifecycle(): Lifecycle {
        val mDispatcher = ServiceLifecycleDispatcher(this)
        return mDispatcher.lifecycle
    }

    private var notification: NotificationCompat.Builder? = null
    private lateinit var manager: NotificationManager
    private val CHANNEL_ONE_ID = "Package.Service"
    private val CHANNEL_ONE_NAME = "Medical Assessment";

    private var dataBaseDao = MedicalDataBase.getInstance(this).getDao();
    private lateinit var uploadViewModel: UploadViewModel


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        UploadingService.INSTANCE = this;
        if (Build.VERSION.SDK_INT >= 26) {
            //if more than 26
            if (Build.VERSION.SDK_INT > 26) {
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
            } else if (Build.VERSION.SDK_INT == 26) {
                run { startForeground(101, updateNotification()) }
            }//if version 26
        }
        Log.e("TAG", "Service Started")
        uploadViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(MyApplication.APPLICATION)
                .create(UploadViewModel::class.java)
        uploadViewModel.getViewState().observe(this, Observer {
            getViewState(it)
        })

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
            val connMgr =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    init {
        isUploading = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startUpload()
        return super.onStartCommand(intent, flags, startId)
    }

    private var currentProgress = 0;
    private fun getViewState(uplodaingViewState: UploadingViewState) {
        when (uplodaingViewState) {
            is Uploading -> {
                isUploading = true
                currentProgress++;
                notification?.setContentTitle(uplodaingViewState.model)
                notification?.setProgress(maxProgress, currentProgress, true)
            }
            is UploadingDone -> {
                uplodaingViewState.templateModel.isUploaded = true;
                GlobalScope.launch {
                    dataBaseDao.update(uplodaingViewState.templateModel)
                    currentProgress=0;
                    getNextUpload()
                }
            }
            is ErrorUploading -> {
                aboartUpaload()
            }
        }
    }

    private fun aboartUpaload() {
        Log.e("TAG", "No templete")

        try {
            stopForeground(true)
        } catch (e: Exception) {
        }
        try {
            manager.cancel(101)
        } catch (e: Exception) {
        }
        stopSelf()
    }

    private var maxProgress = 0;
    private fun getNextUpload() {
        GlobalScope.launch {
            var listtemplete = dataBaseDao.getTemplate(false)
            if (!listtemplete.isNullOrEmpty()) {
                GlobalScope.launch {
                    var templateModel = listtemplete[0];
                    var list: List<BaseQustion> = ArrayList()
                    var dao = MedicalDataBase.getInstance(this@UploadingService).getDao()
                    var baseQustion = FeedBackModel()
                    baseQustion.questiontype = "Header"
                    baseQustion.title = "Failed Items"
                    var qustionList = dao.getFaildItems(templateModel.id.toString())
                    list = list.plus(baseQustion)
                    list = list.plus(qustionList)

                    var prelimnryList = dao.getPrelimanryInfoNonlive(templateModel.id.toString())
                    baseQustion = FeedBackModel()
                    baseQustion.questiontype = "Header"
                    baseQustion.title = "Preliminary Information"
                    list = list.plus(baseQustion)
                    list = list.plus(prelimnryList)

                    qustionList = dao.getQuestionsNonFail(templateModel.id.toString())
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
                    maxProgress = list.size
                    uploadViewModel.startUpload(listtemplete[0])
                }

            } else {
                aboartUpaload()
            }
        }
    }

    private fun startUpload() {
        Log.e("TAG", "uploading Started")

        if (isOnline) {
            //sendBroadcast(Intent(UploadStart))
            getNextUpload()
            isUploading = true
        } else aboartUpaload()
    }


    fun stopUploading() {
        try {
            isUploading = false
            aboartUpaload()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        aboartUpaload()
        super.onDestroy()
    }

    companion object {
        var UploadStart = "UploadingStart"
        var StopUploading = "StopUploading"
        var UploadingDone = "UploadingDone"
        var INSTANCE: UploadingService? = null
    }
}
