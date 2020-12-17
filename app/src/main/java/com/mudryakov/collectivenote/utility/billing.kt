package com.mudryakov.collectivenote.utility

import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext


fun makeDonation(){
     val purchasesUpdateListener = PurchasesUpdatedListener{ billingResult, purchases ->
        //someFun
    }
   val billingClient:BillingClient = BillingClient.newBuilder(APP_ACTIVITY)
        .setListener(purchasesUpdateListener)
        .enablePendingPurchases()
        .build()

  //  val myBillingClientStateListener = object:BillingClientStateListener{
     //   override fun onBillingSetupFinished(p0: BillingResult) {
           // if(p0.responseCode == BillingClient.BillingResponseCode.OK)
         //      startDonataionQuery(billingClient)
      //  }



      //  override fun onBillingServiceDisconnected() {

      //  }

  //  }

}

 fun startDonataionQuery() {
    TODO("Not yet implemented")
}
// fun querySkuDetails(billingClient:BillingClient){
 //   val skuList = mutableListOf("donation")
  //  val params = SkuDetailsParams.newBuilder()
   // params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
   // withContext(IO){
     //   billingClient.querySkuDetailsAsync(params.build()){ billingResult, mutableList -> }
   // }
//}
