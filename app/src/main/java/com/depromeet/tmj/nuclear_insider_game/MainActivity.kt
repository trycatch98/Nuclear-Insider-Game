package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import com.depromeet.tmj.nuclear_insider_game.Model.ScoreModel
import com.depromeet.tmj.nuclear_insider_game.shared.ARG_NICKNAME
import com.depromeet.tmj.nuclear_insider_game.shared.BaseActivity
import com.depromeet.tmj.nuclear_insider_game.shared.SCHEMA_RANK
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.toast

class MainActivity : BaseActivity(), StartFragment.Listener, RankingFragment.Listener {
    private val database = FirebaseDatabase.getInstance()
    private lateinit var nickname: String
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
    }

    override fun setNickname(nickname: String) {
        this.nickname = nickname
    }

    override fun goToGameFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, GameFragment()).commitAllowingStateLoss()
    }

    private fun initUi() {
        supportFragmentManager.beginTransaction().replace(R.id.container, StartFragment())
                .commitAllowingStateLoss()
    }

    fun gameOver(score: Int) {
        this.score = score
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, GameOverDialogFragment()).commit()
    }

    fun gameFinish(score: Int) {
        this.score = score
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, ClearDialogFragment()).commit()
    }

    private fun showRanking() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.container,
                RankingFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NICKNAME, nickname)
                    }
                }
        ).commit()
    }

    fun saveMyScore() {
        val score = ScoreModel(score, nickname)

        database.getReference(SCHEMA_RANK).push().setValue(score).addOnSuccessListener {
            showRanking()
        }.addOnFailureListener {
            toast("점수 정보 저장에 실패하였습니다.")
        }
    }
}
