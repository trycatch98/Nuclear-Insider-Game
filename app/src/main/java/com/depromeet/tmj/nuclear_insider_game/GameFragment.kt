package com.depromeet.tmj.nuclear_insider_game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_game.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class GameFragment : Fragment(), RewardedVideoAdListener {

    private val hintArray = listOf(
            "#게임",
            "#레오나르도디카프리오",
            "#영화"
    )
    private val hintViewArray = mutableListOf<AppCompatTextView>()
    private val hintImgArray = mutableListOf<AppCompatImageView>()
    private var hintCount = 3
    private lateinit var mRewardedVideoAd: RewardedVideoAd


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        emoji_view.imageResource = R.drawable.app_logo

        hintViewArray.add(hint_text1)
        hintViewArray.add(hint_text2)
        hintViewArray.add(hint_text3)

        hintImgArray.add(hint_img1)
        hintImgArray.add(hint_img2)
        hintImgArray.add(hint_img3)

        hint_btn.clicks()
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

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()

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
            text = hintArray.get(3 - hintCount)
            visibility = View.VISIBLE
        }
        hintImgArray.get(--hintCount).imageResource = R.drawable.hint_broken_icon
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