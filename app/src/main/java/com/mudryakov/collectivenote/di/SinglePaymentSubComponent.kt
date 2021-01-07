package com.mudryakov.collectivenote.di

import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.screens.singleUserPayments.SinglePaymentAdapter
import com.mudryakov.collectivenote.screens.singleUserPayments.SingleUserPaymentsFragment
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Subcomponent()
interface SinglePaymentSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SinglePaymentSubComponent
    }

    fun inject(singlePaymentFragment: SingleUserPaymentsFragment)
}


