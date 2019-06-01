package com.depromeet.tmj.nuclear_insider_game

import com.depromeet.tmj.nuclear_insider_game.model.QuizModel
import com.depromeet.tmj.nuclear_insider_game.model.ScoreModel
import com.depromeet.tmj.nuclear_insider_game.shared.BasePresenter
import com.depromeet.tmj.nuclear_insider_game.shared.SCHEMA_QUIZ
import com.depromeet.tmj.nuclear_insider_game.shared.SCHEMA_RANK
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class GamePresenter(private val view: GameView,
                    private val nickname: String) : BasePresenter {
    private val database = FirebaseDatabase.getInstance()
    private val quizList = arrayListOf<QuizModel>()
    private lateinit var answer: String
    private var hintCount = DEFAULT_HINT_COUNT
    private var heart = DEFAULT_HEART_COUNT
    private var score = 0
    private var passCount = 0


    fun getQuiz() {
        val quizReference = database.getReference(SCHEMA_QUIZ)

        quizReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children

                children.forEach { snapshot ->
                    val quiz = snapshot.getValue(QuizModel::class.java)

                    quiz?.let { quizList.add(it) }
                }
                setNextQuiz()
            }
        })
    }

    fun setNextQuiz() {
        if (quizList.isNotEmpty()) {
            val nextQuiz = quizList.removeAt(Random().nextInt(quizList.size))

            answer = nextQuiz.solution
            view.setQuiz(nextQuiz, score + passCount + 1)
            hintCount = DEFAULT_HINT_COUNT
        } else {
            view.clearGame()
        }
    }

    fun pass() {
        passCount++
        setNextQuiz()
    }

    fun onRewarded() {
        view.useHint(hintCount--)
    }

    fun getHint() {
        if (hintCount <= 0) {
            view.notAvailableHint()
        } else {
            view.adDialog()
        }
    }

    fun checkAnswer(answer: String) {
        if (this.answer == answer) {
            score++
            setNextQuiz()
        } else {
            if (--heart <= 0) {
                view.gameOver()
            }
            view.wrongAnswer(heart)
        }
    }

    fun saveScore() {
        val score = ScoreModel(score, nickname)

        database.getReference(SCHEMA_RANK).push().setValue(score).addOnSuccessListener {
            view.showRanking()
        }.addOnFailureListener {
            view.toastMessage("점수 정보 저장에 실패하였습니다.")
        }
    }

    companion object {
        private const val DEFAULT_HINT_COUNT = 3
        private const val DEFAULT_HEART_COUNT = 5
    }
}