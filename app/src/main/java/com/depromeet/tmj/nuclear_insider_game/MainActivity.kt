package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import com.depromeet.tmj.nuclear_insider_game.shared.BaseActivity
import com.depromeet.tmj.nuclear_insider_game.util.replace

class MainActivity : BaseActivity(), RankingFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goToStartFragment()
    }

    override fun goToStartFragment() {
        replace(supportFragmentManager, R.id.container,
                StartFragment(), StartFragment::class.java.simpleName)
    }
}
