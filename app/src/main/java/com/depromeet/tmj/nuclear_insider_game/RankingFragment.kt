package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.depromeet.tmj.nuclear_insider_game.shared.BaseFragment
import com.depromeet.tmj.nuclear_insider_game.shared.THROTTLE_TIME
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.jetbrains.anko.AnkoLogger
import java.util.concurrent.TimeUnit


class RankingFragment : BaseFragment(), AnkoLogger {
    private lateinit var nickname: String
    private lateinit var score: String
    private lateinit var listener: Listener


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initData()
    }

    private fun initUi() {
        changeStatusBarColor()
        compositeDisposable.add(btn_regame.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe { listener.goToGameFragment() })
    }

    private fun initData() {
        arguments?.apply {
            nickname = getString("nickname", "")
            score = getString("score", "0")
        }
        getTop10Ranking()
        getMyRanking()
    }

    private fun getTop10Ranking() {

    }

    private fun getMyRanking() {

    }

    private fun changeStatusBarColor() {
        val window = activity?.window
        window?.let { window ->
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context!!, R.color.background)
        }
    }

    interface Listener {
        fun goToGameFragment()
    }
}
