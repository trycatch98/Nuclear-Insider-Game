package com.depromeet.tmj.nuclear_insider_game

object RankingDataModel {

    data class RankingModel(
            val myScore: MyScore,
            val top10Score: List<Score>)

    data class MyScore (
            val myAnswerNum: Int,
            val myNickName: String,
            val myRank: Int)

    data class Score (
            val answerNum: Int,
            val nickName: String)
}