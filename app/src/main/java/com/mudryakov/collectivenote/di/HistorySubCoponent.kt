package com.mudryakov.collectivenote.di

import com.mudryakov.collectivenote.screens.history.HistoryFragment
import com.mudryakov.collectivenote.screens.history.HistoryRecyclerAdapter
import com.mudryakov.collectivenote.screens.singleUserPayments.SinglePaymentAdapter
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton

@Subcomponent()
interface HistorySubCoponent {

    @Subcomponent.Factory
    interface Factory{
        fun create():HistorySubCoponent

    }

    fun inject(historyFragment: HistoryFragment)
}

