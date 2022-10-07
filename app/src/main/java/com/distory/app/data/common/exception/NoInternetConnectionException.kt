package com.distory.app.data.common.exception

import android.content.Context
import com.distory.app.R
import okio.IOException

class NoInternetConnectionException(private val appContext: Context) : IOException() {
    override val message: String
        get() = appContext.getString(R.string.err_offline)
}