package com.depromeet.tmj.nuclear_insider_game

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

interface Api {
    companion object {
        fun create(): Api = Retrofit.Builder().apply {
            baseUrl("")
            client(OkHttpClient.Builder().build())
            addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        }.build().create(Api::class.java)
    }
}