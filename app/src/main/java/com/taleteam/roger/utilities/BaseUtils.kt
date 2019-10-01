package com.taleteam.roger.utilities

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by ari on 12/2/2017.
 */
class BaseUtils (context : Context) {

    private val TAG = "com.taleteam.roger"
    private val PREF_NAME = "$TAG.prefs"
    private val LOGGED_IN = "logged_in"
    private val DEVICE = "device_saved"
    private val ADMIN_DATA = "admin_data"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, 0)


    var loggedIn: Boolean
        get() = prefs.getBoolean(LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(LOGGED_IN, value).apply()

    var deviceSaved: Boolean
        get() = prefs.getBoolean(DEVICE, false)
        set(value) = prefs.edit().putBoolean(DEVICE, value).apply()

    fun clear(){
        prefs.edit().clear()
    }

//    var adminUserData : User
//        get() {
//            val savedUserString = prefs.getString(ADMIN_DATA,"")
//            val gson = Gson()
//            return gson.fromJson(savedUserString, User::class.java)
//        }
//        set(value) {
//            val gson = Gson()
//            prefs.edit().putString(ADMIN_DATA, gson.toJson(value)).apply()
//        }

}

