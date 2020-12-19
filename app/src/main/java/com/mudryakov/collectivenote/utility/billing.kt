package com.mudryakov.collectivenote.utility

import com.android.billingclient.api.*
import com.mudryakov.collectivenote.R


lateinit var myBillingClient: BillingClient
var mySkuDetailMap:HashMap<String,SkuDetails> = HashMap()

fun makeDonation(onSuccess: () -> Unit) {
    val purchasesUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            onSuccess()
    }

    val myBillingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(p0: BillingResult) {
            if (p0.responseCode ==  BillingClient.BillingResponseCode.OK){
                println("ok1")
                querySkuDetails()
            }
        }

        override fun onBillingServiceDisconnected() {
           myBillingClient.endConnection()
            showToast(R.string.something_going_wrong)
        }

    }

     myBillingClient = BillingClient.newBuilder(APP_ACTIVITY)
        .setListener(purchasesUpdateListener)
        .enablePendingPurchases()
        .build()
    myBillingClient.startConnection(myBillingClientStateListener)


}

private fun querySkuDetails() {
    val skuDetailsParamsBuilder = SkuDetailsParams.newBuilder()
    val skuList: MutableList<String> = mutableListOf("sku_donate")
    skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
    myBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build()) { billingResult, skuDetailsList ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK&& skuDetailsList !=null){
            println("ok2")
            for (skuDetails in skuDetailsList) {
                mySkuDetailMap[skuDetails.sku] = skuDetails
                println("ok3")
                println(mySkuDetailMap[skuDetails.sku])
            }
        }
        val  billingFlowParams = mySkuDetailMap["sku_donate"]?.let {
            BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()
        }

        if (billingFlowParams != null) {
            myBillingClient.launchBillingFlow(APP_ACTIVITY, billingFlowParams)
        }
        println("ok4")

    }

}







































