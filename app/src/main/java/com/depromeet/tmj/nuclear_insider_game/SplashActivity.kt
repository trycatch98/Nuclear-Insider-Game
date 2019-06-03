package com.depromeet.tmj.nuclear_insider_game

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.depromeet.tmj.nuclear_insider_game.shared.BaseActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initUi()
    }

    private fun initUi() {
        changeStatusBarColor()
        tv_team_name.paintFlags += Paint.UNDERLINE_TEXT_FLAG

        compositeDisposable.add(Observable.just(0)
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribe { goToMain() })
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeStatusBarColor() {
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_yellow)
    }
}
