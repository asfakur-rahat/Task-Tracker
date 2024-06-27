package com.ar.task_tracker.data.network

import com.ar.task_tracker.utils.AppConstant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private fun buildClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }).addInterceptor { chain ->
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