package com.depromeet.tmj.nuclear_insider_game

import android.content.Intent
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import io.reactivex.disposables.CompositeDisposable


class SplashActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun initUi() {
        changeStatusBarColor()
        tv_team_name.paintFlags += Paint.UNDERLINE_TEXT_FLAG

        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_trans_down)
        anim.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                goToMain()
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
        tv_title.startAnimation(anim)
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
    }
}
