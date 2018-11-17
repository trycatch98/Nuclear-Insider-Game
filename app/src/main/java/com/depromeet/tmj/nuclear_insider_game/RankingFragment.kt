package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
<<<<<<< HEAD
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error


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
=======
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.jetbrains.anko.Android


class RankingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val data = mapOf("answerNum" to 1, "nickname" to "adsada")
        Api.create().putScore(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { data ->
                    context?.let { context ->
                        recycler_view.adapter = RankingAdapter(context, data.top10Score)
                        // TODO : 내 점수 출력
                    }
                }
>>>>>>> 282ba8fa5f09bf3ecf2031a1516136b073ccdac3
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor()
    }

    private fun changeStatusBarColor() {
        val window = activity?.window
        window?.let { window ->
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context!!, R.color.background)
        }
    }
<<<<<<< HEAD

=======
>>>>>>> 282ba8fa5f09bf3ecf2031a1516136b073ccdac3
}
