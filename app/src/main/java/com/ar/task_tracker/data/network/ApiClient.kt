package com.ar.task_tracker.data.network

import com.ar.task_tracker.utils.AppConstant
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit Client builder
class ApiClient {
    companion object {
        private fun buildClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(
                    LoggingInterceptor.Builder()
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .build()

                ).addInterceptor { chain ->
                    val request = chain.request().newBuilder().build()
                    chain.proceed(request)
                }.build()
        }
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .client(buildClient())
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }
}