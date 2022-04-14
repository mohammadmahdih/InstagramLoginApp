package com.mmahdihx.instagramlogin

import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @POST("login/")
    @FormUrlEncoded
    fun loginToInstagram(@FieldMap data: Map<String,String>): Single<Response>
}

class ApiClient {
    fun apiServiceInstance(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val oldRequest = it.request()
                val newRequestBuilder = oldRequest.newBuilder()
                newRequestBuilder.addHeader(
                    "User-Agent",
                    "Instagram 135.0.0.34.124 Android (24/5.0; 515dpi; 1440x2416; huawei/google; Nexus 6P; angler; angler; en_US)"
                )
                newRequestBuilder.method(oldRequest.method, oldRequest.body)
                return@addInterceptor it.proceed(newRequestBuilder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
        return Retrofit.Builder()
            .baseUrl("https://i.instagram.com/api/v1/accounts/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient.build())
            .build()

    }
}
