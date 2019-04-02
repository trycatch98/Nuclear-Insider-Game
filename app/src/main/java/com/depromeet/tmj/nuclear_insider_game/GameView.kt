package com.depromeet.tmj.nuclear_insider_game

import com.depromeet.tmj.nuclear_insider_game.model.QuizModel

interface GameView {
    fun setQuiz(quiz: QuizModel, current: Int)

    fun clearGame()

    fun useHint(hintCount: Int)

    fun notAvailableHint()

    fun adDialog()

    fun rightAnswer()

    fun wrongAnswer(heart: Int)

    fun gameOver()

    fun toastMessage(text: String)

    fun showRanking()
}