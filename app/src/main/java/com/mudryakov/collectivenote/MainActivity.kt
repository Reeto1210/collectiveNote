package com.mudryakov.collectivenote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mudryakov.collectivenote.databinding.ActivityMainBinding
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.SIGN_CODE_REQUEST
import com.mudryakov.collectivenote.utilits.appPreference
import com.mudryakov.collectivenote.utilits.handleSignInresult

class MainActivity : AppCompatActivity() {
    var _Binding: ActivityMainBinding? = null
    val mBinding get() = _Binding!!
    lateinit var navConroller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.mainToolbar)
        title = getString(R.string.common_app_name)
        APP_ACTIVITY = this
        navConroller = Navigation.findNavController(APP_ACTIVITY, R.id.container)
    }

    override fun onStart() {
        super.onStart()
        appPreference.getPreference(APP_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_CODE_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInresult(task)
        }
    }
}