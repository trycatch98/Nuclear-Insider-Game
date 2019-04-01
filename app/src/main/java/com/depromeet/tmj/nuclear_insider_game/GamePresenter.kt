package com.depromeet.tmj.nuclear_insider_game

import com.depromeet.tmj.nuclear_insider_game.model.QuizModel
import com.depromeet.tmj.nuclear_insider_game.shared.BasePresenter
import com.depromeet.tmj.nuclear_insider_game.shared.SCHEMA_QUIZ
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GamePresenter(val view: GameView) : BasePresenter {
    private val database = FirebaseDatabase.getInstance()
    private val quizList = arrayListOf<QuizModel>()
    private lateinit var hintList: List<String>

    private var hintCount = 3
    private var heart = 5
    private var currentQuestion = 0
    private var passCount = 0


    private fun getQuiz() {
        val quizReference = database.getReference(SCHEMA_QUIZ)

        quizReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children

                children.forEach { snapshot ->
                    val quiz = snapshot.getValue(QuizModel::class.java)

                    quiz?.let { quizList.add(it) }
                }
                view.setQuiz(quizList)
            }
        })
    }
}