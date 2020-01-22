package com.example.medicalassesment.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.medicalassesment.MyApplication
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection

/**
 * Helper class to automatically inject fragments if they implement [Injectable].
 */
object AppInjector {
    fun init(application: MyApplication) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                // do nothing
            }

            override fun onActivityResumed(activity: Activity) {
                // do nothing
            }

            override fun onActivityPaused(activity: Activity) {
                // do nothing
            }

            override fun onActivityStopped(activity: Activity) {
                // do nothing
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                // do nothing
            }

            override fun onActivityDestroyed(activity: Activity) {
                // do nothing
            }
        })
    }

    private fun handleActivity(activity: Activity) {
        Log.e("TAG","name "+activity::class.java.simpleName)
        if (activity is HasAndroidInjector) {
            Log.e("TAG","name "+activity::class.java.simpleName)

           // AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(
                            fm: FragmentManager,
                            f: Fragment,
                            savedInstanceState: Bundle?
                        ) {
                            if (f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }
                    }, true
                )
        }
    }
}
