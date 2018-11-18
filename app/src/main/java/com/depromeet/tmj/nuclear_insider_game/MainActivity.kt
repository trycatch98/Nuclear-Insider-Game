package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity(), StartFragment.Listener {
    private lateinit var nickname: String
    private lateinit var score: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
    }

    override fun setNicknameAndStartGame(nickname: String) {
        this.nickname = nickname
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment,
                        GameFragment().apply {
                            arguments = Bundle().apply {
                                putString("nickname", nickname)
                            }
                        }
                )
                .commitAllowingStateLoss()
    }

    private fun initUi() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, StartFragment())
                .commitAllowingStateLoss()
    }

    fun gameOver(score: String) {
        this.score = score
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, GameOverDialogFragment()).commit()
    }

    fun gameFinish(score: String) {
        this.score = score
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, ClearDialogFragment()).commit()
    }

    fun showRanking() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment,
                RankingFragment().apply {
                    arguments = Bundle().apply {
                        putString("nickname", nickname)
                        putString("score", score)
                    }
                }
        ).commit()
    }
}
