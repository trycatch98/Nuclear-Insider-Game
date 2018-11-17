package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import java.util.concurrent.TimeUnit


class RankingFragment : Fragment(), AnkoLogger {
    private lateinit var nickname: String
    private lateinit var score: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        arguments?.apply {
            nickname = getString("nickname")
            score = getString("score")
        }
        error { "$nickname, $score" }

        val data = mapOf("nickName" to nickname, "answerNum" to score)
        Api.create().putScore(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    context?.let { context ->
                        tv_my_nickname.text = data.myScore.myNickName
                        tv_my_rank.text = "${data.myScore.myRank + 1}"
                        tv_my_answer.text = data.myScore.myAnswerNum.toString()
                        recycler_view.adapter = RankingAdapter(context, data.top10Score)
                    }
                }) {
                    Log.d(this::class.java.simpleName, it.localizedMessage)
                }
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor()
        btn_regame.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe {
                    fragmentManager?.let {
                        it.beginTransaction().replace(R.id.fragment, GameFragment())
                                .commitAllowingStateLoss()
                    }
                }
    }

    private fun changeStatusBarColor() {
        val window = activity?.window
        window?.let { window ->
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context!!, R.color.background)
        }
    }
}
