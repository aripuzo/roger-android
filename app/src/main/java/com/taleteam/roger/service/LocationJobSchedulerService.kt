package com.taleteam.roger.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.taleteam.roger.BuildConfig
import com.taleteam.roger.utilities.BaseUtils
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by ari on 2019-08-19.
 */

class LocationJobSchedulerService : JobService() {

    var isWorking = false
    var jobCancelled = false
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onStartJob(params: JobParameters): Boolean {
        if(BaseUtils(this).loggedIn) {
            isWorking = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        addLocation(location, params)
                    }
                }
                .addOnFailureListener {

                }
            return isWorking
        }
        else
            return isWorking
    }

    private fun addLocation(location: Location, params: JobParameters) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()

        val body = RequestBody.create(JSON, Gson().toJson(
            AddLocationRequest(
                location.latitude, location.longitude
            )
        ))
        val request = Request.Builder()
            .header("Auth-Token", BaseUtils(this).token!!)
            .url(APIBaseUrl+"locations/add")
            .post(body)
            .build()
        val call = client.newCall(request)
        if (jobCancelled)
            return
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Something went wrong
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    isWorking = false
                    jobFinished(params, false)
                } else {
                    // Request not successful
                }
            }
        })

    }

    override fun onStopJob(params: JobParameters): Boolean {
        jobCancelled = true
        val needsReschedule = isWorking
        jobFinished(params, needsReschedule)
        return needsReschedule
    }
}