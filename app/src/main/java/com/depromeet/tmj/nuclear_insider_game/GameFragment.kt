package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.depromeet.tmj.nuclear_insider_game.shared.*
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

class GameFragment : BaseFragment() {
    private val database = FirebaseDatabase.getInstance()
    private val quizList = arrayListOf<QuizDataModel>()
    private lateinit var hintList: List<String>
    private lateinit var hintTextList: MutableList<AppCompatTextView>
    private lateinit var hintImgList: MutableList<AppCompatImageView>
    private lateinit var heartImgList: MutableList<AppCompatImageView>
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var currentQuiz: QuizDataModel
    private var hintCount = 3
    private var heart = 5
    private var currentQuestion = 0
    private var passCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hintTextList = mutableListOf(hint_text1, hint_text2, hint_text3)
        hintImgList = mutableListOf(hint_img1, hint_img2, hint_img3)
        heartImgList = mutableListOf(heart_img1, heart_img2, heart_img3, heart_img4, heart_img5)
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
                hintTextList[3 - hintCount].run {
                    text = hintList[3 - hintCount]
                    visibility = View.VISIBLE
                }
                Glide.with(context!!)
                        .load(R.drawable.hint_broken_icon)
                        .into(hintImgList[--hintCount])
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

    private fun setQuiz() {
        if (quizList.size == 0) {
            (activity as MainActivity).gameFinish("$currentQuestion")
        } else {
            currentQuiz = quizList.removeAt(Random().nextInt(quizList.size))

            category_text.text = currentQuiz.category
            emoji_view.text = currentQuiz.problem
            hintList = currentQuiz.hints
            order_text.text = String.format("Q%d.", currentQuestion + passCount + 1)

            hintCount = 3
            hintImgList.forEach {
                Glide.with(context!!)
                        .load(R.drawable.hint_icon)
                        .into(it)
            }
            hintTextList.forEach {
                it.visibility = View.GONE
            }
        }
    }

    private fun initUi() {
        setQuiz()
        compositeDisposable.add(hint_btn.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (hintCount <= 0) {
                        toast("힌트 기회를 모두 사용했습니다.")
                    } else {
                        alert("힌트를 보기 위해서\n광고를 보시겠어요?") {
                            yesButton {
                                if (rewardedVideoAd.isLoaded) {
                                    rewardedVideoAd.show()
                                }
                            }
                            noButton { }
                        }.show()
                    }
                }
        )

        compositeDisposable.add(confirm_btn.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .map { currentQuiz.solution == StringUtils.removeSpace(answer_text.text.toString()) }
                .subscribe { isRighted ->
                    if (isRighted) {
                        currentQuestion++
                        setQuiz()
                        answer_text.setText("")
                        toast("정답입니다.")
                    } else {
                        if (--heart <= 0) {
                            (activity as MainActivity).gameOver("$currentQuestion")
                        }
                        toast("틀렸습니다.")
                        answer_text.setText("")
                        Glide.with(context!!)
                                .load(R.drawable.heart_broken_icon)
                                .into(heartImgList[heart])
                    }
                })

        compositeDisposable.add(pass_view.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    passCount++
                    setQuiz()
                })
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
        rewardedVideoAd.loadAd(ADMOB_APP_KEY, AdRequest.Builder().build())
    }

    private fun getQuiz() {
        val quizReference = database.getReference(SCHEMA_QUIZ)

        quizReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children

                children.forEach { snapshot ->
                    val quiz = snapshot.getValue(QuizDataModel::class.java)
                    quiz?.let { quizList.add(it) }
                }
                initUi()
            }
        })
    }
}