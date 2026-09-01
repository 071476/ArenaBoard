package com.ali.arenaboard.core.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {

    private const val BANNER_ID = "ca-app-pub-6032223146401152/3723551721"
    private const val REWARDED_ID = "ca-app-pub-6032223146401152/7042131008"

    private var rewardedAd: RewardedAd? = null

    fun loadBanner(adView: AdView) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, REWARDED_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
            }
        })
    }

    fun showRewardedAd(activity: Activity, onReward: () -> Unit) {
        val ad = rewardedAd
        if (ad != null) {
            ad.show(activity) {
                onReward()
                loadRewardedAd(activity)
            }
        } else {
            onReward()
            loadRewardedAd(activity)
        }
    }

    fun isRewardedReady(): Boolean = rewardedAd != null
}
