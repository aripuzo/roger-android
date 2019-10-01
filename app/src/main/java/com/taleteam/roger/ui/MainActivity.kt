package com.taleteam.roger.ui

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.*
import com.google.android.material.snackbar.Snackbar
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import kotlinx.android.synthetic.main.activity_main.*
import com.taleteam.roger.R
import com.taleteam.roger.utilities.BaseUtils


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController

    var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activityStarted
            && intent != null
            && intent.flags and Intent.FLAG_ACTIVITY_REORDER_TO_FRONT != 0
        ) {
            finish()
            return
        }

        activityStarted = true

        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.main_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigation.setupWithNavController(navController)
        navigation.setOnNavigationItemSelectedListener {item ->
            NavigationUI.onNavDestinationSelected(item, navController)
        }

        askPermission()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private var activityStarted: Boolean = false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    private fun scheduleJobUploadContacts() {
        val componentName = ComponentName(this, ContactsJobSchedulerService::class.java)
        val builder = JobInfo.Builder(1, componentName)
        builder.setPeriodic((7 * 24 * 60 * 60 * 1000))
//        builder.setMinimumLatency((1 * 1000).toLong()) // wait at least
//        builder.setOverrideDeadline((3 * 1000).toLong()) // maximum delay
        builder.setBackoffCriteria(6000, JobInfo.BACKOFF_POLICY_LINEAR)
        builder.setPersisted(true)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        val jobScheduler = App.applicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

    private fun showCustomDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        dialogView.findViewById<View>(R.id.allow_btn).setOnClickListener {
            alertDialog.dismiss()
            askPermission()
            BaseUtils(this).welcomed = true
        }
        alertDialog.show()
    }

    private fun askPermission(){
        askPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION) {
            onGranted {

            }
            onDenied { permissions ->
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Grant Permission")
                    .setMessage("You should grant location access permissions to use app")
                    .setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
                    .create()
                alertDialog.show()
            }

            onShowRationale { request ->
                Snackbar.make(toolbar, "You should grant location access permissions to use app"
                    , Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") { request.retry() }
                    .show()
            }

            onNeverAskAgain { permissions ->
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Grant Permission")
                    .setMessage("You should grant location access permissions to use app")
                    .setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
                    .create()
                alertDialog.show()
            }
        }
    }

}
