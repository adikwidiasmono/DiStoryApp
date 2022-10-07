package com.distory.app.data.common.interceptor

import android.content.Context
import com.distory.app.utils.MySharedPref
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor constructor(private val applicationContext: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest =
            if (chain.request().header("Authorization").isNullOrEmpty())
                chain.request().newBuilder().build()
            else
                chain.request().newBuilder()
                    // Add auth token in header
                    .header("Authorization", "Bearer ${MySharedPref(applicationContext).token()}")
                    .build()

        return chain.proceed(newRequest)
    }
}