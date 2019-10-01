package com.taleteam.roger

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.TwitterConfig
import android.util.Log
import androidx.multidex.MultiDexApplication
import timber.log.Timber


/**
 * Created by ari on 10/02/2018.
 */

class App: MultiDexApplication() {

    companion object {
        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

//    lateinit var applicationComponent: ApplicationComponent
//        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

//        this.applicationComponent = DaggerApplicationComponent.builder()
//                .applicationModule(ApplicationModule(this))
//                .build()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

//        OneSignal.startInit(this)
//            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//            .unsubscribeWhenNotificationsAreDisabled(true)
//            .init()

        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    resources.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                    resources.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)
                )
            )//pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

        //finally initialize twitter with created configs
        Twitter.initialize(config)

    }
}
