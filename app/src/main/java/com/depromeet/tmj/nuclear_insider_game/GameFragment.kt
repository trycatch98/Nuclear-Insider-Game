package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_game.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

class GameFragment : Fragment(), RewardedVideoAdListener, AnkoLogger {
    private val api by lazy {
        Api.create()
    }
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
    private lateinit var nickname: String

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

        error { idArray.toString().substring(1, idArray.toString().length - 1) }
        api.getQuiz(idArray.toString().substring(1, idArray.toString().length - 1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    startQuiz()
                }
                .flatMap {
                    it.toObservable()
                }
                .subscribe({
                    idArray.add(it.id)
                    quizList.add(it)
                }){
                    it.printStackTrace()
                }

        MobileAds.initialize(context, "ca-app-pub-3940256099942544/5224354917")
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()
    }

    private fun startQuiz() {
        changeQuiz()
        hint_btn.clicks()
                .map {
                    if(hintCount<=0)
                        toast("힌트가 없습니다.")
                }
                .filter {
                    hintCount > 0
                }
                .subscribe({
                    alert("힌트를 보기 위해서\n광고를 보시겠어요?") {
                        yesButton {
                            if (mRewardedVideoAd.isLoaded) {
                                mRewardedVideoAd.show()
                            }
                        }
                        noButton {  }
                    }.show()
                }){
                    it.printStackTrace()
                }

        confirm_btn.clicks()
                .subscribe({
                    if(quizList[currentQuestion].solution == answer_text.text.toString()) {
                        currentQuestion++
                        hintCount = 3
                        changeQuiz()
                        answer_text.setText("")
                        toast("정답입니다.")
                    }
                    else {
                        if(--heart <= 0)
                            (activity as MainActivity).gameOver()
                        toast("틀렸습니다.")
                        answer_text.setText("")
                        Glide.with(context!!)
                                .load(R.drawable.heart_broken_icon)
                                .into(heartImgArray[heart])
                    }
                }){
                    it.printStackTrace()
                }
    }

    private fun getQuiz() =
            api.getQuiz("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        changeQuiz()
                    }
                    .doOnError {
                        (activity as MainActivity).gameOver("${currentQuestion + 1}")
                    }
                    .flatMap {
                        if(it.isEmpty())
                            throw RuntimeException("end of quiz")
                        it.toObservable()
                    }
                    .subscribe({
                        idArray.add(it.id)
                        quizList.add(it)
                    }){
                        it.printStackTrace()
                    }

    private fun changeQuiz(){
        order_text.text = "Q${currentQuestion + 1}."
        quizList[currentQuestion].run {
            category_text.text = category
            Glide.with(context!!)
                    .load("http://119.194.163.190:8080/$imagePath")
                    .into(emoji_view)
            hintList = hints
        }
    }

    private fun initBtn() {
        hint_btn.clicks()
                .map {
                    if (--heart <= 0)
                        (activity as MainActivity).gameOver("${currentQuestion + 1}")
                    toast("틀렸습니다.")
                    answer_text.setText("")
                    Glide.with(context!!)
                }
        pass_view.clicks()
                .subscribe({
                    passCount++
                    changeQuiz()
                }){
                    it.printStackTrace()
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

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdLeftApplication() {
    }

    override fun onRewardedVideoAdLoaded() {
    }

    override fun onRewardedVideoAdOpened() {
    }

    override fun onRewardedVideoCompleted() {
    }

    override fun onRewarded(p0: RewardItem?) {
        hintViewArray.get(3 - hintCount).run {
            text = hintList[3 - hintCount]
            visibility = View.VISIBLE
        }
        Glide.with(context!!)
                .load(R.drawable.hint_broken_icon)
                .into(hintImgArray[--hintCount])
    }

    override fun onRewardedVideoStarted() {
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                AdRequest.Builder().build())
    }
}