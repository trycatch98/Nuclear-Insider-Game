package com.depromeet.tmj.nuclear_insider_game.shared

import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener

class RewardAdListener: RewardedVideoAdListener {
    private var listener: AdListener? = null

    override fun onRewardedVideoAdClosed() {
        // 다음 광고 로딩
        listener?.onRewardedVideoAdClosed()
    }

    override fun onRewardedVideoAdLeftApplication() {
    }

    override fun onRewardedVideoAdLoaded() {
    }

    override fun onRewardedVideoAdOpened() {
    }

    override fun onRewardedVideoCompleted() {
    }

    override fun onRewarded(rewardItem: RewardItem?) {
        listener?.onRewarded()
    }

    override fun onRewardedVideoStarted() {
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
    }

    fun setAdListener(listener: AdListener) {
        this.listener = listener
    }

    interface AdListener {
        fun onRewardedVideoAdClosed()

        fun onRewarded()
    }
}