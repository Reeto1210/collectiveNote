package com.mudryakov.collectivenote.di

import android.app.Application
import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.screens.addNewPayment.NewPaymentFragment
import com.mudryakov.collectivenote.screens.emailLogin.EmailLoginFragment
import com.mudryakov.collectivenote.screens.groupChoose.GroupChooseFragment
import com.mudryakov.collectivenote.screens.groupInfo.GroupInfoFragment
import com.mudryakov.collectivenote.screens.help.HelpFragment
import com.mudryakov.collectivenote.screens.mainScreen.MainFragment
import com.mudryakov.collectivenote.screens.registration.RegistrationFragment
import com.mudryakov.collectivenote.screens.settings.SettingsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Component(modules = [FirebaseAuthModule::class, CropModule::class, AppSubComponent::class, DaoModule::class, RepositoryModule::class])
@Singleton
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }


    fun getSinglePaymentSub(): SinglePaymentSubComponent.Factory
    fun getHistorySub(): HistorySubCoponent.Factory
    fun inject(act: MainActivity)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: GroupChooseFragment)
    fun inject(fragment: MainFragment)
    fun inject(login: EmailLoginFragment)
    fun inject(newPaymentFragment: NewPaymentFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(groupInfo: GroupInfoFragment)
    fun inject(helpFragmento: HelpFragment)

}


