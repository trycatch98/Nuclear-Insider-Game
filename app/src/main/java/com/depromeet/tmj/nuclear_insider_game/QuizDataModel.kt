package com.depromeet.tmj.nuclear_insider_game

data class QuizDataModel(
        val id: String,
        val solution: String,
        val imagePath: String,
        val hints: List<String>,
        val category: String
)