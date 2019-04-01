package com.depromeet.tmj.nuclear_insider_game

import com.depromeet.tmj.nuclear_insider_game.model.QuizModel

interface GameView {
    fun setQuiz(quizList: List<QuizModel>)
}