package com.inc.silence.weather.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.inc.silence.weather.BuildConfig
import com.inc.silence.weather.data.WeatherService
import com.inc.silence.weather.di.ServiceProperties.API_KEY
import com.inc.silence.weather.di.ServiceProperties.SERVER_URL
import com.inc.silence.weather.di.ServiceProperties.UNITS
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {
    single { createOkHttpClient() }
    single { createWebService<WeatherService>(get(), SERVER_URL) }
}


object ServiceProperties {
    const val SERVER_URL = "https://api.openweathermap.org/"
    const val API_KEY = "46370e4b14a0a7ca2af3bf40954e7fad"
    const val UNITS = "metric"
}

fun createOkHttpClient(): OkHttpClient {
    val okHttpClientBuilder = OkHttpClient.Builder()

    okHttpClientBuilder.addInterceptor({ chain ->
        var request = chain.request()
        val url = request.url().newBuilder()
                .addQueryParameter("appid", API_KEY)
                .addQueryParameter("units", UNITS)
        request = request.newBuilder()
                .url(url.build())
                .build()
        chain.proceed(request)
    })

    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
    }
    return okHttpClientBuilder.build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String) : T {
    val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    return retrofit.create(T::class.java)
}