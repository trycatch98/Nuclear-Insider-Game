package com.depromeet.tmj.nuclear_insider_game

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd

class MainActivity : AppCompatActivity(), StartFragment.Listener {
    private lateinit var nickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
    }

    override fun setNicknameAndStartGame(nickname: String) {
        this.nickname = nickname
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, GameFragment())
                .commitAllowingStateLoss()
    }

    private fun initUi() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, StartFragment())
                .commitAllowingStateLoss()
    }
}
