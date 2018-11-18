package com.depromeet.tmj.nuclear_insider_game

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {
    @GET("quiz")
    fun getQuiz(@Query("selectedIds") selectedIds: String): Observable<List<QuizDataModel>>

    @POST("Score")
    @Headers("Content-Type: application/json")
    fun putScore(@Body data: Map<String, String>): Observable<RankingDataModel.RankingModel>

    companion object {
        fun create(): Api = Retrofit.Builder().apply {
            baseUrl("https://nuclear-insider-game-server.herokuapp.com/")
            client(OkHttpClient.Builder().build())
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        }.build().create(Api::class.java)
    }
}