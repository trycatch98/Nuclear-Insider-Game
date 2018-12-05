package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import com.depromeet.tmj.nuclear_insider_game.shared.BaseActivity

class MainActivity : BaseActivity(), StartFragment.Listener, RankingFragment.Listener {
    private lateinit var nickname: String
    private lateinit var score: String

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

    fun gameOver(score: String) {
        this.score = score
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, GameOverDialogFragment()).commit()
    }

    fun gameFinish(score: String) {
        this.score = score
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, ClearDialogFragment()).commit()
    }

    fun showRanking() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container,
                RankingFragment().apply {
                    arguments = Bundle().apply {
                        putString("nickname", nickname)
                        putString("score", score)
                    }
                }
        ).commit()
    }
}
