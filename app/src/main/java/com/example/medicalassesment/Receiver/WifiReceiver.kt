package com.example.medicalassesment.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.service.UploadingService

import com.google.gson.Gson

class WifiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val string = Gson().toJson(intent.extras!!.keySet())
        Log.e("TAG", "extra $string")
        val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
       /* if (info != null && info.isConnected) {
            if (!Utils.isMyServiceRunning(UploadingService::class.java, context)) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, UploadingService::class.java)
                )
            }
        } else {
            if (UploadingService.INSTANCE != null) {
                UploadingService.INSTANCE?.stopUploading()
            }
            // UploadingService uploadVideo=UploadingService.getInstance(context);
            //   uploadVideo.stopUploading();
        }*/
    }
}
