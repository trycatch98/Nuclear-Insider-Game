package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.depromeet.tmj.nuclear_insider_game.Model.ScoreModel
import com.depromeet.tmj.nuclear_insider_game.shared.ARG_NICKNAME
import com.depromeet.tmj.nuclear_insider_game.shared.BaseFragment
import com.depromeet.tmj.nuclear_insider_game.shared.SCHEMA_RANK
import com.depromeet.tmj.nuclear_insider_game.shared.THROTTLE_TIME
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.jetbrains.anko.AnkoLogger
import java.util.concurrent.TimeUnit


class RankingFragment : BaseFragment(), AnkoLogger {
    private val database = FirebaseDatabase.getInstance()
    private val scoreList = arrayListOf<ScoreModel>()
    private lateinit var nickname: String
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
            nickname = getString(ARG_NICKNAME, "")
        }
        getTop10Ranking()
    }

    private fun getTop10Ranking() {
        database.getReference(SCHEMA_RANK).orderByChild("answerNum")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach { snapshot ->
                            val score = snapshot.getValue(ScoreModel::class.java)
                            score?.let { scoreList.add(it) }
                        }
                        scoreList.reverse()

                        context?.let {
                            val adapter = if (scoreList.size < 10) {
                                RankingAdapter(it, scoreList.subList(0, scoreList.size))
                            } else {
                                RankingAdapter(it, scoreList.subList(0, 10))
                            }
                            recycler_view.adapter = adapter
                        }
                        getMyRanking()
                    }
                })

    }

    private fun getMyRanking() {
        scoreList.forEach { score ->
            if (score.nickName == nickname) {
                val myRank = scoreList.indexOf(score)

                tv_my_rank.text = (myRank + 1).toString()
                tv_my_nickname.text = nickname
                tv_my_answer.text = score.answerNum.toString()
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

    interface Listener {
        fun goToGameFragment()
    }
}
