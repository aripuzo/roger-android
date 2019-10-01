package com.taleteam.roger.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taleteam.roger.utilities.BaseUtils

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(BaseUtils(this@StartActivity).loggedIn) {
            startActivity(Intent(this@StartActivity, IntroActivity::class.java))
        }
        else {
            startActivity(Intent(this@StartActivity, IntroActivity::class.java))
        }
        finish()

    }
}


