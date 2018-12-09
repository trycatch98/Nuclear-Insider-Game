package com.depromeet.tmj.nuclear_insider_game.Model

data class QuizModel(
        val id: Int = 0,
        val solution: String = "",
        val problem: String = "",
        val hints: List<String> = listOf(),
        val category: String = ""
)