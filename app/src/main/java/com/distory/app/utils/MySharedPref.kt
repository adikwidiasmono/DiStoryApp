package com.distory.app.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPref(private val context: Context) {

    private fun pref(): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun editor(): SharedPreferences.Editor {
        return pref().edit()
    }

    fun email(): String? {
        return pref().getString(KEY_EMAIL, null)
    }

    fun email(email: String) {
        editor().apply {
            putString(KEY_EMAIL, email)
            commit()
        }
    }

    fun name(): String? {
        return pref().getString(KEY_NAME, null)
    }

    fun name(name: String) {
        editor().apply {
            putString(KEY_NAME, name)
            commit()
        }
    }

    fun token(): String? {
        return pref().getString(KEY_ACCESS_TOKEN, null)
    }

    fun token(token: String) {
        editor().apply {
            putString(KEY_ACCESS_TOKEN, token)
            commit()
        }
    }

    fun clearLoginData() {
        editor().apply {
            remove(KEY_ACCESS_TOKEN)
            commit()
        }
    }

    companion object {
        private const val SHARED_PREF_NAME = "Di Story"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_ACCESS_TOKEN = "token"
    }
}