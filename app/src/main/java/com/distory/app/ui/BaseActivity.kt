package com.distory.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.distory.app.utils.MySharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    protected lateinit var myPref: MySharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myPref = MySharedPref(applicationContext)
    }
}