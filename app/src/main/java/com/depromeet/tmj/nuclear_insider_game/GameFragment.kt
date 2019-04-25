package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
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
import com.jakewharton.rxbinding3.view.clicks
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.fragment_game.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        MobileAds.initialize(context, ADMOB_APP_ID)
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
                } else {
                    toast("광고가 로드되지 않았습니다.")
                    loadRewardedVideoAd()
                    presenter.onRewarded()
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

        compositeDisposable.add(btn_share_kakao.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    sendKakaoLink()
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

    private fun sendKakaoLink() {
        // 카카오톡 링크 공유
        val params = FeedTemplate
                .newBuilder(ContentObject.newBuilder(
                        "카테고리 : " + category_text.text.toString(),
                        "",
                        LinkObject.newBuilder()
                                .setWebUrl("https://play.google.com/store/apps/details?id=com.depromeet.tmj.nuclear_insider_game")
                                .setMobileWebUrl("https://play.google.com/store/apps/details?id=com.depromeet.tmj.nuclear_insider_game").build())
                        .setDescrption(emoji_view.text.toString() + "\n" + "문제의 정답을 알고싶다면 '뜻밖의 게임'으로 ")
                        .build())
                .build()
        context?.let { context ->
            KakaoLinkService.getInstance().sendDefault(context, params, object : ResponseCallback<KakaoLinkResponse>() {
                override fun onSuccess(result: KakaoLinkResponse?) {
                    toast("퀴즈를 공유합니다.")
                }

                override fun onFailure(errorResult: ErrorResult?) {
                    toast(errorResult!!.errorMessage)
                }
            })
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