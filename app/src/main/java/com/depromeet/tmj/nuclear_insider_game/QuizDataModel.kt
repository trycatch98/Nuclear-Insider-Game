package com.depromeet.tmj.nuclear_insider_game

data class QuizDataModel(
        val id: Int = 0,
        val solution: String = "",
        val problem: String = "",
        val hints: List<String> = listOf(),
        val category: String = ""
)