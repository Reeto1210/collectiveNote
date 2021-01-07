package com.mudryakov.collectivenote.di

import android.app.Application
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.UserManager.AppUserManager
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
import com.mudryakov.collectivenote.database.RoomDatabase.myDao
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.screens.emailLogin.EmailLoginFragment
import com.mudryakov.collectivenote.screens.mainScreen.MainFragmentViewModel
import com.mudryakov.collectivenote.screens.singleUserPayments.SinglePaymentAdapter
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import com.mudryakov.collectivenote.utility.CROP_IMAGE_SIZE
import com.theartofdev.edmodo.cropper.CropImage
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseAuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

@Module
class CropModule{
    @Provides
fun providesCropper():CropImage.ActivityBuilder =  CropImage.activity()
        .setAllowFlipping(false)
        .setAllowRotation(false)
        .setCropMenuCropButtonTitle(APP_ACTIVITY.getString(R.string.continue_it))
        .setAspectRatio(1, 1)
        .setRequestedSize(CROP_IMAGE_SIZE, CROP_IMAGE_SIZE)

}

@Module
class DaoModule{
    @Provides
    fun providesDao():myDao = MyRoomDatabase.getDatabase(MyApplication.thisApp).getDao()
}


@Module(subcomponents = [SinglePaymentSubComponent::class,HistorySubCoponent::class])
class AppSubComponent

@Module
abstract class RepositoryModule{
    @Binds
abstract fun getRepository(firebaseRepository:FireBaseRepository):AppDatabaseRepository
}