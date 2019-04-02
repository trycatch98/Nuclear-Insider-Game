package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.depromeet.tmj.nuclear_insider_game.model.QuizModel
import com.depromeet.tmj.nuclear_insider_game.shared.*
import com.depromeet.tmj.nuclear_insider_game.util.add
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_game.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import java.util.concurrent.TimeUnit

class GameFragment : BaseFragment(), GameView {
    private lateinit var hintTextList: MutableList<AppCompatTextView>
    private lateinit var hintImgList: MutableList<AppCompatImageView>
    private lateinit var heartImgList: MutableList<AppCompatImageView>
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var presenter: GamePresenter
    private lateinit var nickname: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        presenter = GamePresenter(this, nickname)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hintTextList = mutableListOf(hint_text1, hint_text2, hint_text3)
        hintImgList = mutableListOf(hint_img1, hint_img2, hint_img3)
        heartImgList = mutableListOf(heart_img1, heart_img2, heart_img3, heart_img4, heart_img5)
        presenter.getQuiz()
        initAd()
        initUi()
    }

    override fun setQuiz(quiz: QuizModel, current: Int) {
        category_text.text = quiz.category
        emoji_view.text = quiz.problem
        order_text.text = String.format("Q%d.", current)
        hintImgList.forEach {
            Glide.with(context!!)
                    .load(R.drawable.hint_icon)
                    .into(it)
        }
        hintTextList.forEach {
            it.visibility = View.GONE
            it.text = quiz.hints[hintTextList.indexOf(it)]
        }

    }

    override fun useHint(hintCount: Int) {
        context?.let { context ->
            hintTextList[3 - hintCount + 1].run {
                visibility = View.VISIBLE
            }
            Glide.with(context)
                    .load(R.drawable.hint_broken_icon)
                    .into(hintImgList[hintCount])
        }
    }

    private fun initData() {
        arguments?.let { bundle ->
            this.nickname = bundle.getString(ARG_NICKNAME, "")
        }
    }

    private fun initAd() {
        val adListener = RewardAdListener()

        adListener.setAdListener(object : RewardAdListener.AdListener {
            override fun onRewardedVideoAdClosed() {
                loadRewardedVideoAd()
            }

            override fun onRewarded() {
                presenter.onRewarded()
            }
        })

        if (BuildConfig.DEBUG) {
            MobileAds.initialize(context, ADMOB_TEST_KEY)
        } else {
            MobileAds.initialize(context, ADMOB_APP_KEY)
        }
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAd.rewardedVideoAdListener = adListener
        loadRewardedVideoAd()
    }

    override fun clearGame() {
        val clearDialogFragment = ClearDialogFragment()

        clearDialogFragment.setListener {
            presenter.saveScore()
        }
        fragmentManager?.beginTransaction()
                ?.add(R.id.container, clearDialogFragment)
                ?.commitAllowingStateLoss()
    }

    override fun notAvailableHint() {
        toast("힌트 기회를 모두 사용했습니다.")
    }

    override fun adDialog() {
        alert("힌트를 보기 위해서\n광고를 보시겠어요?") {
            yesButton {
                if (rewardedVideoAd.isLoaded) {
                    rewardedVideoAd.show()
                }
            }
            noButton { }
        }.show()
    }

    private fun initUi() {
        compositeDisposable.add(hint_btn.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    presenter.getHint()
                }
        )

        compositeDisposable.add(confirm_btn.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    presenter.checkAnswer(removeSpace(answer_text.text.toString()))
                })

        compositeDisposable.add(pass_view.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    presenter.pass()
                })
    }

    override fun rightAnswer() {
        answer_text.setText("")
        toast("정답입니다.")
    }

    override fun wrongAnswer(heart: Int) {
        toast("틀렸습니다.")
        answer_text.setText("")
        context?.let { context ->
            Glide.with(context)
                    .load(R.drawable.heart_broken_icon)
                    .into(heartImgList[heart])
        }
    }

    override fun gameOver() {
        val gameOverDialogFragment = GameOverDialogFragment()

        gameOverDialogFragment.setListener {
            presenter.saveScore()
        }
        fragmentManager?.beginTransaction()
                ?.add(R.id.container, gameOverDialogFragment)
                ?.commitAllowingStateLoss()
    }

    override fun onPause() {
        super.onPause()
        rewardedVideoAd.pause(context)
    }

    override fun onResume() {
        super.onResume()
        rewardedVideoAd.resume(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        rewardedVideoAd.rewardedVideoAdListener = null
        rewardedVideoAd.destroy(context)
    }

    private fun loadRewardedVideoAd() {
        if (BuildConfig.DEBUG) {
            rewardedVideoAd.loadAd(ADMOB_TEST_KEY, AdRequest.Builder().build())
        } else {
            rewardedVideoAd.loadAd(ADMOB_APP_KEY, AdRequest.Builder().build())
        }
    }

    override fun toastMessage(text: String) {
        toast(text)
    }

    override fun showRanking() {
        fragmentManager?.let { fragmentManager ->
            add(fragmentManager, R.id.container,
                    RankingFragment().apply {
                        arguments = Bundle().apply {
                            putString(ARG_NICKNAME, nickname)
                        }
                    }, RankingFragment::class.java.simpleName)
        }
    }

    companion object {
        fun newInstance(name: String): GameFragment {
            val fragment = GameFragment()
            val bundle = Bundle()

            bundle.putString(ARG_NICKNAME, name)
            fragment.arguments = bundle

            return fragment
        }
    }
}