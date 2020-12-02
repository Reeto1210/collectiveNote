package com.mudryakov.collectivenote.screens.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.AUTH
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.USERNAME
import com.mudryakov.collectivenote.databinding.FragmentLoginBinding

import com.mudryakov.collectivenote.utilits.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard


class LoginFragment : Fragment() {
    var _Binding: FragmentLoginBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: LoginViewModel
    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentLoginBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _Binding = null
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = getString(R.string.login_toolbar_title)
        initialization()
        initHomeUp()
        animateButtons()
        internetCheck()
    }

    private fun initHomeUp() {
        APP_ACTIVITY.actionBar?.setDisplayHomeAsUpEnabled(false)
        APP_ACTIVITY.back = false
    }

    private fun internetCheck() {
        if (appPreference.getSignIn()) {
            CURRENT_UID = appPreference.getUserId()
            fastNavigate(R.id.action_loginFragment_to_roomChooseFragment)
        } else {
            if (!checkConnectity()) {
                buildNoInternetDialog { internetCheck() }
            } else {
                if (AUTH.currentUser != null) {
                   showChangeNameLayout()
                }
                mBinding.loginNoAccount.setOnClickListener { fastNavigate(R.id.action_loginFragment_to_emailRegistrationFragment) }
                emailBtnClick()
                googleSighbtnClick()
            }
        }
    }

    private fun showChangeNameLayout() {
        mBinding.firstLogin.makeGone()
        changeName { fastNavigate(R.id.action_loginFragment_to_roomChooseFragment)}
    }

    private fun animateButtons() {
         if (AUTH.currentUser == null) {
             mBinding.loginEmailBtn.startRegisterAnimation(false)
             mBinding.loginChangeName.startRegisterAnimation(false)
             val keyBoardListener =
                 object : KeyboardVisibilityEventListener {
                     override fun onVisibilityChanged(isOpen: Boolean) {
                         if (isOpen) {
                             mBinding.loginBtnSignInGoogle.startRegisterAnimation(false)
                             mBinding.loginTip.startRegisterAnimation(false)
                             mBinding.loginEmailBtn.startRegisterAnimation(true)
                             mBinding.loginNoAccount.startRegisterAnimation(false)
                         } else {
                             mBinding.loginBtnSignInGoogle.startRegisterAnimation(true)
                             mBinding.loginTip.startRegisterAnimation(true)
                             mBinding.loginEmailBtn.startRegisterAnimation(false)
                             mBinding.loginNoAccount.startRegisterAnimation(true)
                         }
                     }
                 }
             KeyboardVisibilityEvent.setEventListener(
                 APP_ACTIVITY,
                 viewLifecycleOwner,
                 keyBoardListener
             )
         }
    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        mViewModel.initCommons()
    }

    private fun emailBtnClick() {
        mBinding.loginEmailBtn.setOnClickListener {
            if (!isLoading) {
                val email = mBinding.loginInputEmail.text.toString()
                val pass = mBinding.loginInputPassword.text.toString()
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    showProgressBar()
                    mViewModel.login(
                        TYPE_EMAIL,
                        email,
                        pass,
                        { onloginFail() }) {
                        onRegisterSuccess()
                    }
                } else showToast(getString(R.string.add_info))
            }
        }
    }

    private fun googleSighbtnClick() {
        mBinding.loginBtnSignInGoogle.setOnClickListener {
            if (!isLoading) {
                showProgressBar()
                mViewModel.login(
                    TYPE_GOOGLE_ACCOUNT,
                    onFail = { onloginFail() }) {
                    onRegisterSuccess()
                }
            }
        }
    }

    private fun onRegisterSuccess() {
        hideKeyboard(APP_ACTIVITY)
        onloginFail()
        changeName {
            fastNavigate(R.id.action_loginFragment_to_roomChooseFragment)
        }
    }

    private fun changeName(onConfirm: () -> Unit) {
        mBinding.loginChangeNameInputText.setText(USERNAME)
        mBinding.firstLogin.startRegisterAnimation(false)
        mBinding.loginChangeName.startRegisterAnimation(true)
        mBinding.loginChangeNameBtn.setOnClickListener {
            val newName = mBinding.loginChangeNameInputText.text.toString()
            if (newName.isNotEmpty()) {
                if (!isLoading) {
                    showProgressBar()
                    mViewModel.changeName(newName) { onConfirm() }
                }
            } else
                showToast(getString(R.string.name_cant_be_empty_toast))
        }
    }

    private fun onloginFail() {
        isLoading = false
        mBinding.fragmentLoginProgressBar.makeGone()
    }

    private fun showProgressBar() {
        hideKeyboard(APP_ACTIVITY)
        isLoading = true
        mBinding.fragmentLoginProgressBar.makeVisible()
    }
}