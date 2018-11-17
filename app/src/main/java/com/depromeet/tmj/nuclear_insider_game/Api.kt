package com.depromeet.tmj.nuclear_insider_game

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {
    @GET("quiz")
    fun getQuiz(@Query("selectedIds") selectedIds: String): Observable<List<QuizDataModel>>

    @POST("Score")
    fun putScore(@Body map: Map<String, Any>): Observable<RankingDataModel.RankingModel>

    companion object {
        fun create(): Api = Retrofit.Builder().apply {
            baseUrl("http://119.194.163.190:8080/")
            client(OkHttpClient.Builder().build())
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        }.build().create(Api::class.java)
    }
}