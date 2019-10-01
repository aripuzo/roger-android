package com.taleteam.roger.ui

import android.os.Bundle
import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughActivity
import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughCard
import android.widget.Toast
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import android.content.Intent
import androidx.core.content.ContextCompat
import com.taleteam.roger.R
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.identity.TwitterAuthClient


class IntroActivity : FancyWalkthroughActivity() {

    var pages = mutableListOf<FancyWalkthroughCard>()
    private var client: TwitterAuthClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = TwitterAuthClient()

        val fancywalkthroughCard1 = FancyWalkthroughCard("Title", "Description")
//        fancywalkthroughCard1.setBackgroundColor(R.color.white)
//        fancywalkthroughCard1.setTitleColor(R.color.black)
//        fancywalkthroughCard1.setDescriptionColor(R.color.black)

        val fancywalkthroughCard2 = FancyWalkthroughCard("Title", "Description")

        val fancywalkthroughCard3 = FancyWalkthroughCard("Title", "Description")

        pages.add(fancywalkthroughCard1)
        pages.add(fancywalkthroughCard2)
        pages.add(fancywalkthroughCard3)

        setOnboardPages(pages)

        setFinishButtonTitle("Login with twitter")
        showNavigationControls(true)
        setColorBackground(R.color.black_alpha73)
        setImageBackground(R.drawable.img)

        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button))

        defaultLoginTwitter()
    }

    override fun onFinishButtonPressed() {
        if (getTwitterSession() == null) {

            //if user is not authenticated start authenticating
            client?.authorize(this, object : Callback<TwitterSession>() {
                override fun success(result: com.twitter.sdk.android.core.Result<TwitterSession>) {

                    // Do something with result, which provides a TwitterSession for making API calls
                    val twitterSession = result.data

                    //call fetch email only when permission is granted
                    fetchTwitterEmail(twitterSession)
                }

                override fun failure(e: TwitterException) {
                    // Do something on failure
                    Toast.makeText(
                        this@IntroActivity,
                        "Failed to authenticate. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            //if user is already authenticated direct call fetch twitter email api
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show()
            fetchTwitterEmail(getTwitterSession())
        }
    }

    private fun defaultLoginTwitter() {
        if (getTwitterSession() != null) {
            fetchTwitterEmail(getTwitterSession())
        }
    }

    private fun getTwitterSession() = TwitterCore.getInstance().sessionManager.activeSession

    fun fetchTwitterEmail(twitterSession: TwitterSession) {
        client?.requestEmail(twitterSession, object : Callback<String>() {
            override fun success(result: com.twitter.sdk.android.core.Result<String>) {
                //here it will give u only email and rest of other information u can get from TwitterSession
                //userDetailsLabel.setText("User Id : " + twitterSession.userId + "\nScreen Name : " + twitterSession.userName + "\nEmail Id : " + result.data)
            }

            override fun failure(exception: TwitterException) {
                Toast.makeText(
                    this@IntroActivity,
                    "Failed to authenticate. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the twitterAuthClient.
        if (client != null)
            client?.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the login button.
        //twitterLoginButton.onActivityResult(requestCode, resultCode, data)
    }
}
