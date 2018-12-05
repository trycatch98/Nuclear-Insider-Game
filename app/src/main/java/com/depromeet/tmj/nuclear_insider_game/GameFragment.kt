package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.depromeet.tmj.nuclear_insider_game.shared.ADMOB_APP_KEY
import com.depromeet.tmj.nuclear_insider_game.shared.BaseFragment
import com.depromeet.tmj.nuclear_insider_game.shared.RewardAdListener
import com.depromeet.tmj.nuclear_insider_game.shared.THROTTLE_TIME
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_game.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.concurrent.TimeUnit

class GameFragment : BaseFragment() {
    private lateinit var hintList: List<String>
    private val idArray = mutableListOf<String>()
    private val hintViewArray = mutableListOf<AppCompatTextView>()
    private val hintImgArray = mutableListOf<AppCompatImageView>()
    private var hintCount = 3
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private val quizList = mutableListOf<QuizDataModel>()
    private var heart = 5
    private val heartImgArray = mutableListOf<AppCompatImageView>()
    private var currentQuestion = 0
    private var passCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hintViewArray.add(hint_text1)
        hintViewArray.add(hint_text2)
        hintViewArray.add(hint_text3)

        hintImgArray.add(hint_img1)
        hintImgArray.add(hint_img2)
        hintImgArray.add(hint_img3)

        heartImgArray.add(heart_img1)
        heartImgArray.add(heart_img2)
        heartImgArray.add(heart_img3)
        heartImgArray.add(heart_img4)
        heartImgArray.add(heart_img5)

        initUi()
        getQuiz()
        initAd()
    }

    /**
     * 광고 초기화
     */
    private fun initAd() {
        val adListener = RewardAdListener()

        adListener.setAdListener(object : RewardAdListener.AdListener {
            override fun onRewardedVideoAdClosed() {
                loadRewardedVideoAd()
            }

            override fun onRewarded() {
                hintViewArray[3 - hintCount].run {
                    text = hintList[3 - hintCount]
                    visibility = View.VISIBLE
                }
                Glide.with(context!!)
                        .load(R.drawable.hint_broken_icon)
                        .into(hintImgArray[--hintCount])
            }
        })
        MobileAds.initialize(context, ADMOB_APP_KEY)
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        mRewardedVideoAd.rewardedVideoAdListener = adListener
        loadRewardedVideoAd()
    }

    private fun initUi() {
        compositeDisposable.add(hint_btn.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (hintCount <= 0) {
                        toast("힌트 기회를 모두 사용했습니다.")
                    } else {
                        alert("힌트를 보기 위해서\n광고를 보시겠어요?") {
                            yesButton {
                                if (mRewardedVideoAd.isLoaded) {
                                    mRewardedVideoAd.show()
                                }
                            }
                            noButton { }
                        }.show()
                    }
                }
        )

        compositeDisposable.add(confirm_btn.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (quizList[currentQuestion + passCount].solution.replace(" ", "") == answer_text.text.toString().replace(" ", "")) {
                        currentQuestion++
                        clearHint()
                        changeQuiz()
                        answer_text.setText("")
                        toast("정답입니다.")
                    } else {
                        if (--heart <= 0)
                            (activity as MainActivity).gameOver("$currentQuestion")
                        toast("틀렸습니다.")
                        answer_text.setText("")
                        Glide.with(context!!)
                                .load(R.drawable.heart_broken_icon)
                                .into(heartImgArray[heart])
                    }
                })

        compositeDisposable.add(pass_view.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    passCount++
                    clearHint()
                    changeQuiz()
                })
    }

    private fun getQuiz() {

    }


    private fun clearHint() {
        hintCount = 3
        hintImgArray.forEach {
            Glide.with(context!!)
                    .load(R.drawable.hint_icon)
                    .into(it)
        }
        hintViewArray.forEach {
            it.visibility = View.GONE
        }
    }

    private fun changeQuiz() {
        if (currentQuestion + passCount == quizList.size) {
            getQuiz()
        } else {
            order_text.text = "Q${currentQuestion + passCount + 1}."
            quizList[currentQuestion + passCount].run {
                category_text.text = category
                Glide.with(context!!)
                        .load("https://nuclear-insider-game-server.herokuapp.com//$imagePath")
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .into(emoji_view)
                hintList = hints
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(context)
    }

    override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRewardedVideoAd.rewardedVideoAdListener = null
        mRewardedVideoAd.destroy(context)
    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(ADMOB_APP_KEY, AdRequest.Builder().build())
    }
}